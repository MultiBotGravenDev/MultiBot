package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.AModeration;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Date;

public class TempmuteCommand extends AModeration {

    private final DatabaseConnection databaseConnection;

    public TempmuteCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "tempmute";
    }

    @Override
    public String getDescription() {
        return "Rendre temporairement muet un membre du discord.";
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
        return true;
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

        InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
        InfractionData infractionData = new InfractionData(victim.getId(), message.getAuthor().getId(), InfractionType.MUTE, reason, start, end);
        infractionDAO.save(infractionData);

        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
        long mutedID = guildIdDAO.get("muted").id;
        Role muted = guild.getRoleById(mutedID);

        GuildIdsData logs = guildIdDAO.get("logs");

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[TEMPMUTE] " + victim.getAsTag(), victim.getAvatarUrl())
                .addField("Utilisateur:", victim.getAsMention(), true)
                .addField("Modérateur:", moderator.getAsMention(), true)
                .addField("Raison:", reason, true)
                .addField("Jusqu'à:", Utils.getDateFormat().format(end), true);

        TextChannel logsChannel = guild.getTextChannelById(logs.id);
        logsChannel.sendMessage(embedBuilder.build()).queue();

        guild.addRoleToMember(memberVictim, muted).queue();

        message.getChannel().sendMessage(Utils.getMuteEmbed(victim, reason, end)).queue();

    }
}