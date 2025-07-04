package net.devscape.project.supremechat.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.devscape.project.supremechat.SupremeChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.devscape.project.supremechat.utils.FormatUtil.emojiReplacer;

public class Message {

    public static String PREFIX = "&#00ff94&lSupremeChat ➟ ";

    private static Pattern p1 = Pattern.compile("\\{#([0-9A-Fa-f]{6})\\}");
    private static Pattern p2 = Pattern.compile("&#([A-Fa-f0-9]){6}");
    private static Pattern p3 = Pattern.compile("#([A-Fa-f0-9]){6}");
    private static Pattern p4 = Pattern.compile("<#([A-Fa-f0-9])>{6}");
    private static Pattern p5 = Pattern.compile("<#&([A-Fa-f0-9])>{6}");

    private static final String LOGS_FOLDER = "logs";
    private static final String COMMANDS_FOLDER = "commands";
    private static final String CHAT_FOLDER = "chat";

    public static String format(String message) {

        if (isVersionLessThan("1.16")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            return message;
        } else {
            message = ChatColor.translateAlternateColorCodes('&', message);

            Matcher match = p1.matcher(message);
            while (match.find()) {
                getRGB(message);
            }

            Matcher hexMatcher = p1.matcher(message);
            while (hexMatcher.find()) {
                message = message.replace(hexMatcher.group(), ChatColor.of(hexMatcher.group().substring(1)).toString());
            }

            hexMatcher = p2.matcher(message);
            while (hexMatcher.find()) {
                message = message.replace(hexMatcher.group(), ChatColor.of(hexMatcher.group().substring(1)).toString());
            }

            hexMatcher = p3.matcher(message);
            while (hexMatcher.find()) {
                message = message.replace(hexMatcher.group(), ChatColor.of(hexMatcher.group()).toString());
            }

            hexMatcher = p4.matcher(message);
            while (hexMatcher.find()) {
                String hexColor = hexMatcher.group().substring(2, 8);
                message = message.replace(hexMatcher.group(), ChatColor.of(hexColor).toString());
            }

            hexMatcher = p5.matcher(message);
            while (hexMatcher.find()) {
                String hexColor = hexMatcher.group().substring(3, 9);
                message = message.replace(hexMatcher.group(), ChatColor.of(hexColor).toString());
            }

            message = message.replace("<black>", "§0")
                    .replace("<dark_blue>", "§1")
                    .replace("<dark_green>", "§2")
                    .replace("<dark_aqua>", "§3")
                    .replace("<dark_red>", "§4")
                    .replace("<dark_purple>", "§5")
                    .replace("<gold>", "§6")
                    .replace("<gray>", "§7")
                    .replace("<dark_gray>", "§8")
                    .replace("<blue>", "§9")
                    .replace("<green>", "§a")
                    .replace("<aqua>", "§b")
                    .replace("<red>", "§c")
                    .replace("<light_purple>", "§d")
                    .replace("<yellow>", "§e")
                    .replace("<white>", "§f")
                    .replace("<obfuscated>", "§k")
                    .replace("<bold>", "§l")
                    .replace("<strikethrough>", "§m")
                    .replace("<underlined>", "§n")
                    .replace("<italic>", "§o")
                    .replace("<reset>", "§r");

            return message;
        }
    }

    public static boolean isValidVersion(String version) {
        return version.matches("\\d+(\\.\\d+)*"); // Matches version strings like "1", "1.2", "1.2.3", etc.
    }

    public static boolean isVersionLessThan(String version) {
        String serverVersion = Bukkit.getVersion();
        String[] serverParts = serverVersion.split(" ")[2].split("\\.");
        String[] targetParts = version.split("\\.");

        for (int i = 0; i < Math.min(serverParts.length, targetParts.length); i++) {
            if (!isValidVersion(serverParts[i]) || !isValidVersion(targetParts[i])) {
                return false;
            }

            int serverPart = Integer.parseInt(serverParts[i]);
            int targetPart = Integer.parseInt(targetParts[i]);

            if (serverPart < targetPart) {
                return true;
            } else if (serverPart > targetPart) {
                return false;
            }
        }
        return serverParts.length < targetParts.length;
    }

    private static Pattern rgbPat = Pattern.compile("(?:#|0x)(?:[a-f0-9]{3}|[a-f0-9]{6})\\b|(?:rgb|hsl)a?\\([^\\)]*\\)");

    public static String getRGB(String msg) {
        String temp = msg;
        try {

            String status = "none";
            String r = "";
            String g = "";
            String b = "";
            Matcher match = rgbPat.matcher(msg);
            while (match.find()) {
                String color = msg.substring(match.start(), match.end());
                for (char character : msg.substring(match.start(), match.end()).toCharArray()) {
                    switch (character) {
                        case '(':
                            status = "r";
                            continue;
                        case ',':
                            switch (status) {
                                case "r":
                                    status = "g";
                                    continue;
                                case "g":
                                    status = "b";
                                    continue;
                                default:
                                    break;
                            }
                        default:
                            switch (status) {
                                case "r":
                                    r = r + character;
                                    continue;
                                case "g":
                                    g = g + character;
                                    continue;
                                case "b":
                                    b = b + character;
                                    continue;
                            }
                            break;
                    }


                }
                b = b.replace(")", "");
                Color col = new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
                temp = temp.replaceFirst("(?:#|0x)(?:[a-f0-9]{3}|[a-f0-9]{6})\\b|(?:rgb|hsl)a?\\([^\\)]*\\)", ChatColor.of(col) + "");
                r = "";
                g = "";
                b = "";
                status = "none";
            }
        } catch (Exception e) {
            return msg;
        }
        return temp;
    }

    public static String getGlobalFormat() {
        return SupremeChat.getInstance().getConfig().getString("format");
    }

    public static String getRankFormat(String rank) {
        return SupremeChat.getInstance().getConfig().getString("groups." + rank);
    }

    public static String getGlobalLeave() {
        return SupremeChat.getInstance().getConfig().getString("leave.global-format");
    }

    public static String getGroupLeave(String group) {
        return SupremeChat.getInstance().getConfig().getString("leave.groups." + group);
    }

    public static String getGlobalJoin() {
        return SupremeChat.getInstance().getConfig().getString("join.global-format");
    }

    public static String getGroupJoin(String group) {
        return SupremeChat.getInstance().getConfig().getString("join.groups." + group);
    }

    public static String addChatPlaceholders(String string, Player player) {
        // Replace emojis first
        string = emojiReplacer(player, string, false, false);

        // Replace player-specific placeholders
        string = string.replace("%name%", player.getName());
        string = string.replace("%world%", player.getLocation().getWorld().getName());
        string = string.replace("%x%", String.valueOf(player.getLocation().getX()));
        string = string.replace("%y%", String.valueOf(player.getLocation().getY()));
        string = string.replace("%z%", String.valueOf(player.getLocation().getZ()));
        string = string.replace("%xp%", String.valueOf(player.getLevel()));
        string = string.replace("%gamemode%", player.getGameMode().name());
        string = string.replace("%flying%", String.valueOf(player.isFlying()));

        // Apply additional PlaceholderAPI placeholders if enabled
        if (isPAPI() && PlaceholderAPI.containsPlaceholders(string)) {
            string = PlaceholderAPI.setPlaceholders(player, string);
        }

        return string;
    }


    public static String addOtherPlaceholders(String string, Player player) {
        string = emojiReplacer(player, string, false, false);

        string = string.replace("%name%", player.getName());
        string = string.replace("%player_name%", player.getName());
        string = string.replace("%world%", player.getLocation().getWorld().getName());
        string = string.replace("%x%", String.valueOf(player.getLocation().getX()));
        string = string.replace("%y%", String.valueOf(player.getLocation().getY()));
        string = string.replace("%z%", String.valueOf(player.getLocation().getZ()));
        string = string.replace("%xp%", String.valueOf(player.getLevel()));
        string = string.replace("%gamemode%", player.getGameMode().name());
        string = string.replace("%flying%", String.valueOf(player.isFlying()));
        string = replacePlaceholders(player, string);

        return string;
    }

    public static String deformat(String str) {
        return ChatColor.stripColor(format(str));
    }

    public static void msgPlayer(Player player, String... str) {
        for (String msg : str) {
            player.sendMessage(format(msg));
        }
    }

    public static String replacePlaceholders(Player p, String message) {
        String holders = message;

        if (isPAPI()) {
            if (PlaceholderAPI.containsPlaceholders(holders))
                holders = PlaceholderAPI.setPlaceholders(p, holders);
        }

        return holders;
    }

    public static boolean isPAPI() {
        return Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static void msgPlayer(CommandSender player, String... str) {
        for (String msg : str) {
            player.sendMessage(format(msg));
        }
    }

    public static void titlePlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(format(title), format(subtitle), fadeIn * 20, stay * 20, fadeOut * 20);
    }

    public static void soundPlayer(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static List<String> color(List<String> lore){
        return lore.stream().map(Message::format).collect(Collectors.toList());
    }

    public static TextComponent interactCreator(Player player, String str, String hover_message) {
        TextComponent mainComponent = new TextComponent(format(str));
        mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().create()));
        return mainComponent;
    }

    public static TextComponent setHoverBroadcastEvent(TextComponent component, List<String> hoverMessagesList, Player broadcastReceivers) {
        ComponentBuilder hoverMessageBuilder = new ComponentBuilder();
        int hoverLine = 0;
        for (String hoverMessage : hoverMessagesList) {
            hoverMessage = addOtherPlaceholders(hoverMessage, broadcastReceivers);
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(format(hoverMessage)));
            hoverMessageBuilder.append(textComponent);
            if (hoverLine != hoverMessagesList.size() - 1)
                hoverMessageBuilder.append("\n");
            hoverLine++;
        }
        BaseComponent[] hoverComponents = hoverMessageBuilder.create();
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponents));

        return component;
    }

    public static TextComponent setClickBroadcastEvent(TextComponent component, String click, Player player) {
        if (click == null || click.length() == 0)
            return component;
        switch (click.charAt(0)) {
            case '/':
                click = addOtherPlaceholders(click, player);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click));
                break;
            case '*':
                click = addOtherPlaceholders(click, player);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(1)));
                break;
            default:
                click = addOtherPlaceholders(click, player);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
                break;
        }
        return component;
    }

    public static void setClickBroadcastEvent(TextComponent component, String click) {
        if (click == null || click.length() == 0)
            return;
        switch (click.charAt(0)) {
            case '/':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click));
                return;
            case '*':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(1)));
                return;
        }

        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
    }

    /**
     * Creates a log entry in the appropriate folder (commands or chat) with the current date.
     * The logs are saved inside the plugin's data folder.
     *
     * @param player        The player who triggered the log entry.
     * @param message       The message to log.
     * @param isCommandLog  Whether the log is for a command or chat.
     */
    public static void createLog(Player player, String message, boolean isCommandLog) {
        // Get the current date and time
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // Determine the folder based on the log type
        String folderPath = isCommandLog ? COMMANDS_FOLDER : CHAT_FOLDER;

        // Create the log folder inside the plugin's data folder
        File logFolder = new File(SupremeChat.getInstance().getDataFolder(), LOGS_FOLDER + File.separator + folderPath);
        if (!logFolder.exists() && !logFolder.mkdirs()) {
            SupremeChat.getInstance().getLogger().warning("Failed to create log directory: " + logFolder.getPath());
            return;
        }

        // Create the log file for the current date if it doesn't exist
        File logFile = new File(logFolder, date + ".log");
        try {
            if (!logFile.exists() && !logFile.createNewFile()) {
                SupremeChat.getInstance().getLogger().warning("Failed to create log file: " + logFile.getPath());
                return;
            }

            // Format the log message with date, time, and player information
            String playerName = (player != null) ? player.getName() : "Console";
            String logEntry = String.format("%s / %s | %s: %s", date, time, playerName, message);

            // Write the log entry to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(logEntry);
                writer.newLine();
            }

        } catch (IOException e) {
            SupremeChat.getInstance().getLogger().severe("Error writing to log file: " + e.getMessage());
        }
    }
}