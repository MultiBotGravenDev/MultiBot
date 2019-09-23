package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.AModeration;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Date;

public class MuteCommand extends AModeration {

    public MuteCommand(DAOManager daoManager) {
        super(daoManager);
    }

    @Override
    public String getCommand() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Rendre muet un membre du discord.";
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
        User moderator = message.getAuthor();
        Guild guild = message.getGuild();

        Member memberVictim = guild.getMember(victim);
        if(memberVictim == null) {
            return;
        }

        if (GuildUtils.hasRole(memberVictim, "Muted")) {
            message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Ce membre est déjà mute")).queue();
            return;
        }

        Role muted = guild.getRoleById(Configuration.MUTED.getValue());
        if(muted != null) {
            guild.addRoleToMember(memberVictim, muted).queue();
        }

        InfractionData data = new InfractionData(
                victim.getId(), moderator.getId(), InfractionType.MUTE, reason, new Date(), null);
        infractionDAO.save(data);

        String logs = Configuration.LOGS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[MUTE] " + victim.getAsTag(), victim.getAvatarUrl())
                .addField("Utilisateur:", victim.getAsMention(), true)
                .addField("Modérateur:", moderator.getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if(logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }
        message.getChannel().sendMessage(Utils.getMuteEmbed(victim, reason, null)).queue();
    }
}
