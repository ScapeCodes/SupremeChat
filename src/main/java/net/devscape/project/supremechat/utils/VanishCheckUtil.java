package net.devscape.project.supremechat.utils;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class VanishCheckUtil {

    public static boolean isVanished(Player player) {

        // For SuperVanish / PremiumVanish / VanishNoPacket / possibly more
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        // For Essentials
        if (Bukkit.getPluginManager().getPlugin("EssentialsX") != null) {
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("EssentialsX");
            if (essentials != null && essentials.isEnabled()) {
                User user = essentials.getUser(player);
                return user != null && user.isVanished();
            }
        }

        // Otherwise, player is not vanished
        return false;
    }

}
