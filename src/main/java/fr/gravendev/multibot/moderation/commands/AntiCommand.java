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
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        return "Permet de mettre un role anti- (meme, repost...) à une personne pour une durée de 6 mois.";
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

        List<Member> mentionedMembers = message.getMentionedMembers();

        if (args.length < 3 || mentionedMembers.size() == 0 || !"repost review meme".contains(args[0])) {
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "<repost,review,meme> @membre <raison>")).queue();
            return;
        }

        if (GuildUtils.hasRole(mentionedMembers.get(0), "anti-" + args[0])) {
            message.getChannel().sendMessage("Ce membre possède déjà le rôle anti-" + args[0]).queue();
            return;
        }

        Member member = mentionedMembers.get(0);
        Guild guild = message.getGuild();
        Role role = guild.getRoleById(Configuration.getConfigByName("anti_" + args[0]).getValue());
        if (role == null) {
            return;
        }

        guild.addRoleToMember(member, role).queue(unused -> {

            saveInDatabase(args[0], member);

            TextChannel logsTextChannel = guild.getTextChannelById(Configuration.LOGS.getValue());
            if (logsTextChannel == null) {
                return;
            }

            message.getChannel().sendMessage("Le rôle anti-" + args[0] + " a bien été attribué.").queue();

            logsTextChannel.sendMessage(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setTitle("[ANTI-" + args[0].toUpperCase() + "] " + member.getUser().getAsTag())
                    .addField("Utilisateur :", member.getAsMention(), true)
                    .addField("Modérateur :", message.getAuthor().getAsMention(), true)
                    .addField("Fin :", Utils.getDateFormat().format(Date.from(Instant.now().plus(31, ChronoUnit.DAYS))), true)
                    .build()).queue();

            String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            warn(message, reason);

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

    private void warn(Message message, String reason) {
        Member member = message.getMentionedMembers().get(0);
        Guild guild = message.getGuild();

        InfractionData data = new InfractionData(member.getId(), member.getId(), InfractionType.WARN, reason, new java.util.Date(), null);

        infractionsDAO.save(data);
        String logs = Configuration.LOGS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[WARN] " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                .addField("Utilisateur:", member.getAsMention(), true)
                .addField("Modérateur:", message.getJDA().getSelfUser().getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if (logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }

        message.getChannel().sendMessage(Utils.getWarnEmbed(member.getUser(), reason)).queue();
    }

}
