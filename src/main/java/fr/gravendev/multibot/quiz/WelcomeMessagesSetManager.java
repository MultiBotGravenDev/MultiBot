package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.WelcomeMessageDAO;
import fr.gravendev.multibot.database.data.MessageData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeMessagesSetManager {

    private final WelcomeMessageDAO welcomeMessageDAO;
    private Map<Long, List<Message>> setters = new HashMap<>();

    public WelcomeMessagesSetManager(DAOManager daoManager) {
        this.welcomeMessageDAO = daoManager.getWelcomeMessageDAO();
    }

    public void onCommand(Message message) {

        if (!this.setters.containsKey(message.getAuthor().getIdLong())) {
            createEntry(message);
        } else {
            closeEntry(message);
        }

    }

    private void createEntry(Message message) {
        this.setters.put(message.getAuthor().getIdLong(), new ArrayList<>(Collections.singletonList(message)));
        message.getChannel().sendMessage("Ecrivez le nouveau message de bienvenue").queue();
    }

    private void closeEntry(Message message) {

        String newMessage = this.setters.get(message.getAuthor().getIdLong())
                .stream()
                .skip(1)
                .map(Message::getContentDisplay)
                .reduce((messageBuilder, message1) -> messageBuilder += message1 + "\n")
                .orElse(" ");

        this.welcomeMessageDAO.save(new MessageData(String.valueOf(1), newMessage));
        this.setters.remove(message.getAuthor().getIdLong());

        message.getChannel().sendMessage("Nouveau message enregistré").queue();

    }

    public void registerMessage(Message message) {
        List<Message> messages = this.setters.get(message.getAuthor().getIdLong());

        boolean isNotACommand = !message.getContentDisplay().startsWith("!quiz setChoice message");

        if (messages != null && isNotACommand) {

            boolean isGoodChannel = messages.get(0).getChannel().getIdLong() == message.getChannel().getIdLong();

            if (isGoodChannel) {

                messages.add(message);
                message.getChannel().sendMessage("enregistré").queue();

            }

        }
    }

    public boolean isWaitingFor(User user) {
        return this.setters.containsKey(user.getIdLong());
    }
}
