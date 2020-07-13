package io.github.bluelhf.chatcat.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class PAPIHook {
    private boolean isEnabled;
    public PAPIHook() {
        isEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public boolean isEnabled() { return isEnabled; }
    public String setPlaceholders(Player player, String msg) {
        return PlaceholderAPI.setPlaceholders(player, msg);
    }
}
