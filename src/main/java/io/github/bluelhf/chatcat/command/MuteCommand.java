package io.github.bluelhf.chatcat.command;

import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.chat.Chat;
import io.github.bluelhf.chat.util.InputUtils;
import io.github.bluelhf.chat.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MuteCommand implements WrappedCommand {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("chat.mutes.mute")) {
            TextUtils.sendMessage(sender, Chat.getInstance().getChatConfig().permissionMessage);
            return true;
        }

        if (args.length == 0) {
            TextUtils.sendMessage(sender, Chat.getInstance().getChatConfig().invalidArguments);
            return true;
        }

        Duration duration = Duration.ofSeconds(-1);
        String reason = "";

        HashMap<String, Boolean> flagMap = new HashMap<>();
        flagMap.put("-s", false);
        flagMap.put("-h", false);
        for (int i = 0; i < flagMap.size(); i++) {
            if (flagMap.containsKey(args[0])) {
                flagMap.put(args[0], true);
                args = Arrays.copyOfRange(args, 1, args.length);
            }
        }

        @SuppressWarnings("deprecation") // Because there is no other way.
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (!player.hasPlayedBefore()) {
            TextUtils.sendMessage(sender, Chat.getInstance().getChatConfig().neverPlayedBefore);
            return true;
        }

        // If there is a duration and/or a reason provided.
        // We need >= because reasons are more than 1 argument.
        if (args.length >= 2) {

            // To specify a reason, the user also has to
            // specify a duration. This could be changed, but I think it works nicely.
            if (InputUtils.inputToDuration(args[1]) == null) {
                TextUtils.sendMessage(sender, Chat.getInstance().getChatConfig().invalidArguments);
                return true;
            } else {
                // inputToDuration automatically adds P and T characters as specified by the
                // ISO-8601 duration format. (PnDTnHnMn.nS)
                duration = InputUtils.inputToDuration(args[1]);
            }

            // We have to do this because reasons can be more than 1 argument.
            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                builder.append(" ").append(args[i]);
            }

            // Here we have to remove the first character, which is a space, from the output of builder.toString()
            // We don't want to do this if the length of it is 0, though, since that'll give us an exception.
            reason = builder.toString().length() > 0 ? builder.toString().substring(1) : "";
        }

        Chat.getInstance().mutePlayer(player, duration, reason, flagMap.get("-s"));

        String humanDuration = (TextUtils.humanReadableDuration(duration, true));
        String message;
        if (reason.equalsIgnoreCase("")) {
            // Muted without reason
            message = TextUtils.colour(String.format(
                    Chat.getInstance().getChatConfig().mutednoreason,
                    player.getName(),
                    humanDuration
            ));
        } else {
            // Muted with reason
            message = TextUtils.colour(String.format(
                    Chat.getInstance().getChatConfig().muted,
                    player.getName(),
                    humanDuration,
                    reason
            ));
        }

        if (!flagMap.get("-h")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.spigot().sendMessage(TextUtils.fromLegacyString(message, true));
            }
            Bukkit.getConsoleSender().spigot().sendMessage((TextUtils.fromLegacyString(message, true)));
        } else {
            sender.spigot().sendMessage(TextUtils.fromLegacyString(message, true));
        }

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
        return "Mute command from Chat";
    }

    @Override
    public @Nullable String getPermission() {
        return "chat.mutes.mute";
    }

    @Override
    public @Nullable String getPermissionMessage() {
        return TextUtils.colour(Chat.getInstance().getChatConfig().permissionMessage);
    }

    @Override
    public @NotNull String getCommand() {
        return "mute";
    }

    @Override
    public @Nullable List<String> getCompletions(int i) {
        return null;
    }

    @Override
    public @Nullable List<String> getCompletions(int i, @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (i == 1) {
            out.addAll(Arrays.asList("-s", "-h"));
            out.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
        } else if (i == 2 && Bukkit.getPlayer(args[0]) != null) {
            out.addAll(Arrays.asList("P1d", "P30d", "PT12h", "PT1h"));
        } else if (i == 2 && Bukkit.getPlayer(args[0]) == null) {
            out.addAll(Arrays.asList("-s", "-h").stream()
                    .filter(s -> !args[0].equalsIgnoreCase(s))
                    .collect(Collectors.toList()));
        }
        return out;
    }
}
