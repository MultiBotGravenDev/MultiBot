package fr.gravendev.multibot.database.data;

public class RoleData {
    private final String roleId;
    private final String emoteId;

    public RoleData(String roleId, String emoteId) {
        this.roleId = roleId;
        this.emoteId = emoteId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getEmoteId() {
        return emoteId;
    }
}
