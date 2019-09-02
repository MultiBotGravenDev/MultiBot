package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

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

        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {

            Member member = message.getMentionedMembers().get(0);
            String validationMessage = member.getAsMention() + "\n\n";

            switch (event.getReactionEmote().getName()) {

                case "\u2705":
                    validationMessage += ":white_check_mark: acceptée ";
                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre candidature a été acceptée ! Bienvenue sur GravenDev").queue());
                    GuildUtils.addRole(member, String.valueOf(memberRoleId)).queue();
                    break;

                case "\u274C":
                    validationMessage += ":x: refusée ";
                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre candidature a été refusée").queue());
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
