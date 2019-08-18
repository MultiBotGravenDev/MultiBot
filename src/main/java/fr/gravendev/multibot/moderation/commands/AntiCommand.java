package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.sql.Date;
import java.time.Instant;

import java.util.List;
import java.util.Map;

public class AntiCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public AntiCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "anti";
    }

    // TODO When duration will be putted into config file don't forget to change text here
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
        if (args.length != 2 ||
            mentionedMembers.size() != 1 ||
            !args[0].contains("repost review meme") || // TODO WTF?? Check it it seems to be an error
            GuildUtils.hasRole(mentionedMembers.get(0), "anti-" + args[0]))
        {
            return;
        }

        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);

        Member member = mentionedMembers.get(0);
        Guild guild = message.getGuild();
        guild.getController().addSingleRoleToMember(member, guild.getRoleById(guildIdDAO.get("anti_" + args[0]).id)).queue(unused -> {

            saveInDatabase(args[0], member);

            long duration = 60 * 60 * 24 * 30 * 6; // 6 months hardcoded here
            guild.getTextChannelById(guildIdDAO.get("logs").id).sendMessage(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setTitle("[ANTI-" + args[0].toUpperCase() + "] " + member.getUser().getAsTag())
                    .addField("Utilisateur :", member.getAsMention(), true)
                    .addField("Modérateur :", message.getAuthor().getAsMention(), true)
                    .addField("Fin :", Utils.getDateFormat().format(Date.from(Instant.now().plusSeconds(duration))), true)
                    .build()).queue();

        });

    }

    private Date getCurrentDate(){
        long currentTimeMillis = System.currentTimeMillis();
        return new Date(currentTimeMillis);
    }

    private void saveInDatabase(String roleName, Member member) {
        AntiRolesDAO antiRolesDAO = new AntiRolesDAO(databaseConnection);
        User sanctionedUser = member.getUser();
        String sanctionedUserId = sanctionedUser.getId();
        AntiRoleData antiRoleData = antiRolesDAO.get(sanctionedUserId);
        Map<java.util.Date, String> sanctionedUserRoles = antiRoleData.roles;
        String antiRoleName = "anti-" + roleName;
        Date currentDate = getCurrentDate();

        sanctionedUserRoles.put(currentDate, antiRoleName);
        antiRolesDAO.save(antiRoleData);
    }
}
