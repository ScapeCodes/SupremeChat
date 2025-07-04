package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import net.devscape.project.supremechat.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.devscape.project.supremechat.utils.Message.*;
import static net.devscape.project.supremechat.utils.VanishCheckUtil.isVanished;

public class JoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        boolean firstjoin = SupremeChat.getInstance().getConfig().getBoolean("first-join.enable");
        String first_joinmsg = SupremeChat.getInstance().getConfig().getString("first-join.announce-format");

        if (SupremeChat.getInstance().getConfig().getBoolean("vanish-support")) {
            if (isVanished(player)) {
                e.setJoinMessage(null);
                return;
            }
        }

        if (!player.hasPlayedBefore()) {
            if (firstjoin) {
                e.setJoinMessage(null);
                Bukkit.broadcastMessage(format(first_joinmsg));
            }
        }

        if (SupremeChat.getInstance().getConfig().getBoolean("enable-join-motd")) {
            for (String motd : SupremeChat.getInstance().getConfig().getStringList("motd")) {
                motd = addOtherPlaceholders(motd, player);
                msgPlayer(player, motd);
            }
        }

        if (SupremeChat.getInstance().getConfig().getBoolean("join.enable")) {
            if (SupremeChat.getInstance().getConfig().getBoolean("join.enable-groups")) {
                String joinGroup;
                if (getGroupJoin(FormatUtil.getRank(player)) != null) {
                    joinGroup = getGroupJoin(FormatUtil.getRank(player));
                } else {
                    joinGroup = getGlobalJoin();
                }
                joinGroup = addOtherPlaceholders(joinGroup, player);

                if (!joinGroup.isEmpty()) {
                    e.setJoinMessage(format(joinGroup));
                } else {
                    e.setJoinMessage(null);
                }
            } else {
                String joinGlobal = getGlobalJoin();
                joinGlobal = addOtherPlaceholders(joinGlobal, player);

                if (!joinGlobal.isEmpty()) {
                    e.setJoinMessage(format(joinGlobal));
                } else {
                    e.setJoinMessage(null);
                }
            }
        }

        if (SupremeChat.getInstance().getConfig().getBoolean("custom-title-join.enable")) {
            String top = SupremeChat.getInstance().getConfig().getString("custom-title-join.top");
            String bottom = SupremeChat.getInstance().getConfig().getString("custom-title-join.bottom");

            top = addOtherPlaceholders(top, player);
            bottom = addOtherPlaceholders(bottom, player);

            int fade_in = SupremeChat.getInstance().getConfig().getInt("custom-title-join.fade-in");
            int stay = SupremeChat.getInstance().getConfig().getInt("custom-title-join.stay");
            int fade_out = SupremeChat.getInstance().getConfig().getInt("custom-title-join.fade-out");

            titlePlayer(player, top, bottom, fade_in, stay, fade_out);
        }

        boolean anti_bot = SupremeChat.getInstance().getConfig().getBoolean("anti-bot.commands") || SupremeChat.getInstance().getConfig().getBoolean("anti-bot.chat");

        if (anti_bot) {
            SupremeChat.getInstance().getPrevention().add(player);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (SupremeChat.getInstance().getConfig().getBoolean("vanish-support")) {
            if (isVanished(player)) {
                e.setQuitMessage(null);
                return;
            }
        }

        if (SupremeChat.getInstance().getConfig().getBoolean("leave.enable")) {

            String leaveGlobal = getGlobalLeave();
            leaveGlobal = addOtherPlaceholders(leaveGlobal, player);

            String leaveGroup;

            if (getGroupLeave(FormatUtil.getRank(player)) != null) {
                leaveGroup = getGroupLeave(FormatUtil.getRank(player));
            } else {
                leaveGroup = getGlobalLeave();
            }

            leaveGroup = addOtherPlaceholders(leaveGroup, player);

            if (SupremeChat.getInstance().getConfig().getBoolean("leave.enable-groups")) {
                if (!leaveGroup.isEmpty()) {
                    e.setQuitMessage(format(leaveGroup));
                } else {
                    e.setQuitMessage(null);
                }
            } else {
                if (!leaveGlobal.isEmpty()) {
                    e.setQuitMessage(format(leaveGlobal));
                } else {
                    e.setQuitMessage(null);
                }
            }
        }

        SupremeChat.getInstance().getLastMessage().remove(player);
        SupremeChat.getInstance().getChatDelayList().remove(player);
        SupremeChat.getInstance().getCommandDelayList().remove(player);
        SupremeChat.getInstance().getPrevention().remove(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        boolean anti_bot = SupremeChat.getInstance().getConfig().getBoolean("anti-bot.commands") || SupremeChat.getInstance().getConfig().getBoolean("anti-bot.chat");

        if (anti_bot) {
            SupremeChat.getInstance().getPrevention().remove(e.getPlayer());
        }
    }
}