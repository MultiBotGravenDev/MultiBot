package fr.gravendev.multibot.votes;

import fr.gravendev.multibot.votes.roles.Role;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class VoteEmbedBuilder {

    private Role role;
    private Member member;

    private String presentation;

    private int yes;
    private int no;
    private int white;

    private VoteEmbedBuilder() {}

    public static VoteEmbedBuilder aVoteEmbed() {
        return new VoteEmbedBuilder();
    }

    public VoteEmbedBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public VoteEmbedBuilder withMember(Member member) {
        this.member = member;
        return this;
    }

    public VoteEmbedBuilder withPresentation(String presentation) {
        this.presentation = presentation;
        return this;
    }

    public VoteEmbedBuilder withYes(int yes) {
        this.yes = yes;
        return this;
    }

    public VoteEmbedBuilder withNo(int no) {
        this.no = no;
        return this;
    }

    public VoteEmbedBuilder withWhite(int white) {
        this.white = white;
        return this;
    }


    public MessageEmbed build() {
        return new EmbedBuilder()
                .setColor(role.getColor())
                .setAuthor(member.getNickname())
                .setTitle("Vote " + role.getRoleName())
                .setImage(member.getUser().getAvatarUrl())
                .addField("Présentation :",  presentation, false)
                .addField("Oui :", yes + "", true)
                .addField("Non :", no + "", true)
                .addField("Blanc :", white + "", true)
                .build();
    }

}
