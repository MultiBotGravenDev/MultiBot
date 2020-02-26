package fr.gravendev.multibot.votes;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.VoteDAO;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.database.data.VoteDataBuilder;
import fr.gravendev.multibot.utils.UserSearchUtils;
import fr.gravendev.multibot.votes.roles.Developer;
import fr.gravendev.multibot.votes.roles.Honorable;
import fr.gravendev.multibot.votes.roles.Role;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VoteCommand implements CommandExecutor {

    private final List<Role> roles;
    private final VoteDAO voteDAO;

    public VoteCommand(DAOManager daoManager) {
        roles = Arrays.asList(
                new Honorable(),
                new Developer()
        );

        this.voteDAO = daoManager.getVoteDAO();
    }

    @Override
    public String getCommand() {
        return "vote";
    }

    @Override
    public String getDescription() {
        return "Permet de lancer un vote pour une personne.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SYSTEM;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList(
                "vote-honorable",
                "vote-developpeur");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    // TODO Refactor : split that in multiple private methods etc.
    @Override
    public void execute(Message message, String[] args) {

        List<Member> mentionedMembers = message.getMentionedMembers();
        MessageChannel channel = message.getChannel();

        if (args.length < 2) {
            channel.sendMessage("Erreur. " + getCharacter() + "vote <@membre> <présentation>").queue();
            return;
        }

        Optional<Member> opMember = UserSearchUtils.searchMember(message.getGuild(), args[0]);

        if (!opMember.isPresent()) {
            UserSearchUtils.sendUserNotFound(message.getChannel());
            return;
        }
        
        Member member = opMember.get();
        
        if(member.getUser().isBot()) {
            channel.sendMessage("Erreur: Vous ne pouvez pas proposer un bot.").queue();
            return;
        }


        String channelName = channel.getName();
        Role role = roles.stream()
                .filter(currentRole -> currentRole.getChannelName().equalsIgnoreCase(channelName))
                .findAny()
                .orElse(null);

        if (role == null){
            return;
        }

        boolean memberAlreadyHasRole = member.getRoles().stream()
                .anyMatch(currentRole -> currentRole.getId().equals(role.getRoleId()));
        if (memberAlreadyHasRole) {
            channel.sendMessage("Cette personne a déjà le role " + role.getRoleName()).queue();
            return;
        }

        String presentation = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(role.getColor())
                .setAuthor(member.getUser().getAsTag(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl())
                .setTitle("Vote " + role.getRoleName())
                .addField("Présentation :", presentation, false)
                .setFooter("Proposé par " + message.getAuthor().getAsTag(), message.getAuthor().getAvatarUrl())
                .build();

        message.getChannel().sendMessage("@everyone").queue();

        message.getChannel().sendMessage(messageEmbed).queue(sentMessage -> {
            sentMessage.addReaction("\u2705").queue();
            sentMessage.addReaction("\u274C").queue();
            sentMessage.addReaction("\u2B1C").queue();

            voteDAO.save(VoteDataBuilder
                    .aVoteData()
                    .withMessageId(sentMessage.getIdLong())
                    .withRole(role.getRoleName())
                    .withUserID(member.getUser().getIdLong())
                    .isAccepted(false)
                    .build());

            sentMessage.getChannel().retrieveMessageById(sentMessage.getIdLong()).queueAfter(10, TimeUnit.SECONDS, sentMessage2 -> {

                VoteData voteData = voteDAO.get(sentMessage.getId());

                List<Long> voteYes = voteData.getVotersByType(VoteType.YES);
                List<Long> voteNo = voteData.getVotersByType(VoteType.NO);
                List<Long> voteWhite = voteData.getVotersByType(VoteType.WHITE);

                boolean accepted = voteYes.size() > voteNo.size();

                JDA jda = message.getJDA();

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(role.getColor())
                        .setAuthor(member.getUser().getAsTag(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl())
                        .setTitle("Vote " + role.getRoleName())
                        .addField("Présentation :", presentation, false)
                        .addField("Oui :", formatVoters(jda, voteYes), true)
                        .addField("Non :", formatVoters(jda, voteNo), true)
                        .addField("Blanc :", formatVoters(jda, voteWhite), true)
                        .addField(accepted ? "Accepté :" : "Refusé :", "(" + voteYes.size() + "/" + voteNo.size() + ")", false)
                        .setFooter("Proposé par " + message.getAuthor().getAsTag(), message.getAuthor().getAvatarUrl());

                voteData = VoteDataBuilder.fromVoteData(voteData).isAccepted(accepted).build();
                voteDAO.save(voteData);

                if (accepted) {
                    net.dv8tion.jda.api.entities.Role voteRole = message.getGuild().getRoleById(role.getRoleId());
                    if(voteRole != null)
                        message.getGuild().addRoleToMember(member, voteRole).queue();
                }

                sentMessage2.editMessage(embedBuilder.build()).queue();
                sentMessage2.clearReactions().queue();

                voteDAO.delete(voteData);
            });

        });

        message.delete().queue();
    }

    private String formatVoters(JDA jda, List<Long> voters) {
        return voters.stream()
                .map(userId -> {
                    User user = jda.getUserById(userId);
                    if(user == null)
                        return "";
                    return user.getName();
                })
                .collect(Collectors.joining("\n"));
    }

}
