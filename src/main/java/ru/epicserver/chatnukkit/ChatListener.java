package ru.epicserver.chatnukkit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.math.Vector2;
import cn.nukkit.utils.TextFormat;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import ru.epicserver.chatnukkit.lib.Helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChatListener implements Listener {
    private final ChatNukkit plugin;

    ChatListener(ChatNukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        LuckPermsApi luckPerms = plugin.getLuckPerms();

        Player player = event.getPlayer();
        String message = event.getMessage();
        User user = luckPerms.getUser(player.getUniqueId());
        if (user == null) {
            plugin.getLogger().warning("An error occurred when attempting to retrieve " + player.getName() + "'s user data!");
            return;
        }
        String nick = Helper.getNick(player, plugin);
        String msg;
        message = message.replace("§", "&");
        String perm = user.getPrimaryGroup();
        if (message.startsWith("!")) { // Global
            message = message.substring(1);
            msg = (" §7[§2G§7] §r%nick%§r: %msg%"
                    .replace("%nick%", nick)
                    .replace("%group%", perm)
                    .replace("%msg%", message));
        } else {
            msg = (" §7[L] §r%nick%§r: %msg%"
                    .replace("%nick%", nick)
                    .replace("%group%", perm)
                    .replace("%msg%", message));
            Map<UUID, Player> players = plugin.getServer().getOnlinePlayers();
            Set<CommandSender> receivers = new HashSet<>();
            Vector2 posPlayer = new Vector2(player.getX(), player.getZ());
            for (Map.Entry<UUID, Player> entry : players.entrySet()) {
                Vector2 posReceiver = new Vector2(entry.getValue().getX(), entry.getValue().getZ());
                if (posPlayer.distance(posReceiver) <= 100) {
                    receivers.add(Helper.getCommandSender(entry.getValue()));
                }
            }
            receivers.add(plugin.getServer().getConsoleSender());
            event.setRecipients(receivers);
        }
        if (player.hasPermission("chatnukkit.chatformat")) {
            event.setFormat(TextFormat.colorize('&', msg));
        } else {
            event.setFormat(TextFormat.colorize('§', msg));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nick = Helper.getNick(player, plugin);
        String msg = " %nick%§e присоединился!";
        if (!player.playedBefore) {
            msg = " %nick%§e впервые зашел на сервер!";
        }
        msg = msg.replace("%nick%", nick).replace("&", "§");
        event.setJoinMessage(msg);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String nick = Helper.getNick(player, plugin);
        String msg = " %nick%§e вышел!";
        msg = msg.replace("%nick%", nick).replace("&", "§");
        event.setQuitMessage(msg);
    }
}
