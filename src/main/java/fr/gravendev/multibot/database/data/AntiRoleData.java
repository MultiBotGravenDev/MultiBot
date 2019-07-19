package fr.gravendev.multibot.database.data;

import java.sql.Date;
import java.util.Map;

public class AntiRoleData {

    public final long userId;
    public final Map<Date, String> roles;

    public AntiRoleData(long userId, Map<Date, String> roles) {
        this.userId = userId;
        this.roles = roles;
    }

}
