package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ChannelCommand implements CommandExecutor {

    private final GuildIdDAO guildIdDAO;

    ChannelCommand(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "channel";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("lisez-ce-salon", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        List<TextChannel> mentionedChannels = message.getMentionedChannels();
        if (mentionedChannels.size() != 1) return;

        TextChannel textChannel = mentionedChannels.get(0);
        this.guildIdDAO.save(new GuildIdsData("candids", textChannel.getIdLong()));
        message.getChannel().sendMessage("Le nouveau salon pour envoyer les candidatures a bien été définis à : " + textChannel.getAsMention()).queue();

    }

}
