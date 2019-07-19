package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.TimerTask;

public class AntiRolesTask extends TimerTask {

    private final JDA jda;
    private final DatabaseConnection databaseConnection;

    public AntiRolesTask(JDA jda, DatabaseConnection databaseConnection) {
        this.jda = jda;
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void run() {

        System.out.println("ok");

        try {
            GuildIdDAO guildIdDAO = new GuildIdDAO(this.databaseConnection.getConnection());

            long antiRepostId = guildIdDAO.get("anti_repost").id;
            long antiMemeId = guildIdDAO.get("anti_meme").id;
            long antiReviewId = guildIdDAO.get("anti_review").id;

            Guild guild = this.jda.getGuildById(guildIdDAO.get("guild").id);

            AntiRolesDAO antiRoleDAO = new AntiRolesDAO(this.databaseConnection.getConnection());

            guild.getMembersWithRoles(guild.getRoleById(antiRepostId)).stream()
                    .map(member -> antiRoleDAO.get(member.getUser().getId()))
                    .filter(Objects::nonNull)
                    .forEach(antiRoleData -> {

                        boolean removeRole = antiRoleData.roles.entrySet().stream()
                                .filter(entry -> entry.getValue().contains("anti-repost"))
                                .anyMatch(entry -> entry.getKey().before(Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30 * 6))));

                        if (removeRole) {
                            guild.getController().removeRolesFromMember(guild.getMemberById(antiRoleData.userId), guild.getRoleById(antiRepostId)).queue();
                            antiRoleDAO.delete(antiRoleData);
                        }

                    });

            guild.getMembersWithRoles(guild.getRoleById(antiMemeId)).stream()
                    .map(member -> antiRoleDAO.get(member.getUser().getId()))
                    .filter(Objects::nonNull)
                    .forEach(antiRoleData -> {

                        System.out.println("ok1");

                        boolean removeRole = antiRoleData.roles.entrySet().stream()
                                .filter(entry -> entry.getValue().contains("anti-meme"))
                                .anyMatch(entry -> entry.getKey().before(Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30 * 6))));

                        System.out.println(removeRole);

                        if (removeRole) {
                            guild.getController().removeRolesFromMember(guild.getMemberById(antiRoleData.userId), guild.getRoleById(antiMemeId)).queue();
                            antiRoleDAO.delete(antiRoleData);
                        }

                    });

            guild.getMembersWithRoles(guild.getRoleById(antiReviewId)).stream()
                    .map(member -> antiRoleDAO.get(member.getUser().getId()))
                    .filter(Objects::nonNull)
                    .forEach(antiRoleData -> {

                        boolean removeRole = antiRoleData.roles.entrySet().stream()
                                .filter(entry -> entry.getValue().contains("anti-review"))
                                .anyMatch(entry -> entry.getKey().before(Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30 * 6))));

                        if (removeRole) {
                            guild.getController().removeRolesFromMember(guild.getMemberById(antiRoleData.userId), guild.getRoleById(antiReviewId)).queue();
                            antiRoleDAO.delete(antiRoleData);
                        }

                    });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
