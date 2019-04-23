package ru.epicserver.chatnukkit.lib;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public class Helper {
    public static CommandSender getCommandSender(Player player) {
        return (CommandSender) player;
    }
}
