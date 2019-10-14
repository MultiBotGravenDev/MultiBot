package fr.gravendev.multibot.database.data;

import fr.gravendev.multibot.moderation.InfractionType;

import java.util.Date;
import java.util.UUID;

public class InfractionData {
    private final UUID uuid;
    private final String punishedId;
    private final String punisherId;
    private final String reason;
    private final InfractionType type;
    private final Date start;
    private Date end;
    private boolean finished;

    public InfractionData(String punishedId, String punisherId, InfractionType type, String reason, Date start, Date end) {
        this.uuid = UUID.randomUUID();
        this.punishedId = punishedId;
        this.punisherId = punisherId;
        this.type = type;
        this.reason = reason;
        this.start = start;
        this.end = end;
        this.finished = false;
    }

    public InfractionData(UUID uuid, String punishedId, String punisherId, InfractionType type, String reason, Date start, Date end, boolean finished) {
        this.uuid = uuid;
        this.punishedId = punishedId;
        this.punisherId = punisherId;
        this.type = type;
        this.reason = reason;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public String getPunishedId() {
        return punishedId;
    }

    public String getPunisherId() {
        return punisherId;
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
