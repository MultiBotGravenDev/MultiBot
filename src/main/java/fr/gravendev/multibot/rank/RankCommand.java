package fr.gravendev.multibot.rank;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import fr.gravendev.multibot.database.data.ExperienceData;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        return "Permet de voir son niveau et son classement.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.EXPERIENCE;
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
            if (mentionedMembers.size() > 0)
                member = mentionedMembers.get(0);
            User user = member.getUser();

            if (user.isBot()) return;

            List<Role> roles = member.getRoles();
            Color color = roles.size() > 0 ? getColor(roles) : Color.WHITE;

            ExperienceDAO dao = new ExperienceDAO(databaseConnection);
            ExperienceData data = dao.get(user.getId());

            if (data == null) {
                data = new ExperienceData(user.getId());
                dao.save(data);
            }

            int experiences = data.getExperiences();
            int levels = data.getLevels();
            int expToLevelUp = levelToExp(levels);

            BufferedImage image = new BufferedImage(950, 300, BufferedImage.TYPE_INT_RGB);
            ImageBuilder builder = new ImageBuilder(image);

            String url = user.getAvatarUrl() != null ? user.getAvatarUrl() : user.getDefaultAvatarUrl();
            builder.drawImage(new URL(url));

            builder.drawProgress(color, experiences, expToLevelUp);

            builder.drawString(String.format("%d/%d", experiences, expToLevelUp), color, 785, 190, 25);
            builder.drawString(member.getUser().getAsTag(), color, 280, 185, 35);
            builder.drawString(String.format("Niveau %d", levels), Color.WHITE, levels >= 100 ? 730 : 750, 90, 35);

            message.getChannel().sendFile(builder.toInputStream(), "card.png").queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("commandes");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return true;
    }

    private int levelToExp(int level) {
        return 5 * level * level + 50 * level + 100;
    }

    private Color getColor(List<Role> roles) {
        Optional<Role> optionalRole = roles.stream()
                .filter(c -> c != null && c.getColor() != Color.BLACK)
                .findFirst();
        return optionalRole.isPresent() ? optionalRole.get().getColor() : Color.WHITE;
    }

}
