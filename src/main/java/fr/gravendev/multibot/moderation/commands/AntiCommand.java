package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import fr.gravendev.multibot.database.data.AntiRoleData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class AntiCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public AntiCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "anti";
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

        if (args.length == 0) return;
        if (mentionedMembers.size() != 1) return;
        if (!"repost review meme".contains(args[0])) return;

        GuildIdDAO guildIdDAO = new GuildIdDAO(this.databaseConnection);
        long roleId = guildIdDAO.get("anti_" + args[0]).id;
        long logsId = guildIdDAO.get("logs").id;

        Member member = mentionedMembers.get(0);
        message.getGuild().getController().addRolesToMember(member, message.getGuild().getRoleById(roleId)).queue(a -> {

            AntiRolesDAO antiRolesDAO = new AntiRolesDAO(this.databaseConnection);
            AntiRoleData antiRoleData = antiRolesDAO.get(member.getUser().getId());

            if (antiRoleData != null) return;
            antiRoleData = new AntiRoleData(member.getUser().getIdLong(), new HashMap<>());


            antiRoleData.roles.put(new Date(System.currentTimeMillis()), "anti-" + args[0]);

            antiRolesDAO.save(antiRoleData);

            message.getGuild().getTextChannelById(logsId).sendMessage(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setTitle("[ANTI-" + args[0].toUpperCase() + "]" + member.getUser().getAsTag())
                    .addField("Utilisateur :", member.getAsMention(), true)
                    .addField("Modérateur :", message.getAuthor().getAsMention(), true)
                    .addField("Fin :", new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss").format(Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 30 * 6))), true)
                    .build()).queue();

        });

    }

}
