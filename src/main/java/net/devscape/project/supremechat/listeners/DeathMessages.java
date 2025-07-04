package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static net.devscape.project.supremechat.utils.Message.format;

public class DeathMessages implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        if (SupremeChat.getInstance().getConfig().getBoolean("death.enable")) {
            String customMessage = getCustomDeathMessage(victim);

            if (!customMessage.isEmpty()) {
                event.setDeathMessage(format(customMessage));
            }
        }
    }

    private String getCustomDeathMessage(Player player) {
        String cause = player.getLastDamageCause() != null
                ? player.getLastDamageCause().getCause().toString()
                : "UNKNOWN";

        // Fetch message from the config using the cause
        String msg = SupremeChat.getInstance().getConfig().getString("death.messages." + cause.toLowerCase(), "&c&lDeath! &c" + player.getName() + " died.");
        msg = msg.replaceAll("%name%", player.getName());

        // Check if the player was killed by another player
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            msg = SupremeChat.getInstance().getConfig().getString("death.messages.entity_player");
            msg = msg.replaceAll("%name%", player.getName());

            // Replace %killer% with the player's killer's name
            msg = msg.replaceAll("%killer%", killer.getName());
        } else {
            msg = msg.replaceAll("%killer%", "Unknown");
        }

        // Check if the death was caused by a mob
        if (player.getLastDamageCause() != null && player.getLastDamageCause().getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) player.getLastDamageCause().getEntity();

            // Get the mob's name (use custom name if set, otherwise entity type)
            String mobName = (entity.getCustomName() != null) ? entity.getCustomName() : entity.getType().name().toLowerCase().replace("_", " ");

            // Replace the %mob% placeholder with the mob's name
            msg = msg.replaceAll("%mob%", mobName);
        } else {
            msg = msg.replaceAll("%mob%", "Unknown Mob");
        }
        return msg;
    }

}