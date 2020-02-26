package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.UserSearchUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class AntiCommand implements CommandExecutor {

    private final AntiRolesDAO antiRolesDAO;
    private final InfractionDAO infractionsDAO;

    public AntiCommand(DAOManager daoManager) {
        this.antiRolesDAO = daoManager.getAntiRolesDAO();
        this.infractionsDAO = daoManager.getInfractionDAO();
    }

    @Override
    public String getCommand() {
        return "anti";
    }

    @Override
    public String getDescription() {
        return "Permet de mettre un rôle anti- (meme, repost...) à une personne pour une durée de 6 mois.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {
        if (args.length < 3 || !"repost review meme".contains(args[0])) {
            MessageEmbed embed = Utils.errorArguments(getCommand(), "<repost,review,meme> @membre <raison>");
            message.getChannel().sendMessage(embed).queue();
            return;
        }

        Guild guild = message.getGuild();
        Optional<Member> opMember = UserSearchUtils.searchMember(guild, args[1]);

        if (!opMember.isPresent()) {
            UserSearchUtils.sendUserNotFound(message.getChannel());
            return;
        }

        Member targetMember = opMember.get();

        Role role = guild.getRoleById(Configuration.getConfigByName("anti_" + args[0]).getValue());
        if (role == null) {
            return;
        }

        if (targetMember.getRoles().contains(role)) {
            message.getChannel().sendMessage("Ce membre possède déjà le rôle anti-" + args[0]).queue();
            return;
        }

        guild.addRoleToMember(targetMember, role).queue(unused -> {

            saveInDatabase(args[0], targetMember);

            message.getChannel().sendMessage("Le rôle anti-" + args[0] + " a bien été attribué.").queue();

            String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            warn(targetMember, message.getChannel(), reason);

            TextChannel logsTextChannel = guild.getTextChannelById(Configuration.SANCTIONS.getValue());
            if (logsTextChannel == null) {
                return;
            }

            logsTextChannel.sendMessage(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setTitle("[ANTI-" + args[0].toUpperCase() + "] " + targetMember.getUser().getAsTag())
                    .addField("Utilisateur :", targetMember.getAsMention(), true)
                    .addField("Modérateur :", message.getAuthor().getAsMention(), true)
                    .addField("Fin :", Utils.getDateFormat().format(Date.from(Instant.now().plus(31, ChronoUnit.DAYS))), true)
                    .build()).queue();
        });

    }

    private void saveInDatabase(String roleName, Member member) {
        User sanctionedUser = member.getUser();
        String sanctionedUserId = sanctionedUser.getId();
        AntiRoleData antiRoleData = antiRolesDAO.get(sanctionedUserId);
        Map<java.util.Date, String> sanctionedUserRoles = antiRoleData.getRoles();
        String antiRoleName = "anti-" + roleName;
        java.util.Date currentDate = new java.util.Date();
        sanctionedUserRoles.put(currentDate, antiRoleName);
        antiRolesDAO.save(antiRoleData);
    }

    private void warn(Member member, MessageChannel channel, String reason) {
        Guild guild = member.getGuild();

        InfractionData data = new InfractionData(member.getId(), member.getId(), InfractionType.WARN, reason, new java.util.Date(), null);

        infractionsDAO.save(data);
        String logs = Configuration.SANCTIONS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[WARN] " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                .addField("Utilisateur:", member.getAsMention(), true)
                .addField("Modérateur:", guild.getJDA().getSelfUser().getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if (logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }

        channel.sendMessage(Utils.getWarnEmbed(member.getUser(), reason)).queue();
    }

}
