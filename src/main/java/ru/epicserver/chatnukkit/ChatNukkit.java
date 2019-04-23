package ru.epicserver.chatnukkit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.math.Vector2;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import ru.epicserver.chatnukkit.lib.Helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChatNukkit extends PluginBase implements Listener {
    public static LuckPermsApi luckPerms = null;
    public static PlaceholderAPI placeholderApi = null;

    public void onEnable() {
        this.saveDefaultConfig();

        try {
            luckPerms = LuckPerms.getApiSafe().orElse(null);
        } catch (Throwable ignored) {

        }
        if (luckPerms == null) {
            this.getLogger().emergency("Unable to get LuckPerms API! Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check for Placeholder API
        try {
            placeholderApi = PlaceholderAPI.getInstance();
        } catch (Throwable e) {
            // ignore
        }


        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getLogger().info(TextFormat.YELLOW + "Enabling " + TextFormat.RED + "ChatNukkit");
    }

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        String name = p.getDisplayName();
        String message = e.getMessage();
        User user = luckPerms.getUser(p.getUniqueId());
        if (user == null) {
            this.getLogger().warning("An error occurred when attempting to retrieve " + p.getName() + "'s user data!");
            return;
        }
        Contexts contexts = luckPerms.getContextManager().getApplicableContexts(p);
        MetaData metaData = user.getCachedData().getMetaData(contexts);

        String prefix = metaData.getPrefix();
        prefix = prefix != null ? prefix : "Игрок";
        String msg;
        message = message.replace("§", "&");
        String perm = user.getPrimaryGroup();
        if (message.startsWith("!")) { // Global
            message = message.substring(1);
            msg = (" §7[§2G§7] §r§7[§f%prefix%§7] §r%name%§r: %msg%"
                    .replace("%name%", p.getName())
                    .replace("%disname%", name)
                    .replace("%prefix%", prefix)
                    .replace("%group%", perm)
                    .replace("%msg%", message));
        } else {
            msg = (" §7[L] §r§7[§f%prefix%§7] §r%name%§r: %msg%"
                    .replace("%name%", p.getName())
                    .replace("%disname%", name)
                    .replace("%prefix%", prefix)
                    .replace("%group%", perm)
                    .replace("%msg%", message));
            Map<UUID, Player> players = getServer().getOnlinePlayers();
            Set<CommandSender> receivers = new HashSet<>();
            Vector2 posPlayer = new Vector2(p.getX(), p.getZ());
            for (Map.Entry<UUID, Player> entry : players.entrySet()) {
                Vector2 posReceiver = new Vector2(entry.getValue().getX(), entry.getValue().getZ());
                if (posPlayer.distance(posReceiver) <= 100) {
                    receivers.add(Helper.getCommandSender(entry.getValue()));
                }
            }
            receivers.add(getServer().getConsoleSender());
            e.setRecipients(receivers);
        }
        if (placeholderApi != null) {
            msg = placeholderApi.translateString(msg, p);
        }
        if(p.hasPermission("chatnukkit.chatformat")) {
            e.setFormat(TextFormat.colorize('&', msg));
        } else {
            e.setFormat(TextFormat.colorize('§', msg));
        }
    }
}
