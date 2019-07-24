package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class CandidsExecutor implements EmoteAddedExecutor {

    private final GuildIdDAO guildIdDAO;

    public CandidsExecutor(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }

    @Override
    public long getSaloonId() {
        return this.guildIdDAO.get("candids").id;
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        long memberRoleId = this.guildIdDAO.get("member").id;

        event.getChannel().getMessageById(event.getMessageIdLong()).queue(message -> {

            Member member = message.getMentionedMembers().get(0);
            String validationMessage = member.getAsMention() + "\n\n";

            switch (event.getReactionEmote().getName()) {

                case "\u2705":
                    validationMessage += ":white_check_mark: accepté ";
                    GuildUtils.addRole(member, String.valueOf(memberRoleId)).queue();
                    break;

                case "\u274C":
                    validationMessage += ":x: refusé ";
                    break;

                default:
                    event.getReaction().removeReaction(event.getUser()).queue();
                    return;

            }

            validationMessage += "par " + event.getMember().getAsMention();

            message.clearReactions().queue();

            message.editMessage(new MessageBuilder(message)
                    .setContent(validationMessage)
                    .build()).queue();

        });

    }

}
