package io.github.bluelhf.chatcat.command;

import com.moderocky.mask.template.WrappedCommand;
import io.github.bluelhf.chatcat.ChatCat;
import io.github.bluelhf.chatcat.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatCatCommand implements WrappedCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            TextUtils.sendMessage(commandSender, helpMessage(label));
        } else if (args[0].equalsIgnoreCase("reload")) {
            TextUtils.sendMessage(commandSender, "&bReloading...");
            long start = System.currentTimeMillis();
            ChatCat.get().getChatConfig().load();
            ChatCat.get().getMuteCache().save();
            TextUtils.sendMessage(commandSender, "&bReloaded in " + (System.currentTimeMillis() - start) + "ms.");
        } else { TextUtils.sendMessage(commandSender, helpMessage(label)); }
        return true;
    }

    private String helpMessage(String label) {
        return "&bCommands for ChatCat\n" +
               "&f  /" + label + " reload &7- &fReloads the configuration.\n" +
               "&f  /mute [-s] <player> <duration> [<reason>] &7- &fMutes a player.\n" +
               "&f  /unmute [-s] <player> &7- &fUnmutes a player.";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Arrays.asList("ccat", "cc", "chatc");
    }

    @Override
    public @NotNull String getUsage() {
        return TextUtils.colour("usage message");
    }

    @Override
    public @NotNull String getDescription() {
        return "Main command for ChatCat.";
    }

    @Override
    public @Nullable String getPermission() {
        return "chatcat.admin";
    }

    @Override
    public @Nullable String getPermissionMessage() {
        return TextUtils.colour(ChatCat.get().getChatConfig().permissionMessage);
    }

    @Override
    public @NotNull String getCommand() {
        return "chatcat";
    }

    @Override
    public @Nullable List<String> getCompletions(int i) {
        if (i == 1) return Collections.singletonList("reload");
        return null;
    }
}
