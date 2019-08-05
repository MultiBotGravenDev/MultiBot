package fr.gravendev.multibot.database.data;

import fr.gravendev.multibot.moderation.InfractionType;

import java.util.Date;
import java.util.UUID;

public class InfractionData {

    private UUID uuid;
    private String punished_id, punisher_id, reason;
    private InfractionType type;
    private Date start, end;
    private boolean finished;

    public InfractionData(String punished_id, String punisher_id, InfractionType type, String reason, Date start, Date end) {
        this.uuid = UUID.randomUUID();
        this.punished_id = punished_id;
        this.punisher_id = punisher_id;
        this.type = type;
        this.reason = reason;
        this.start = start;
        this.end = end;
        this.finished = false;
    }

    public InfractionData(UUID uuid, String punished_id, String punisher_id, InfractionType type, String reason, Date start, Date end, boolean finished) {
        this.uuid = uuid;
        this.punished_id = punished_id;
        this.punisher_id = punisher_id;
        this.type = type;
        this.reason = reason;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public String getPunished_id() {
        return punished_id;
    }

    public String getPunisher_id() {
        return punisher_id;
    }

    public String getReason() {
        return reason;
    }

    public InfractionType getType() {
        return type;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }
}
