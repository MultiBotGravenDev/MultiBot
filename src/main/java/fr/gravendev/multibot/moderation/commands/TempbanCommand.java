package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.AModeration;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Date;

public class TempbanCommand extends AModeration {

    public TempbanCommand(DAOManager daoManager) {
        super(daoManager);
    }

    @Override
    protected boolean isTemporary() {
        return true;
    }

    @Override
    protected void execute(Message message, User victim, String reason, Date start, Date end) {
        User moderator = message.getAuthor();
        Guild guild = message.getGuild();

        InfractionData infractionData = new InfractionData(victim.getId(), moderator.getId(), InfractionType.BAN, reason, start, end);
        infractionDAO.save(infractionData);

        String logs = Configuration.SANCTIONS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[TEMPBAN] " + victim.getAsTag(), victim.getAvatarUrl())
                .addField("Utilisateur:", victim.getAsMention(), true)
                .addField("Modérateur:", message.getAuthor().getAsMention(), true)
                .addField("Raison:", reason, true)
                .addField("Jusqu'à:", Utils.getDateFormat().format(end), true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if(logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }

        guild.ban(victim, 0, reason).queue();
        message.getChannel().sendMessage(Utils.getBanEmbed(victim, reason, end)).queue();
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

