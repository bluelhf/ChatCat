package io.github.bluelhf.chatcat.command;

import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.chatcat.ChatCat;
import io.github.bluelhf.chatcat.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NickCommand implements WrappedCommand {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            TextUtils.sendMessage(sender, ChatCat.get().getChatConfig().invalidArguments);
            return true;
        }

        ArrayList<String> newArgs = new ArrayList<>();
        String input = String.join(" ", args);
        boolean inQuote = false;
        int idx = 0;

        StringBuilder soFar = new StringBuilder();
        for (String chr : input.split("")) {
            if (!inQuote && chr.equals("\"") && input.indexOf('"', idx + 1) != -1) {
                inQuote = true;
                continue;
            } else if (chr.equals("\"")) {
                inQuote = false;
                continue;
            }

            if (!inQuote && chr.equals(" ")) {
                newArgs.add(soFar.toString());
                soFar = new StringBuilder();
            } else soFar.append(chr);
            idx++;
        }

        if (soFar.length() > 0) newArgs.add(soFar.toString());


        if (newArgs.size() == 1) {
            if (!(sender instanceof Player)) {
                TextUtils.sendMessage(sender, ChatCat.get().getChatConfig().mustSpecifyPlayer);
                return true;
            }

            setNick(sender, (Player)sender, newArgs.get(0));
        } else {
            String requiredPermission = "chatcat.nick.other";
            if (sender instanceof Player && newArgs.get(0).equalsIgnoreCase(sender.getName()))
                requiredPermission = "chatcat.nick";

            if (!sender.hasPermission(requiredPermission)) {
                TextUtils.sendMessage(sender, ChatCat.get().getChatConfig().permissionMessage);
                return true;
            }

            Player target;
            if ((target = getPlayer(newArgs.get(0))) == null) {
                TextUtils.sendMessage(sender, ChatCat.get().getChatConfig().neverPlayedBefore);
                return true;
            }

            setNick(sender, target, newArgs.get(1));
        }
        return true;
    }

    private @Nullable Player getPlayer(String name) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
            .filter(OfflinePlayer::isOnline)
            .map(op -> (Player) op)
            .filter(op -> op.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    private void setNick(CommandSender sender, Player target, String displayName) {
        target.setDisplayName(displayName);

        String targetString = target.getName() + "'s";
        if (sender instanceof Player && target.getUniqueId() == ((Player) sender).getUniqueId()) {
            targetString = "Your";
        }
        TextUtils.sendMessage(sender, String.format(ChatCat.get().getChatConfig().nickSet, targetString, displayName));
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
        return "Nickname command from ChatCat";
    }

    @Override
    public @Nullable String getPermission() {
        return "chatcat.nick";
    }

    @Override
    public @Nullable String getPermissionMessage() {
        return TextUtils.colour(ChatCat.get().getChatConfig().permissionMessage);
    }

    @Override
    public @NotNull String getCommand() {
        return "nick";
    }

    @Override
    public @Nullable List<String> getCompletions(int i) {
        return null;
    }

    @Override
    public @Nullable List<String> getCompletions(int i, @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .forEachOrdered(out::add);
            return out;
        } else if (args.length == 0) {
            out = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            return out;
        }

        return out;
    }
}
