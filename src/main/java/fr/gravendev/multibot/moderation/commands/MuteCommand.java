package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.AModeration;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.util.Date;

public class MuteCommand extends AModeration {

    private final DatabaseConnection databaseConnection;

    public MuteCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
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
        if (GuildUtils.hasRole(memberVictim, "Muted")) {
            message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Ce membre est déjà mute")).queue();
            return;
        }

        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
        long mutedID = guildIdDAO.get("muted").id;
        Role muted = guild.getRoleById(mutedID);

        guild.getController().addSingleRoleToMember(memberVictim, muted).queue();

        InfractionData data = new InfractionData(
                victim.getId(), moderator.getId(), InfractionType.MUTE, reason, new Date(), null);
        InfractionDAO dao = new InfractionDAO(databaseConnection);
        dao.save(data);

        GuildIdsData logs = guildIdDAO.get("logs");

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[MUTE] " + victim.getAsTag(), victim.getAvatarUrl())
                .addField("Utilisateur:", victim.getAsMention(), true)
                .addField("Modérateur:", moderator.getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs.id);
        logsChannel.sendMessage(embedBuilder.build()).queue();

        message.getChannel().sendMessage(Utils.muteEmbed(victim, reason, null)).queue();
    }
}
