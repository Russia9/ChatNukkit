package ru.epicserver.chatnukkit.lib;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import ru.epicserver.chatnukkit.ChatNukkit;

public class Helper {
    public static CommandSender getCommandSender(Player player) {
        return (CommandSender) player;
    }

    public static String getNick(Player player, ChatNukkit plugin) {
        LuckPermsApi luckPerms = plugin.getLuckPerms();

        String nick = player.getName();
        User user = luckPerms.getUser(player.getUniqueId());
        if (user == null) {
            plugin.getLogger().warning("An error occurred when attempting to retrieve " + nick + "'s user data!");
            return "ERROR";
        }
        Contexts contexts = luckPerms.getContextManager().getApplicableContexts(player);
        MetaData metaData = user.getCachedData().getMetaData(contexts);
        String prefix = metaData.getPrefix();
        prefix = prefix != null ? prefix : "Игрок";

        return "§7[§f" + prefix + "§7] §r" + nick + "§r";
    }
}
