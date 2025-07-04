package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static net.devscape.project.supremechat.utils.Message.format;
import static org.bukkit.Bukkit.getServer;

public class Mention implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMention(AsyncPlayerChatEvent event) {
        if (event.isCancelled() || event.getMessage().startsWith("/")) return;

        String message = event.getMessage();
        Player sender = event.getPlayer();

        String targetString = SupremeChat.getInstance().getConfig().getString("mention.everyone.target");
        String e_permission = SupremeChat.getInstance().getConfig().getString("mention.everyone.permission");
        String p_permission = SupremeChat.getInstance().getConfig().getString("mention.player.permission");

        String targetStringPlayer = SupremeChat.getInstance().getConfig().getString("mention.player.target");

        // Check for "@everyone" mention if the sender has permission
        if (targetString != null && message.contains(targetString) && e_permission != null && sender.hasPermission(e_permission)) {
            boolean addSpaces = SupremeChat.getInstance().getConfig().getBoolean("mention.everyone.spaces");
            if (addSpaces) {
                for (Player all : getServer().getOnlinePlayers()) {
                    all.sendMessage("");
                }
            }

            handleMention(event, sender, "@everyone", "everyone");
            return;
        }

        // Check for individual player mentions
        Player target = null;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (message.contains(onlinePlayer.getName())) {
                target = onlinePlayer;
                break;
            }
        }

        // No target found or mentioning self
        //if (target == null || target.getName().equalsIgnoreCase(sender.getName())) return;

        // Handle individual player mention if sender has permission
        if (target != null && p_permission != null && message.contains(targetStringPlayer + target.getName()) && sender.hasPermission(p_permission)) {
            boolean addSpaces = SupremeChat.getInstance().getConfig().getBoolean("mention.player.spaces");
            if (addSpaces) {
                target.sendMessage("");
            }

            handleMention(event, target, target.getName(), "player");
        }
    }

    // Helper method to handle mentions
    private void handleMention(AsyncPlayerChatEvent event, Player target, String mentionText, String type) {
        SupremeChat plugin = SupremeChat.getInstance();
        String replacement = plugin.getConfig().getString("mention." + type + ".replacement");
        boolean addSpaces = plugin.getConfig().getBoolean("mention." + type + ".spaces");
        String soundConfig = plugin.getConfig().getString("mention." + type + ".sound.sound");
        boolean playSound = plugin.getConfig().getBoolean("mention." + type + ".sound.enable");

        boolean isEveryone = type.equalsIgnoreCase("everyone");

        // Format the replacement text
        if (replacement != null) {
            if (target != null) {
                replacement = replacement.replaceAll("%target%", target.getName());
            }
            event.setMessage(event.getMessage().replaceAll(mentionText, format(replacement)));

            // Add spaces if configured
            if (addSpaces) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (isEveryone) {
                            for (Player all : getServer().getOnlinePlayers()) {
                                all.sendMessage("");
                            }
                        } else {
                            target.sendMessage("");
                        }

                    }
                }.runTaskLater(plugin, 20L);
            }
        }

        // Play sound if enabled
        if (playSound && soundConfig != null) {
            if (!isEveryone) {
                try {
                    Sound sound = Sound.valueOf(soundConfig.toUpperCase());
                    target.playSound(target.getLocation(), sound, 1, 1);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid sound: " + soundConfig);
                }
            } else {
                for (Player all : getServer().getOnlinePlayers()) {
                    Sound sound = Sound.valueOf(soundConfig.toUpperCase());
                    all.playSound(all.getLocation(), sound, 1, 1);
                }
            }
        }
    }
}