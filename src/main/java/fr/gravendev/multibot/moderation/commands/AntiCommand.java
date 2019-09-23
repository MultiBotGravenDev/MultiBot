package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.AntiRoleData;
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
import java.util.List;
import java.util.Map;

public class AntiCommand implements CommandExecutor {

    private final AntiRolesDAO antiRolesDAO;

    public AntiCommand(DAOManager daoManager) {
        this.antiRolesDAO = daoManager.getAntiRolesDAO();
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

        if(args.length < 2 || mentionedMembers.size() == 0) {
            message.getChannel().sendMessage("Usage: @membre repost | review | meme").queue();
            return;
        }

        if(!"repost review meme".contains(args[1])) {
            message.getChannel().sendMessage("Anti: repost | review | meme").queue();
            return;
        }

        if(GuildUtils.hasRole(mentionedMembers.get(0), "anti-" + args[1])) {
            message.getChannel().sendMessage("Ce membre possède déjà le rôle anti-"+args[1]).queue();
            return;
        }

        Member member = mentionedMembers.get(0);
        Guild guild = message.getGuild();
        Role role = guild.getRoleById(Configuration.getConfigByName("anti_" + args[1]).getValue());
        if (role == null) {
            return;
        }

        guild.addRoleToMember(member, role).queue(unused -> {

            saveInDatabase(args[1], member);

            TextChannel logsTextChannel = guild.getTextChannelById(Configuration.LOGS.getValue());
            if (logsTextChannel == null) {
                return;
            }

            message.getChannel().sendMessage("Le rôle anti-"+args[1]+" a bien été attribué.").queue();

            logsTextChannel.sendMessage(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setTitle("[ANTI-" + args[1].toUpperCase() + "] " + member.getUser().getAsTag())
                    .addField("Utilisateur :", member.getAsMention(), true)
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
}
