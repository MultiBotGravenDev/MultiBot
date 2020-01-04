package fr.gravendev.multibot.moderation;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.utils.UserSearchUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import javax.swing.text.html.Option;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AModeration implements CommandExecutor {

    protected final InfractionDAO infractionDAO;

    public AModeration(DAOManager daoManager) {
        this.infractionDAO = daoManager.getInfractionDAO();
    }

    protected abstract boolean isTemporary();

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    // TODO Refactor it : split it into differents methods, etc.
    @Override
    public void execute(Message message, String[] args) {
        MessageChannel messageChannel = message.getChannel();

        if (args.length == 0) {
            messageChannel.sendMessage(Utils.errorArguments(getCommand(), "@membre "+ (isTemporary() ? "<durée> " : "") + "<raison>")).queue();
            return;
        }

        Guild guild = message.getGuild();
        
        Optional<Member> opMember = UserSearchUtils.searchMember(guild, args[0]);
        
        if (!opMember.isPresent()) {
            UserSearchUtils.sendUserNotFound(messageChannel);
            return;
        }
        
        Member member = opMember.get();
        User victim = member.getUser();

        String reason = "Non définie";
        if (args.length >= (isTemporary() ? 3 : 2)) {
            reason = Stream.of(args).skip(isTemporary() ? 2 : 1).collect(Collectors.joining(" "));
        }

        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Impossible d'appliquer une sanction sur cet utilisateur !")).queue();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        Date start = calendar.getTime();

        if (args.length < 2) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Utilisation: " + getCommand() + " @membre " +
                    (isTemporary() ? "durée " : "") + "raison")).queue();
            return;
        }

        if (isTemporary()) {
            long duration = Utils.parsePeriod(args[1]);
            if (duration == -1) {
                messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Durée invalide")).queue();
                return;
            }

            calendar.add(Calendar.MILLISECOND, (int) duration);
            Date end = calendar.getTime();
            execute(message, victim, reason, start, end);
            return;
        }

        execute(message, victim, reason, start, null);
    }

    protected abstract void execute(Message message, User victim, String reason, Date start, Date end);
}
