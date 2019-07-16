package fr.gravendev.multibot.rank;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.data.ExperienceData;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import fr.gravendev.multibot.rank.ImageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RankCommand implements CommandExecutor {

    private DatabaseConnection databaseConnection;

    public RankCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "rank";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {
        try {

            Member member = message.getMember();
            List<Member> mentionedMembers = message.getMentionedMembers();
            if(mentionedMembers.size() > 0)
                member = mentionedMembers.get(0);
            User user = member.getUser();

            List<Role> roles = member.getRoles();
            Color color = roles.size() > 0 ? roles.get(0).getColor() : Color.WHITE;

            ExperienceDAO dao = new ExperienceDAO(databaseConnection.getConnection());
            ExperienceData data = dao.get(user.getId());
            if(data == null) {
                data = new ExperienceData(user.getId());
                dao.save(data);
            }

            int experiences = data.getExperiences();
            int levels = data.getLevels();
            int expToLevelUp = levelToExp(levels);

            BufferedImage image = new BufferedImage(950, 300, BufferedImage.TYPE_INT_RGB);
            ImageBuilder builder = new ImageBuilder(image);

            builder.drawImage(new URL(member.getUser().getAvatarUrl()), 50);

            builder.drawProgress(color, experiences, expToLevelUp);

            builder.drawString(String.format("%d/%d", experiences, expToLevelUp), color, 785, 190, 25);
            builder.drawString(member.getUser().getAsTag(), color, 280, 185, 35);
            builder.drawString(String.format("Niveau %d", levels), Color.WHITE, levels >= 100 ? 730 : 750, 90, 35);

            message.getChannel().sendFile(builder.toInputStream(), "card.png").queue();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("_commandes");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return true;
    }

    private int levelToExp(int level) {
        return (5 * level) * 2 + (50 * level + 100);
    }

}
