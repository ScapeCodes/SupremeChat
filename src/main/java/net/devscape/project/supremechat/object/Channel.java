package net.devscape.project.supremechat.object;

public class Channel {

    private String name;
    private String format;
    private String permission;
    private String chatColor;
    private boolean enabled;

    public Channel(String name, String format, String permission, String chatColor, boolean enabled) {
        this.name = name;
        this.format = format;
        this.permission = permission;
        this.chatColor = chatColor;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getChatColor() {
        return chatColor;
    }

    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }
}
