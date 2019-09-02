package fr.gravendev.multibot.votes;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.VoteDAO;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.database.data.VoteDataBuilder;
import fr.gravendev.multibot.votes.roles.Developer;
import fr.gravendev.multibot.votes.roles.Honorable;
import fr.gravendev.multibot.votes.roles.Pillar;
import fr.gravendev.multibot.votes.roles.Role;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VoteCommand implements CommandExecutor {

    private final List<Role> roles;
    private final DatabaseConnection databaseConnection;

    public VoteCommand(DatabaseConnection databaseConnection) {
        roles = Arrays.asList(
                new Honorable(databaseConnection),
                new Developer(databaseConnection),
                new Pillar(databaseConnection)
        );
        this.databaseConnection = databaseConnection;
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
                "vote-developpeur",
                "votes-piliers");
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

        if (mentionedMembers.size() < 1 || args.length < 2) {
            channel.sendMessage("Erreur. !vote @personne <présentation>").queue();
            return;
        }

        Member member = mentionedMembers.get(0);
        String channelName = channel.getName();
        Role role = roles.stream()
                .filter(currentRole -> currentRole.getChannelName().equalsIgnoreCase(channelName))
                .findAny()
                .orElse(null);

        if (role == null){
            return;
        }

        boolean memberAlreadyHasRole = member.getRoles().stream()
                .anyMatch(currentRole -> currentRole.getIdLong() == role.getRoleId());
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

            VoteDAO voteDAO = new VoteDAO(this.databaseConnection);

            voteDAO.save(VoteDataBuilder
                    .aVoteData()
                    .withMessageId(sentMessage.getIdLong())
                    .withRole(role.getRoleName())
                    .withUserID(member.getUser().getIdLong())
                    .isAccepted(false)
                    .build());

            sentMessage.getChannel().retrieveMessageById(sentMessage.getIdLong()).queueAfter(10, TimeUnit.SECONDS, sentMessage2 -> {

                VoteData voteData = voteDAO.get(sentMessage.getId());
                boolean accepted = voteData.yes.size() > voteData.no.size();


                MessageEmbed finalEmbed = new EmbedBuilder()
                        .setColor(role.getColor())
                        .setAuthor(member.getUser().getAsTag(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl())
                        .setTitle("Vote " + role.getRoleName())
                        .addField("Présentation :", presentation, false)
                        .addField("Oui :", voteData.yes.stream().map(userId -> message.getJDA().getUserById(userId).getName()).collect(Collectors.joining("\n")), true)
                        .addField("Non :", voteData.no.stream().map(userId -> message.getJDA().getUserById(userId).getName()).collect(Collectors.joining("\n")), true)
                        .addField("Blanc :", voteData.white.stream().map(userId -> message.getJDA().getUserById(userId).getName()).collect(Collectors.joining("\n")), true)
                        .addField(accepted ? "Accepté :" : "Refusé :", "(" + voteData.yes.size() + "/" + voteData.no.size() + ")", false)
                        .setFooter("Proposé par " + message.getAuthor().getAsTag(), message.getAuthor().getAvatarUrl())
                        .build();

                voteData = VoteDataBuilder.fromVoteData(voteData).isAccepted(accepted).build();
                voteDAO.save(voteData);

                if (accepted) {
                    net.dv8tion.jda.api.entities.Role voteRole = message.getGuild().getRoleById(role.getRoleId());
                    if(voteRole != null)
                        message.getGuild().addRoleToMember(member, voteRole).queue();
                }

                sentMessage2.editMessage(finalEmbed).queue();
                sentMessage2.clearReactions().queue();

                voteDAO.delete(voteData);
            });

        });

        message.delete().queue();


    }

}
