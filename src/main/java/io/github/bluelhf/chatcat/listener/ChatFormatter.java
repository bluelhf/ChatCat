package io.github.bluelhf.chatcat.listener;

import io.github.bluelhf.chatcat.ChatCat;
import io.github.bluelhf.chatcat.util.TextUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFormatter implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        e.setCancelled(true);

        BaseComponent[] message = TextUtils.fromLegacyString(
                e.getFormat().contains("%") ? String.format(e.getFormat(), e.getPlayer().getDisplayName(), TextUtils.formatSafe(e.getMessage())) : e.getFormat(),
                e.getPlayer().hasPermission("chat.format")
        );

        for(Player recipient : e.getRecipients()) {
            recipient.spigot().sendMessage(message);
        }
    }

    // Ensure compatibility
    @EventHandler(priority = EventPriority.HIGHEST)
    public void compatibilitiser(AsyncPlayerChatEvent e) {
        if (ChatCat.getInstance().getChatConfig().useMessagePrefix) {
            e.setFormat(TextUtils.formatForEvent(ChatCat.getInstance().getChatConfig().messagePrefix, e) + TextUtils.formatSafe(e.getMessage()));
        }
    }



}
