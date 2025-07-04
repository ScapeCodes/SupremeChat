package net.devscape.project.supremechat.commands;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static net.devscape.project.supremechat.utils.Message.msgPlayer;
import static net.devscape.project.supremechat.utils.Message.replacePlaceholders;

public class EmojisCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            FileConfiguration config = SupremeChat.getInstance().getConfig();

            if (config.isConfigurationSection("emojis")) {

                List<String> emojis = new ArrayList<>(config.getConfigurationSection("emojis").getKeys(false));

                msgPlayer(sender, "&d&lEmojis &8&l➟ &7List: (" + emojis.size() + ")");

                for (String e : emojis) {
                    String emoticon = config.getString("emojis." + e + ".emoticon");
                    String emoji = config.getString("emojis." + e + ".emoji");

                    if (emoticon != null && emoji != null) {
                        msgPlayer(sender, "&d" + emoticon + " &8&l➟ " + emoji);
                    }
                }
            } else {
                msgPlayer(sender, "&d&lEmojis &8&l➟ &cNo emojis found in the configuration.");
            }

            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = SupremeChat.getInstance().getConfig();

        if (cmd.getName().equalsIgnoreCase("emojis")) {
            if (config.isConfigurationSection("emojis")) {

                List<String> emojis = new ArrayList<>(config.getConfigurationSection("emojis").getKeys(false));

                msgPlayer(player, "&d&lEmojis &8&l➟ &7List: (" + emojis.size() + ")");

                for (String e : emojis) {
                    String emoticon = config.getString("emojis." + e + ".emoticon");
                    String emoji = config.getString("emojis." + e + ".emoji");

                    if (emoticon != null && emoji != null) {
                        emoji = replacePlaceholders(player, emoji);
                        msgPlayer(player, "&d" + emoticon + " &8&l➟ " + emoji);
                    }
                }
            } else {
                msgPlayer(player, "&d&lEmojis &8&l➟ &cNo emojis found in the configuration.");
            }
        }

        return true;
    }
}