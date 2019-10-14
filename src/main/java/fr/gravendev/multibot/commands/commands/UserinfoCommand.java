package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserinfoCommand implements CommandExecutor {

    @Override
    public String getCommand() {
        return "userinfo";
    }

    @Override
    public String getDescription() {
        return "Avoir des informations sur un membre";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILS;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {
        Member member = message.getMember();
        List<Member> mentionedMembers = message.getMentionedMembers();

        if (mentionedMembers.size() > 0) {
            member = mentionedMembers.get(0);
        }

        User user = Objects.requireNonNull(member, "Le membre dont les userinfos sont demandées est null!").getUser();
        String joinDate = member.getTimeJoined().format(Utils.getDateTimeFormatter());
        String createdDate = user.getTimeCreated().format(Utils.getDateTimeFormatter());
        String roles = member.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
        String nickname = member.getNickname() == null ? "N/A" : member.getNickname();
        String game = member.getActivities().size() == 0 ? "N/A" : member.getActivities().stream()
                .map(Activity::getName)
                .collect(Collectors.joining(", "));
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.magenta)
                .setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl())
                .setThumbnail(user.getAvatarUrl())
                .addField("ID", user.getId(), true)
                .addField("Surnom", nickname, true)
                .addField("État", member.getOnlineStatus().getKey(), true)
                .addField("Joue à", game, true)
                .addField("Mention", user.getAsMention(), true)
                .addField("A rejoint", joinDate, true)
                .addField("Roles", roles, true)
                .setFooter("Crée le " + createdDate, null);

        message.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("général", "commandes");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return true;
    }
}
