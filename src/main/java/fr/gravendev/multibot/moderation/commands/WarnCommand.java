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

public class WarnCommand extends AModeration {

    public WarnCommand(DAOManager daoManager) {
        super(daoManager);
    }

    @Override
    public String getCommand() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return "Mettre un avertissement à un membre du discord.";
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

    @Override
    protected boolean isTemporary() {
        return false;
    }

    @Override
    protected void execute(Message message, User victim, String reason, Date start, Date end) {

        User user = message.getAuthor();
        Guild guild = message.getGuild();

        InfractionData data = new InfractionData(
                victim.getId(), user.getId(), InfractionType.WARN, reason, new Date(), null);
        infractionDAO.save(data);

        String logs = Configuration.SANCTIONS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[WARN] " + victim.getAsTag(), victim.getAvatarUrl())
                .addField("Utilisateur:", victim.getAsMention(), true)
                .addField("Modérateur:", user.getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if (logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }

        message.getChannel().sendMessage(Utils.getWarnEmbed(victim, reason)).queue();
    }

}