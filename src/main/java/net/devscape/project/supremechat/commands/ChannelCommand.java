package net.devscape.project.supremechat.commands;

import net.devscape.project.supremechat.SupremeChat;
import net.devscape.project.supremechat.object.Channel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.devscape.project.supremechat.utils.Message.msgPlayer;

public class ChannelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {

            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("channel")) {
                if (player.hasPermission("supremechat.channel") || player.isOp()) {
                    if (args.length == 0) {
                        whatChannel(player);
                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("leave")) {
                            if (!SupremeChat.getInstance().getChannelManager().isInChannel(player)) {
                                msgPlayer(player, "&e&lChannel &8&l➟ &cYou are not in a channel.");
                                return true;
                            }

                            String channel = SupremeChat.getInstance().getChannelManager().getChannel(player).getName();

                            SupremeChat.getInstance().getChannelManager().getPlayerChannel().remove(player.getUniqueId());
                            msgPlayer(player, "&e&lChannel &8&l➟ &7You have left channel: &e" + channel);
                        } else if (args[0].equalsIgnoreCase("help")) {
                            whatChannel(player);
                        } else if (args[0].equalsIgnoreCase("list")) {
                            List<String> channels = new ArrayList<>();

                            for (Channel c : SupremeChat.getInstance().getChannelManager().channels) {
                                if (player.hasPermission(c.getPermission()) || c.getPermission().equalsIgnoreCase("None")) {
                                    channels.add(c.getName());
                                }
                            }

                            String formatted = channels.toString().replace("[", "").replace("]", "");

                            if (channels.isEmpty()) {
                                formatted = "&cNo channels found for you!";
                            }

                            msgPlayer(player, "&e&lAvailable Channels &8&l➟ &e" + formatted);
                        }
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("join")) {
                            String channel = args[1];

                            if (!SupremeChat.getInstance().getChannelManager().isChannel(channel)) {
                                msgPlayer(player, "&e&lChannel &8&l➟ &cThis channel does not exist.");
                                return true;
                            }

                            Channel c = SupremeChat.getInstance().getChannelManager().getChannel(channel);

                            if (!c.getPermission().equalsIgnoreCase("None") && !player.hasPermission(c.getPermission())) {
                                msgPlayer(player, "&e&lChannel &8&l➟ &cYou do not have permission to join this channel.");
                                return true;
                            }

                            if (SupremeChat.getInstance().getChannelManager().getChannel(player) != null && SupremeChat.getInstance().getChannelManager().getChannel(player).getName().equalsIgnoreCase(channel)) {
                                msgPlayer(player, "&e&lChannel &8&l➟ &cYou are already in this channel.");
                                return true;
                            }

                            SupremeChat.getInstance().getChannelManager().addToChannel(player, channel);
                            msgPlayer(player, "&e&lChannel &8&l➟ &7You have been added to " + channel + " channel!");
                        }
                    }
                } else {
                    msgPlayer(player, "&cNo Permission!");
                }
            }
        }
        return false;
    }

    public void whatChannel(Player player) {
        if (SupremeChat.getInstance().getChannelManager().isInChannel(player)) {
            msgPlayer(player,
                    "&6&l> &7You are in channel: &e&l" + SupremeChat.getInstance().getChannelManager().getChannel(player).getName(),
                    "&6&l> &f/channel join <name> &7- To switch channel!",
                    "&6&l> &f/channel list &7- List all available channels!",
                    "&6&l> &f/channel leave &7- To leave the channel you are in!");
        } else {
            msgPlayer(player,
                    "&6&l> &7You are in channel: &c&lNONE",
                    "&6&l> &f/channel join <name> &7- To switch channel!",
                    "&6&l> &f/channel list &7- List all available channels!",
                    "&6&l> &f/channel leave &7- To leave the channel you are in!");
        }
    }
}