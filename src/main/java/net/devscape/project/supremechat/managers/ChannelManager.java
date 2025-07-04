package net.devscape.project.supremechat.managers;

import net.devscape.project.supremechat.SupremeChat;
import net.devscape.project.supremechat.object.Channel;
import org.bukkit.entity.Player;

import java.util.*;

public class ChannelManager {

    public List<Channel> channels = new ArrayList<>();
    private Map<UUID, String> playerChannel = new HashMap<>();

    public ChannelManager() {
        loadChannels();
    }

    public Channel getChannel(Player player) {
        return getChannel(playerChannel.get(player.getUniqueId()));
    }

    public Channel getChannel(String channel) {
        for (Channel c : channels) {
            if (c.getName().equalsIgnoreCase(channel)) {
                return c;
            }
        }

        return null;
    }

    public boolean isChannel(String channel) {
        return getChannel(channel) != null;
    }

    public void addToChannel(Player player, String channel) {
        if (isInChannel(player)) {
            playerChannel.remove(player.getUniqueId());
        }

        playerChannel.put(player.getUniqueId(), channel);
    }

    public boolean isInChannel(Player player) {
        return playerChannel.containsKey(player.getUniqueId());
    }

    public void loadChannels() {
        if (SupremeChat.getInstance().getConfig().getConfigurationSection("channels") != null) {
            for (String channel : SupremeChat.getInstance().getConfig().getConfigurationSection("channels").getKeys(false)) {
                String format = SupremeChat.getInstance().getConfig().getString("channels." + channel + ".format");
                boolean enabled = SupremeChat.getInstance().getConfig().getBoolean("channels." + channel + ".enable");
                String permission = SupremeChat.getInstance().getConfig().getString("channels." + channel + ".permission");
                String chat_color = SupremeChat.getInstance().getConfig().getString("channels." + channel + ".chat-color");

                if (enabled) {
                    Channel c = new Channel(channel, format, permission, chat_color, enabled);
                    channels.add(c);
                }
            }
        }
    }

    public void reloadChannels() {
        channels.clear();
        loadChannels();
    }

    public List<Channel> getChannels() { return channels; }

    public Map<UUID, String> getPlayerChannel() { return playerChannel; }
}