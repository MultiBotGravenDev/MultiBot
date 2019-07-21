package fr.gravendev.multibot.moderation;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.util.Date;

public class TempbanCommand extends AModeration {

    private DatabaseConnection databaseConnection;
    public TempbanCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    protected boolean isTemporary() {
        return true;
    }

    @Override
    protected void execute(Message message, User victim, String reason, Date start, Date end) {
        User moderator = message.getAuthor();
        Guild guild = message.getGuild();

        InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
        InfractionData infractionData = new InfractionData(victim.getId(), moderator.getId(), InfractionType.BAN, reason, start, end);
        infractionDAO.save(infractionData);

        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
        GuildIdsData logs = guildIdDAO.get("logs");

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[TEMPBAN] " + victim.getAsTag(), victim.getAvatarUrl())
                .addField("Utilisateur:", victim.getAsMention(), true)
                .addField("Modérateur:", message.getAuthor().getAsMention(), true)
                .addField("Raison:", reason, true)
                .addField("Jusqu'à:", Utils.getDateFormat().format(end), true);

        TextChannel logsChannel = guild.getTextChannelById(logs.id);
        logsChannel.sendMessage(embedBuilder.build()).queue();

        guild.getController().ban(victim, 0, reason).queue();
        message.getChannel().sendMessage(Utils.banEmbed(victim, reason, end)).queue();
    }

    @Override
    public String getCommand() {
        return "tempban";
    }

    @Override
    public String getDescription() {
        return "Bannir temporairement un membre du discord";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public boolean isAuthorizedChannel(MessageChannel channel) {
        return true;
    }
}

