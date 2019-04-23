package ru.epicserver.chatnukkit;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;

public class ChatNukkit extends PluginBase implements Listener {
    private LuckPermsApi luckPerms;
    private PlaceholderAPI placeholderApi;

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

        try {
            placeholderApi = PlaceholderAPI.getInstance();
        } catch (Throwable ignored) {
        }


        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.getServer().getLogger().info(TextFormat.YELLOW + "Enabling " + TextFormat.RED + "ChatNukkit");
    }

    public LuckPermsApi getLuckPerms() {
        return luckPerms;
    }

    public PlaceholderAPI getPlaceholderApi() {
        return placeholderApi;
    }
}
