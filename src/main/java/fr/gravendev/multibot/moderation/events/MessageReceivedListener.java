package fr.gravendev.multibot.moderation.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.BadWordsDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Date;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final BadWordsDAO badWordsDAO;
    private final InfractionDAO infractionDAO;
    private final GuildIdDAO guildIdDAO;

    public MessageReceivedListener(DatabaseConnection databaseConnection) {
        this.badWordsDAO = new BadWordsDAO(databaseConnection);
        this.infractionDAO = new InfractionDAO(databaseConnection);
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }


    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (event.getGuild().getIdLong() == 238975753969074177L) return;
        if (event.getMember() != null && event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        for (String badWord : this.badWordsDAO.get("").badWords.split(" ")) {

            if (badWord.isEmpty()) continue;

            computeBadWord(badWord, event.getMessage());

        }

    }

    private void computeBadWord(String badWord, Message message) {

        if (message.getContentDisplay().toLowerCase().contains(badWord.toLowerCase())) {

            User user = message.getAuthor();
            Guild guild = message.getGuild();

            InfractionData data = new InfractionData(user.getId(), user.getId(), InfractionType.WARN, "Bad word usage", new Date(), null);

            infractionDAO.save(data);
            GuildIdsData logs = guildIdDAO.get("logs");

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                    .setAuthor("[WARN] " + user.getAsTag(), user.getAvatarUrl())
                    .addField("Utilisateur:", user.getAsMention(), true)
                    .addField("Modérateur:", "MultiBot", true)
                    .addField("Raison:", "Bad word usage", true);

            TextChannel logsChannel = guild.getTextChannelById(logs.id);
            logsChannel.sendMessage(embedBuilder.build()).queue();

            message.getChannel().sendMessage(Utils.getWarnEmbed(user, "Bad word usage")).queue();

        }

    }

}
