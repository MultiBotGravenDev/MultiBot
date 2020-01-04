package fr.gravendev.multibot.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserSearchUtils {

    public enum SearchMode {
        /**
         * Tries to search from all the possible ways (mention, id, tag, full username and partial username)
         * with a non-negligible risk of false positives.
         * <p>
         * This must be used when the search is purely informative (e.g. userinfo command).
         */
        SENSITIVE,
        
        /**
         * Tries to search from all the normal ways (mention, id, tag and full username),
         * with a small risk of false positives.
         * <p>
         * This must be used when the search is not critical (e.g. mute command).
         */
        NORMAL,

        /**
         * Tries to search only from the safe ways (mention, id and tag),
         * with almost no risk of false positives.
         * <p>
         * This must be used when the search is critical (e.g. ban command).
         */
        SAFE
    }

    public static Optional<User> searchUserByMention(JDA jda, String message) {
        Pattern pattern = Message.MentionType.USER.getPattern();
        Matcher matcher = pattern.matcher(message);

        if (matcher.matches()) {
            try {
                User user = jda.getUserById(matcher.group(1));
                return Optional.ofNullable(user);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    public static Optional<User> searchUserById(JDA jda, String id) {
        try {
            return Optional.ofNullable(jda.getUserById(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<User> searchUserByTag(JDA jda, String tag) {
        try {
            return Optional.ofNullable(jda.getUserByTag(tag));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<User> searchUserByName(JDA jda, String name) {
        List<User> users = jda.getUsersByName(name, true);

        if (users.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(users.get(0));
    }

    public static Optional<User> searchUserByPartialName(JDA jda, String name) {
        List<User> users = jda.getUsers().stream()
                .filter(user -> user.getName().startsWith(name))
                .collect(Collectors.toList());
        
        if (users.size() != 1) {
            return Optional.empty();
        }
        
        return Optional.of(users.get(0));
    }
    
    public static Optional<User> searchUser(JDA jda, String message) {
        return searchUser(jda, message, SearchMode.NORMAL);
    }

    public static Optional<User> searchUser(JDA jda, String message, SearchMode mode) {
        Stream<Supplier<Optional<User>>> stream;
        
        switch (mode) {
            case SENSITIVE:
                stream = Stream.of(
                        () -> searchUserByMention(jda, message),
                        () -> searchUserById(jda, message),
                        () -> searchUserByTag(jda, message),
                        () -> searchUserByName(jda, message),
                        () -> searchUserByPartialName(jda, message)
                );
                break;
            case NORMAL:
                stream = Stream.of(
                        () -> searchUserByMention(jda, message),
                        () -> searchUserById(jda, message),
                        () -> searchUserByTag(jda, message),
                        () -> searchUserByName(jda, message)
                );
                break;
            case SAFE:
                stream = Stream.of(
                        () -> searchUserByMention(jda, message),
                        () -> searchUserById(jda, message),
                        () -> searchUserByTag(jda, message)
                );
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return stream.map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public static Optional<Member> searchMemberByMention(Guild guild, String message) {
        Pattern pattern = Message.MentionType.USER.getPattern();
        Matcher matcher = pattern.matcher(message);

        if (matcher.matches()) {
            try {
                Member member = guild.getMemberById(matcher.group(1));
                return Optional.ofNullable(member);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    public static Optional<Member> searchMemberById(Guild guild, String id) {
        try {
            return Optional.ofNullable(guild.getMemberById(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Member> searchMemberByTag(Guild guild, String tag) {
        try {
            return Optional.ofNullable(guild.getMemberByTag(tag));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<Member> searchMemberByName(Guild guild, String name) {
        List<Member> members = guild.getMembersByEffectiveName(name, true);

        if (members.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(members.get(0));
    }

    public static Optional<Member> searchMemberByPartialName(Guild guild, String name) {
        List<Member> members = guild.getMembers().stream()
                .filter(member -> member.getEffectiveName().toLowerCase().startsWith(name.toLowerCase()))
                .collect(Collectors.toList());

        if (members.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(members.get(0));
    }

    public static Optional<Member> searchMember(Guild guild, String message) {
        return searchMember(guild, message, SearchMode.NORMAL);
    }
    
    public static Optional<Member> searchMember(Guild guild, String message, SearchMode mode) {
        Stream<Supplier<Optional<Member>>> stream;
        
        switch (mode) {
            case SENSITIVE:
                stream = Stream.of(
                        () -> searchMemberByMention(guild, message),
                        () -> searchMemberById(guild, message),
                        () -> searchMemberByTag(guild, message),
                        () -> searchMemberByName(guild, message),
                        () -> searchMemberByPartialName(guild, message)
                );
                break;
            case NORMAL:
                stream = Stream.of(
                        () -> searchMemberByMention(guild, message),
                        () -> searchMemberById(guild, message),
                        () -> searchMemberByTag(guild, message),
                        () -> searchMemberByName(guild, message)
                );
                break;
            case SAFE:
                stream = Stream.of(
                        () -> searchMemberByMention(guild, message),
                        () -> searchMemberById(guild, message),
                        () -> searchMemberByTag(guild, message)
                );
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return stream.map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
    
    public static void sendUserNotFound(MessageChannel channel) {
        MessageEmbed embed = Utils.buildEmbed(
                Color.RED,
                "Erreur : Impossible de trouver un utilisateur correspondant à l'argument donné, " +
                        "merci d'entrer un utilisateur correct."
        );

        channel.sendMessage(embed).queue();
    }
    
}
