package io.github.bluelhf.chatcat.command;

import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.chat.Chat;
import io.github.bluelhf.chat.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnmuteCommand implements WrappedCommand {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            TextUtils.sendMessage(commandSender, Chat.getInstance().getChatConfig().invalidArguments);
            return true;
        }
        boolean hidden = false;
        if (args[0].equalsIgnoreCase("-h")) {
            hidden = true;
            if (args.length - 1 - 1 >= 0) System.arraycopy(args, 2, args, 1, args.length - 1 - 1);
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (!player.hasPlayedBefore()) {
            TextUtils.sendMessage(commandSender, Chat.getInstance().getChatConfig().neverPlayedBefore);
            return true;
        }
        if (!Chat.getInstance().getMuteCache().isMuted(player.getUniqueId().toString())) {
            TextUtils.sendMessage(commandSender, Chat.getInstance().getChatConfig().notmuted);
            return true;
        }
        Chat.getInstance().unmutePlayer(player);
        String message = TextUtils.colour(String.format(
                Chat.getInstance().getChatConfig().unmuted,
                player.getName()
        ));

        if (!hidden) {
            for (Player p : Bukkit.getOnlinePlayers()) { p.spigot().sendMessage(TextUtils.fromLegacyString(message, true)); }
            Bukkit.getConsoleSender().spigot().sendMessage((TextUtils.fromLegacyString(message, true)));
        } else { commandSender.spigot().sendMessage(TextUtils.fromLegacyString(message, true)); }

        return true;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }

    @Override
    public @NotNull String getDescription() {
        return "Unmute command for Chat";
    }

    @Override
    public @Nullable String getPermission() {
        return "chat.mutes.unmute";
    }

    @Override
    public @Nullable String getPermissionMessage() {
        return TextUtils.colour(Chat.getInstance().getChatConfig().permissionMessage);
    }

    @Override
    public @NotNull String getCommand() {
        return "unmute";
    }

    @Override
    public @Nullable List<String> getCompletions(int i) {
        return null;
    }

    @Override
    public @Nullable List<String> getCompletions(int i, @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (i == 1) {
            out.add("-h");
            out.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
        } else if (i == 2 && args[0].equalsIgnoreCase("-h")) {
            out.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
        }
        return out;
    }
}
