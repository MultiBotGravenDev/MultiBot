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

public class BanCommand extends AModeration {

    public BanCommand(DAOManager daoManager) {
        super(daoManager);
    }

    @Override
    protected boolean isTemporary() {
        return false;
    }

    @Override
    protected void execute(Message message, User victim, String reason, Date start, Date end) {
        User moderator = message.getAuthor();
        Guild guild = message.getGuild();

        guild.ban(victim, 0, reason).queue();

        InfractionData data = new InfractionData(
                victim.getId(), moderator.getId(), InfractionType.BAN, reason, new Date(), null);
        infractionDAO.save(data);

        String logs = Configuration.LOGS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[BAN] " + victim.getAsTag(), victim.getAvatarUrl())
                .addField("Utilisateur:", victim.getAsMention(), true)
                .addField("Modérateur:", moderator.getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if (logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }
        
        message.getChannel().sendMessage(Utils.getBanEmbed(victim, reason, null)).queue();
    }

    @Override
    public String getCommand() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Bannir un membre du discord";
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
