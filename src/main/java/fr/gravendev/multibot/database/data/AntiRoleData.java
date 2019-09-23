package fr.gravendev.multibot.database.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AntiRoleData {
    private final long userId;
    private final Map<Date, String> roles;

    public AntiRoleData(long userId, Map<Date, String> roles) {
        this.userId = userId;
        this.roles = new HashMap<>(roles);
    }

    public long getUserId() {
        return userId;
    }

    public Map<Date, String> getRoles() {
        return roles;
    }
}
