package net.devscape.project.supremechat.chatgames.games;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.devscape.project.supremechat.utils.Message.msgPlayer;

public class MathGame implements Runnable {

    private final SupremeChat plugin;
    private final Runnable onEnd;

    public MathGame(SupremeChat plugin, Runnable onEnd) {
        this.plugin = plugin;
        this.onEnd = onEnd;
    }

    @Override
    public void run() {
        List<String> equations = plugin.getConfig().getStringList("chatgames.math.equations");
        if (equations.isEmpty()) return;

        String[] eqAndA = equations.get(new Random().nextInt(equations.size())).split(":");
        if (eqAndA.length != 2) return;

        String equation = eqAndA[0];
        String answer = eqAndA[1];

        List<String> announce = plugin.getConfig().getStringList("chatgames.strings.game-announce");
        for (String line : announce) {
            String parsed = line.replace("%game_name%", "Math Game")
                    .replace("%game_question%", equation);
            Bukkit.getOnlinePlayers().forEach(p -> msgPlayer(p, parsed));
        }

        AtomicBoolean solved = new AtomicBoolean(false);

        Listener listener = new Listener() {
            @EventHandler
            public void onChat(AsyncPlayerChatEvent event) {
                if (solved.get()) return;

                if (event.getMessage().equals(answer)) {
                    solved.set(true);
                    Bukkit.getOnlinePlayers().forEach(p -> msgPlayer(p, "&c&lC&6&lH&e&lA&a&lT&b&l &9&lG&d&lA&5&lM&c&lE&6&lS &8&l> &a" + event.getPlayer().getName() + " got the right answer!"));
                    SupremeChat.getInstance().getGameManager().executeRewardCommands(event.getPlayer(), "math");
                    HandlerList.unregisterAll(this);
                    onEnd.run();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, plugin);

        long expiryTicks = plugin.getConfig().getInt("chatgames.expiry-time", 30) * 20L;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!solved.get()) {
                HandlerList.unregisterAll(listener);
                Bukkit.getOnlinePlayers().forEach(p -> msgPlayer(p, "&c&lC&6&lH&e&lA&a&lT&b&l &9&lG&d&lA&5&lM&c&lE&6&lS &8&l> &cTime's up! The answer was: " + answer));
                onEnd.run();
            }
        }, expiryTicks);
    }
}