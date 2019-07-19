package fr.gravendev.multibot.database.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AntiRoleData {

    public final long userId;
    public final Map<Date, String> roles;

    public AntiRoleData(long userId, Map<Date, String> roles) {
        this.userId = userId;
        this.roles = new HashMap<>(roles);
    }

}
