package fr.gravendev.multibot.database.data;

public class RoleData {

    private final String roleId;
    private final String emoteId;
    private final String channelId;

    public RoleData(String roleId, String emoteId, String channelId) {
        this.roleId = roleId;
        this.emoteId = emoteId;
        this.channelId = channelId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getEmoteId() {
        return emoteId;
    }

    public String getChannelId() {
        return channelId;
    }

}
