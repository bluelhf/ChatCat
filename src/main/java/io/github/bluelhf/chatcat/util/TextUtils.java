package io.github.bluelhf.chatcat.util;

import io.github.bluelhf.chat.Chat;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.Duration;

public class TextUtils {
    public static void sendMessage(CommandSender p, String s) {
        p.spigot().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', s)));
    }

    public static String colour(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
    public static BaseComponent[] fromLegacyString(String s, boolean colour) {
        if (colour) { return TextComponent.fromLegacyText(colour(s)); } else { return TextComponent.fromLegacyText(s); }
    }

    public static String humanReadableDuration(Duration duration, boolean words) {
        if (duration.equals(Duration.ofSeconds(-1))) return "eternity";
        if (words) {
            return DurationFormatUtils.formatDurationWords(
                    duration.getSeconds() * 1000,
                    true,
                    true
            );
        } else {
            return DurationFormatUtils.formatDuration(
                    duration.getSeconds() * 1000,
                    "y'Y' M'M' d'D' H'h' m'm' s's'"
            );
        }
    }

    public static String formatForEvent(String s, AsyncPlayerChatEvent e) {
        return formatForPlayer(s, e.getPlayer());
    }

    public static String formatForPlayer(String s, Player p) {
        if (Chat.getInstance().getPAPI() != null) {
            s = Chat.getInstance().getPAPI().setPlaceholders(p, s);
        }

        s = formatSafe(s);
        String prefix = "", suffix = "";
        try {
            if (Chat.getInstance().getVault() != null) {
                prefix = Chat.getInstance().getVault().getVaultChat().getPlayerPrefix(p);
                suffix = Chat.getInstance().getVault().getVaultChat().getPlayerSuffix(p);
            }
        } catch (NullPointerException ignored) { }

        return ChatColor.translateAlternateColorCodes('&',
                s.replaceAll("\\$name", p.getName())
                 .replaceAll("\\$nick", p.getDisplayName())
                 .replaceAll("\\$prefix", prefix)
                 .replaceAll("\\$suffix", suffix)
        );
    }

    public static String formatSafe(String s) { return s.replace("%", "%%"); }
}
