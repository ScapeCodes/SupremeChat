package net.devscape.project.supremechat;

import net.devscape.project.supremechat.commands.ChannelCommand;
import net.devscape.project.supremechat.commands.EmojisCommands;
import net.devscape.project.supremechat.commands.SCCommand;
import net.devscape.project.supremechat.hooks.Metrics;
import net.devscape.project.supremechat.listeners.*;
import net.devscape.project.supremechat.managers.ChannelManager;
import net.devscape.project.supremechat.utils.FormatUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SupremeChat extends JavaPlugin {

    private static SupremeChat instance;
    private ChannelManager channelManager;

    private static Permission perms = null;
    private static Chat chat;

    private final List<Player> chatDelayList = new ArrayList<>();
    // prevention list for preventing bot attacks
    private final List<Player> prevention = new ArrayList<>();
    private final List<Player> commandDelayList = new ArrayList<>();
    private final Map<Player, String> lastMessage = new HashMap<>();
    private FormatUtil formattingUtils;

    public static SupremeChat getInstance() {
        return instance;
    }

    public static Chat getChat() {
        return chat;
    }

    @Override
    public void onEnable() {
        init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        chatDelayList.clear();
        lastMessage.clear();
        commandDelayList.clear();
    }

    private void init() {
        instance = this;

        saveDefaultConfig();
        configValidator();

        setupVault();

        channelManager = new ChannelManager();

        getCommand("supremechat").setExecutor(new SCCommand());
        getCommand("channel").setExecutor(new ChannelCommand());
        getCommand("emojis").setExecutor(new EmojisCommands());

        getServer().getPluginManager().registerEvents(new Formatting(), this);
        getServer().getPluginManager().registerEvents(new JoinLeave(), this);
        getServer().getPluginManager().registerEvents(new CommandFilter(), this);
        getServer().getPluginManager().registerEvents(new CustomCommands(), this);
        getServer().getPluginManager().registerEvents(new Mention(), this);
        getServer().getPluginManager().registerEvents(new CommandSpy(), this);
        getServer().getPluginManager().registerEvents(new DeathMessages(), this);

        callMetrics();
    }

    private boolean setupVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permProvider == null) {
            // Permission service not found
            return false;
        }

        perms = permProvider.getProvider();

        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
                .getRegistration(Chat.class);
        if (chatProvider == null) {
            // Chat service not found
            return false;
        }
        chat = chatProvider.getProvider();

        return true;
    }

    public static Permission getPermissions() {
        return perms;
    }


    public List<Player> getChatDelayList() {
        return chatDelayList;
    }

    public void reload() {
        super.reloadConfig();

        channelManager.reloadChannels();
    }

    public Map<Player, String> getLastMessage() {
        return lastMessage;
    }

    public List<Player> getCommandDelayList() {
        return commandDelayList;
    }

    public ChannelManager getChannelManager() { return channelManager; }

    private void callMetrics() {
        int pluginId = 18329;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfig().getString("language", "en")));

        metrics.addCustomChart(new Metrics.DrilldownPie("java_version", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            String javaVersion = System.getProperty("java.version");
            Map<String, Integer> entry = new HashMap<>();
            entry.put(javaVersion, 1);
            if (javaVersion.startsWith("1.7")) {
                map.put("Java 1.7", entry);
            } else if (javaVersion.startsWith("1.8")) {
                map.put("Java 1.8", entry);
            } else if (javaVersion.startsWith("1.9")) {
                map.put("Java 1.9", entry);
            } else {
                map.put("Other", entry);
            }
            return map;
        }));
    }

    public List<Player> getPrevention() {
        return prevention;
    }

    private void configValidator() {
        SupremeChat plugin = SupremeChat.getInstance();
        FileConfiguration config = plugin.getConfig();

        // Validate channels
        if (!config.isSet("channels")) {
            setDefaultChannels(config);
            plugin.saveConfig();
        } else {
            // Ensure permissions and colors are set for existing channels
            for (String key : config.getConfigurationSection("channels").getKeys(false)) {
                if (config.getString("channels." + key + ".permission") == null) {
                    config.set("channels." + key + ".permission", "None");
                    config.set("channels." + key + ".chat-color", "&7");
                }
            }
            plugin.saveConfig();
        }

        // Validate death messages.
        if (!config.isConfigurationSection("death")) {
            config.set("death.enable", true);
            config.set("death.messages.contact", "&c%name% was slain!");
            config.set("death.messages.entity_attack", "&e%name% was killed by a mob.");
            config.set("death.messages.fall", "&b%name% fell from a high place.");
            config.set("death.messages.drowning", "&3%name% drowned.");
            config.set("death.messages.fire", "&c%name% burned to death.");
            config.set("death.messages.projectile", "&d%name% was shot.");
            config.set("death.messages.magic", "&5%name% was killed by magic.");
            config.set("death.messages.suicide", "&7%name% took their own life.");
            config.set("death.messages.unknown", "&7%name% died mysteriously.");
            plugin.saveConfig();
        }

        if (!config.isSet("per-world-chat")) {
            config.set("per-world-chat", false);
            plugin.saveConfig();
        }

        // Validate emojis
        if (!config.isSet("emojis")) {
            setDefaultEmojis(config);
            plugin.saveConfig();
        }

        // Validate mentions
        if (!config.isSet("mention")) {
            setDefaultMentions(config);
            plugin.saveConfig();
        }
    }

    // Method to set default channels
    private void setDefaultChannels(FileConfiguration config) {
        config.set("channels.english.enable", true);
        config.set("channels.english.permission", "None");
        config.set("channels.english.chat-color", "&7");
        config.set("channels.english.format", "&e[ENGLISH] &7%name% &8➟ &7%message%");

        config.set("channels.spanish.enable", true);
        config.set("channels.spanish.permission", "None");
        config.set("channels.spanish.chat-color", "&7");
        config.set("channels.spanish.format", "&e[SPANISH] &7%name% &8➟ &7%message%");

        config.set("channels.french.enable", true);
        config.set("channels.french.permission", "None");
        config.set("channels.french.chat-color", "&7");
        config.set("channels.french.format", "&e[FRENCH] &7%name% &8➟ &7%message%");
    }

    // Method to set default emojis
    private void setDefaultEmojis(FileConfiguration config) {
        config.set("emojis.smile.emoticon", ":)");
        config.set("emojis.smile.emoji", "&e😊");
        config.set("emojis.sad.emoticon", ":(");
        config.set("emojis.sad.emoji", "&9😢");
        config.set("emojis.wink.emoticon", ";)");
        config.set("emojis.wink.emoji", "&6😉");
        config.set("emojis.thumbs_up.emoticon", ":+1:");
        config.set("emojis.thumbs_up.emoji", "&a👍");
        config.set("emojis.thumbs_down.emoticon", ":-1:");
        config.set("emojis.thumbs_down.emoji", "&c👎");
        config.set("emojis.heart.emoticon", "<3");
        config.set("emojis.heart.emoji", "&c❤");
        config.set("emojis.fire.emoticon", ":fire:");
        config.set("emojis.fire.emoji", "&c🔥");
        config.set("emojis.laugh.emoticon", ":D");
        config.set("emojis.laugh.emoji", "&a😄");
        config.set("emojis.cool.emoticon", "B)");
        config.set("emojis.cool.emoji", "&b😎");
        config.set("emojis.surprised.emoticon", ":o");
        config.set("emojis.surprised.emoji", "&e😲");
        config.set("emojis.angry.emoticon", ">:(");
        config.set("emojis.angry.emoji", "&4😠");
        config.set("emojis.party.emoticon", ":party:");
        config.set("emojis.party.emoji", "&d🎉");
        config.set("emojis.clap.emoticon", ":clap:");
        config.set("emojis.clap.emoji", "&a👏");
    }

    // Method to set default mentions
    private void setDefaultMentions(FileConfiguration config) {
        config.set("mention.player.permission", "supremechat.mention.player");
        config.set("mention.player.target", "@");
        config.set("mention.player.replacement", "&e%target%");
        config.set("mention.player.spaces", true);
        config.set("mention.player.sound.enable", true);
        config.set("mention.player.sound.sound", "ENTITY_LEVELUP");

        config.set("mention.everyone.permission", "supremechat.mention.everyone");
        config.set("mention.everyone.target", "@everyone");
        config.set("mention.everyone.replacement", "&e@everyone&f");
        config.set("mention.everyone.spaces", true);
        config.set("mention.everyone.sound.enable", true);
        config.set("mention.everyone.sound.sound", "ENTITY_LEVELUP");
    }


}