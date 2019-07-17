package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.SQLException;
import java.util.List;

public class ResponsesCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    ResponsesCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "responses";
    }

    @Override
    public void execute(Message message, String[] args) {

        List<TextChannel> mentionedChannels = message.getMentionedChannels();
        if (mentionedChannels.size() != 1) return;

        try {
            TextChannel textChannel = mentionedChannels.get(0);
            new GuildIdDAO(this.databaseConnection.getConnection())
                    .save(new GuildIdsData("candids", textChannel.getIdLong()));
            message.getChannel().sendMessage("Le nouveau salon pour envoyer les candidatures a bien été définis à : " + textChannel.getAsMention()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
