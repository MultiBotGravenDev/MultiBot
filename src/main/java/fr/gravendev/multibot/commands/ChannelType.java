package fr.gravendev.multibot.commands;

public enum ChannelType {
    ALL(net.dv8tion.jda.core.entities.ChannelType.TEXT),
    GUILD(net.dv8tion.jda.core.entities.ChannelType.TEXT),
    PRIVATE(net.dv8tion.jda.core.entities.ChannelType.PRIVATE);

    private net.dv8tion.jda.core.entities.ChannelType channelType;

    ChannelType(net.dv8tion.jda.core.entities.ChannelType channelType) {
        this.channelType = channelType;
    }

    public net.dv8tion.jda.core.entities.ChannelType getChannelType() {
        return channelType;
    }

    public boolean isEqualsTo(net.dv8tion.jda.core.entities.ChannelType targetType) {
        return this == ChannelType.ALL || this.getChannelType() == targetType;
    }

}
