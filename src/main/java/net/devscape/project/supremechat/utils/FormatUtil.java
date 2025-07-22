package net.devscape.project.supremechat.utils;

import net.devscape.project.supremechat.SupremeChat;
import net.devscape.project.supremechat.object.Channel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static net.devscape.project.supremechat.utils.Message.*;

public class FormatUtil {


    // ==================================================
    // GET RANK
    // ==================================================
    public static String getRank(Player player) {
        if (SupremeChat.getChat().getPrimaryGroup(player) == null) {
            return "default";
        }

        return SupremeChat.getChat().getPrimaryGroup(player);
    }

    // ==================================================
    // SCAPECHAT HELP MESSAGE
    // ==================================================

    public static void sendHelp(Player player) {
        msgPlayer(player,
                PREFIX + " &7Help Message:",
                "",
                "&6/supremechat reload &7- reload the plugin config.",
                "&6/supremechat mutechat &7- mutes the chat for everyone.",
                "",
                "&#fdc269Author: &#fff2ccDevScape",
                "&#fdc269Plugin Version: &#fff2cc" + SupremeChat.getInstance().getDescription().getVersion(),
                "&#fdc269ScapeHelp Server: &#fff2ccfhttps://discord.gg/AnPwty8asP");
    }

    public static String emojiReplacer(Player player, String message, boolean isInChannel, boolean isNormalChat) {
        FileConfiguration config = SupremeChat.getInstance().getConfig();

        // Check if the "emojis" section exists in the config
        if (config.isConfigurationSection("emojis")) {
            for (String key : config.getConfigurationSection("emojis").getKeys(false)) {

                if (player.hasPermission("supremechat.emoji." + key) || player.hasPermission("supremechat.emoji.*")) {
                    String emoticon = config.getString("emojis." + key + ".emoticon");
                    String emoji = config.getString("emojis." + key + ".emoji");

                    if (isNormalChat) {
                        emoji = emoji + SupremeChat.getInstance().getConfig().getString("global-chat-color");
                    }

                    if (isInChannel) {
                        if (SupremeChat.getInstance().getChannelManager().isInChannel(player)) {
                            Channel c = SupremeChat.getInstance().getChannelManager().getChannel(player);

                            emoji = emoji + c.getChatColor();
                        }
                    }

                    // Ensure both emoticon and emoji are not null
                    if (emoticon != null && emoji != null) {
                        // Replace all occurrences of the emoticon in the message with the emoji
                        if (message.contains(emoticon)) {
                            message = message.replace(emoticon, emoji);
                        }
                    }
                }
            }
        }

        // Apply PlaceholderAPI replacements
        message = replacePlaceholders(player, message);

        return message;
    }

}