package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.WelcomeMessageDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class HereCommand implements CommandExecutor {

    private final WelcomeMessageDAO welcomeMessageDAO;

    HereCommand(DAOManager daoManager) {
        this.welcomeMessageDAO = daoManager.getWelcomeMessageDAO();
    }

    @Override
    public String getCommand() {
        return "here";
    }

    @Override
    public String getDescription() {
        return "Permet d'envoyer le message de bienvenue et la réactions permettant de déclencher le quizz.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("réglement", "lisez-ce-salon");
    }

    @Override
    public void execute(Message message, String[] args) {
        deleteMessages(message);
        sendMessages(message);
    }

    private void sendMessages(Message message) {
        MessageChannel channel = message.getChannel();

        Queue<Message> builtMessages = new MessageBuilder()
                .append(this.welcomeMessageDAO.get(String.valueOf(1)).getMessage())
                .buildAll(MessageBuilder.SplitPolicy.NEWLINE);

        List<Message> messages = new ArrayList<>(builtMessages);

        for (Message value : messages) {
            channel.sendMessage(value).queue();
        }

        if(channel.getName().equals("règlement")) {
            channel.sendMessage("Bonne continuation sur GravenDev - Community !").queue();
            return;
        }
        channel.sendMessage("Pour devenir membre, il vous suffit de cocher le petit :white_check_mark: présent sous ce message et de suivre les instructions fournies par <@572396802008154112>  !\n" +
                ":warning: Vous vous devez de répondre a toutes les questions ! Une candidature avec une question non répondue ne sera pas acceptée ! :warning:").queue();

        channel.sendMessage(new EmbedBuilder().setTitle("Règles lues et acceptées.")
                .setColor(Color.GREEN)
                .build())
                .queue(sentMessage -> sentMessage.addReaction("\u2705").queue());
    }

    private void deleteMessages(Message message) {
        message.getChannel().getHistoryBefore(message, 100).queue(history -> history.getRetrievedHistory().forEach(message1 -> message1.delete().queue()));
        message.delete().queue();
    }

}
