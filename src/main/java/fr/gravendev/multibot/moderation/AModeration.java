package fr.gravendev.multibot.moderation;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AModeration implements CommandExecutor {

    protected abstract boolean isTemporary();

    @Override
    public void execute(Message message, String[] args) {
        List<Member> mentionedMembers = message.getMentionedMembers();
        MessageChannel messageChannel = message.getChannel();
        if (mentionedMembers.size() == 0) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Utilisation: "+getCommand()+" @membre " +
                    (isTemporary() ? "durée " : "") + "raison")).queue();
            return;
        }

        Guild guild = message.getGuild();

        Member member = mentionedMembers.get(0);
        Member bot = guild.getMember(message.getJDA().getSelfUser());

        User victim = member.getUser();

        String reason = "Non définie";
        if (args.length >= (isTemporary() ? 3 : 2)) {
            reason = Stream.of(args).skip(isTemporary() ? 2 : 1).collect(Collectors.joining(" "));
        }

        if (!PermissionUtil.canInteract(bot, member)) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Impossible d'appliquer une sanction sur cet utilisateur !")).queue();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        Date start = calendar.getTime();

        if(isTemporary()) {
            long duration = Utils.parsePeriod(args[1]);
            if(duration == -1) {
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
