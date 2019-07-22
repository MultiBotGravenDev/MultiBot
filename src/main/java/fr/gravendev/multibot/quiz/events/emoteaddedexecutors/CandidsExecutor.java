package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class CandidsExecutor implements EmoteAddedExecutor {

    private final GuildIdDAO guildIdDAO;

    public CandidsExecutor(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }

    @Override
    public String getSaloon() {
        return "candids";
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        long memberRoleId = this.guildIdDAO.get("member").id;

        event.getChannel().getMessageById(event.getMessageIdLong()).queue(message -> {

            Member member = message.getMentionedMembers().get(0);
            String validationMessage = member.getAsMention() + "\n\n";

            if (event.getReactionEmote().getName().equals("\u2705")) {
                validationMessage += ":white_check_mark: accepté ";
                GuildUtils.addRole(member, String.valueOf(memberRoleId)).queue();
            } else {
                validationMessage += ":x: refusé ";
            }

            validationMessage += "par " + event.getMember().getAsMention();

            message.clearReactions().queue();

            message.editMessage(new MessageBuilder(message)
                    .setContent(validationMessage)
                    .build()).queue();

        });

    }

}
