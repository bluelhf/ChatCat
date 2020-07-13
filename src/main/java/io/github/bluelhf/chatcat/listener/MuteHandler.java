package io.github.bluelhf.chatcat.listener;

import io.github.bluelhf.chatcat.ChatCat;
import io.github.bluelhf.chatcat.util.TextUtils;
import org.apache.logging.log4j.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.Duration;
import java.util.HashMap;

public class MuteHandler implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();

        ChatCat.getInstance().getMuteCache().cleanExpired();

        for (HashMap<String, Object> muteEntry : ChatCat.getInstance().getMutes()) {
            if (muteEntry.get("uuid").toString().equalsIgnoreCase(uuid)) {
                Double seconds = Double.parseDouble(muteEntry.getOrDefault("expiry", -1).toString());
                Duration duration = Duration.ofSeconds(seconds != -1 ? (long) Math.ceil(seconds - System.currentTimeMillis() / 1000.0) : -1);
                String humanReadable = TextUtils.humanReadableDuration(duration, true);
                ChatCat.getInstance().log(uuid + " tried to speak while muted: " + muteEntry.toString(), Level.DEBUG, true);
                if (Boolean.parseBoolean(String.valueOf(muteEntry.get("soft")))) {
                    e.getRecipients().clear();
                    e.getRecipients().add(e.getPlayer());
                    return;
                }
                e.setCancelled(true);
                if (!muteEntry.get("reason").toString().equalsIgnoreCase("")) {
                    e.getPlayer().spigot().sendMessage(TextUtils.fromLegacyString(String.format(
                            ChatCat.getInstance().getChatConfig().muteMessage,
                            humanReadable,
                            muteEntry.get("reason").toString()
                    ), true));
                } else {
                    e.getPlayer().spigot().sendMessage(TextUtils.fromLegacyString(String.format(ChatCat.getInstance().getChatConfig().muteMessageNoReason, humanReadable), true));
                }
                return;
            }
        }
    }
}
