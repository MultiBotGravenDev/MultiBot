package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.sql.SQLException;

public class CandidsExecutor implements EmoteAddedExecutor {

    private final DatabaseConnection databaseConnection;

    public CandidsExecutor(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getSaloon() {
        return "candids";
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        try {

            GuildIdDAO guildIdDAO = new GuildIdDAO(this.databaseConnection.getConnection());
            long memberRoleId = guildIdDAO.get("member").id;

            event.getChannel().getMessageById(event.getMessageIdLong()).queue(message -> {

                Member member = message.getMentionedMembers().get(0);
                String validationMessage = member.getAsMention() + "\n\n";

                if (event.getReactionEmote().getName().equals("\u2705")) {
                    validationMessage += ":white_check_mark: accepté ";
                    Guild guild = message.getGuild();
                    guild.getController().addRolesToMember(member, guild.getRoleById(memberRoleId)).queue();
                } else {
                    validationMessage += ":x: refusé ";
                }

                validationMessage += "par " + event.getMember().getAsMention();

                message.getReactions().forEach(messageReaction -> {
                    messageReaction.removeReaction().queue();
                    messageReaction.removeReaction(event.getUser()).queue();
                });

                message.editMessage(new MessageBuilder(message)
                        .setContent(validationMessage)
                        .build()).queue();

            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
