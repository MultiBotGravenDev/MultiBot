package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.data.AntiRoleData;
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
import java.util.List;
import java.util.Map;

public class AntiCommand implements CommandExecutor {

    private final GuildIdDAO guildIdDAO;
    private final AntiRolesDAO antiRolesDAO;

    public AntiCommand(DAOManager daoManager) {
        this.guildIdDAO = daoManager.getGuildIdDAO();
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
        boolean isCommandInvalid = args.length != 2 ||
                mentionedMembers.size() != 1 ||
                !"repost review meme".contains(args[0]) ||
                GuildUtils.hasRole(mentionedMembers.get(0), "anti-" + args[0]);

        if (isCommandInvalid) {
            return;
        }

        Member member = mentionedMembers.get(0);
        Guild guild = message.getGuild();
        Role role = guild.getRoleById(guildIdDAO.get("anti-" + args[0]).id);
        if (role == null) {
            return;
        }

        guild.addRoleToMember(member, role).queue(unused -> {

            saveInDatabase(args[0], member);

            long duration = 60 * 60 * 24 * 30 * 6; // 6 months hardcoded here
            TextChannel logsTextChannel = guild.getTextChannelById(guildIdDAO.get("logs").id);
            if (logsTextChannel == null) {
                return;
            }
            logsTextChannel.sendMessage(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setTitle("[ANTI-" + args[0].toUpperCase() + "] " + member.getUser().getAsTag())
                    .addField("Utilisateur :", member.getAsMention(), true)
                    .addField("Modérateur :", message.getAuthor().getAsMention(), true)
                    .addField("Fin :", Utils.getDateFormat().format(Date.from(Instant.now().plusSeconds(duration))), true)
                    .build()).queue();

        });

    }

    private Date getCurrentDate() {
        long currentTimeMillis = System.currentTimeMillis();
        return new Date(currentTimeMillis);
    }

    private void saveInDatabase(String roleName, Member member) {
        User sanctionedUser = member.getUser();
        String sanctionedUserId = sanctionedUser.getId();
        AntiRoleData antiRoleData = antiRolesDAO.get(sanctionedUserId);
        Map<java.util.Date, String> sanctionedUserRoles = antiRoleData.getRoles();
        String antiRoleName = "anti-" + roleName;
        Date currentDate = getCurrentDate();

        sanctionedUserRoles.put(currentDate, antiRoleName);
        antiRolesDAO.save(antiRoleData);
    }
}
