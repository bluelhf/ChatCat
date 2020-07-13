package io.github.bluelhf.chatcat;

import com.moderocky.mask.template.BukkitPlugin;
import io.github.bluelhf.chatcat.command.ChatCommand;
import io.github.bluelhf.chatcat.command.MuteCommand;
import io.github.bluelhf.chatcat.command.UnmuteCommand;
import io.github.bluelhf.chatcat.configuration.ChatConfig;
import io.github.bluelhf.chatcat.configuration.MuteConfig;
import io.github.bluelhf.chatcat.hook.PAPIHook;
import io.github.bluelhf.chatcat.hook.VaultHook;
import io.github.bluelhf.chatcat.listener.ChatFormatter;
import io.github.bluelhf.chatcat.listener.MuteHandler;
import io.github.bluelhf.chatcat.util.InputUtils;
import io.github.bluelhf.chatcat.util.TextUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public class ChatCat extends BukkitPlugin {
    private static ChatCat instance;

    private VaultHook vaultAPI;
    private PAPIHook PAPI;
    private MuteConfig muteCache;
    private Timer muteInvalidator = new Timer();

    private ChatConfig config;


    @Override
    public void startup() {
        instance = this;
        config = new ChatConfig();

        // Initialise mute cache and
        // the bukkit scheduler to save it every 30 minutes
        muteCache = new MuteConfig();
        muteInvalidator.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                muteCache.cleanExpired();
                muteCache.save();
            }
        }, 0, 1800000); // period is in ms, 1800000ms = 30min

        List<String> hooks = new ArrayList<>();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PAPI = new PAPIHook();
            hooks.add("PlaceholderAPI");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            vaultAPI = new VaultHook();
            hooks.add("Vault");
        }

        // Build hook string
        StringBuilder hookBuilder = new StringBuilder();
        for (int i = 0; i < hooks.size(); i++) {
            String delim = ", ";
            if (i == hooks.size() - 2) delim = hooks.size() != 2 ? ", and " : " and ";
            hookBuilder.append(hooks.get(i)).append(delim);
        }
        String hookString = hookBuilder.toString().length() >= 2 ? hookBuilder.toString().substring(0, hookBuilder.toString().length() - 2) + "!" : "nothing.";

        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new ChatFormatter(), this);
        Bukkit.getPluginManager().registerEvents(new MuteHandler(), this);
        register(new MuteCommand());
        register(new ChatCommand());
        register(new UnmuteCommand());

        Bukkit.getConsoleSender().spigot().sendMessage(TextUtils.fromLegacyString("   &b____ _           _   ", true));
        Bukkit.getConsoleSender().spigot().sendMessage(TextUtils.fromLegacyString("  &b/ ___| |__   __ _| |_ ", true));
        Bukkit.getConsoleSender().spigot().sendMessage(TextUtils.fromLegacyString(" &b| |   | '_ \\ / _` | __|    &3Chat &bv" + getDescription().getVersion(), true));
        Bukkit.getConsoleSender().spigot().sendMessage(TextUtils.fromLegacyString(" &b| |___| | | | (_| | |_     Hooked into " + hookString, true));
        Bukkit.getConsoleSender().spigot().sendMessage(TextUtils.fromLegacyString("  &b\\____|_| |_|\\__,_|\\__|", true));
        Bukkit.getConsoleSender().spigot().sendMessage(TextUtils.fromLegacyString("                             ", true));


    }

    @Override
    public void disable() {
        instance = null;
        config = null;
        muteInvalidator.cancel();
        muteInvalidator = null;
        muteCache.cleanExpired();
        muteCache.save();
        muteCache = null;
        vaultAPI = null;
    }


    public @Nullable VaultHook getVault() {
        return vaultAPI;
    }

    public @Nullable PAPIHook getPAPI() {
        return PAPI;
    }

    public @Nullable ChatConfig getChatConfig() {
        return config;
    }


    /**
     * Logs a message directly to the Minecraft logger, provided
     * that the plugin logging level is high enough.
     *
     * @param logLevel The level to log the message at
     * @param msg      The message to log
     * @param force    Whether to log the message regardless
     *                 of the server's logging level or not.
     * @param prefix   Whether to prefix the message with the plugin's
     *                 logging prefix (usually its name, unless otherwise defined in plugin.yml)
     */
    public void log(String msg, Level logLevel, boolean force, boolean prefix) {
        if (logLevel.intLevel() <= InputUtils.stringToLevel(config.logLevel, Level.INFO).intLevel()) {
            String prefixString = prefix ? "[" + (getDescription().getPrefix() != null ? getDescription().getPrefix() : getDescription().getName()) + "] " : "";
            msg = prefixString + msg;
            org.apache.logging.log4j.core.Logger mcLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();

            if (force) {
                // Fuck you, spigot 💘
                LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
                Configuration config = ctx.getConfiguration();

                LoggerConfig rootLoggerConfig = config.getLoggers().get("");
                getLogger().info(String.join(", ", config.getAppenders().keySet()));
                Appender oldTerminalConsole = config.getAppender("TerminalConsole");
                Appender oldServerGuiConsole = config.getAppender("ServerGuiConsole");
                Appender oldFile = config.getAppender("File");
                rootLoggerConfig.removeAppender("TerminalConsole");
                rootLoggerConfig.removeAppender("ServerGuiConsole");
                rootLoggerConfig.removeAppender("File");
                rootLoggerConfig.addAppender(oldTerminalConsole, Level.ALL, null);
                rootLoggerConfig.addAppender(oldServerGuiConsole, Level.ALL, null);
                rootLoggerConfig.addAppender(oldFile, null, null);
                ctx.updateLoggers();

                Level oldLogLevel = mcLogger.getLevel();
                mcLogger.setLevel(Level.ALL);
                mcLogger.log(logLevel, msg);

                mcLogger.setLevel(oldLogLevel);
                rootLoggerConfig.removeAppender("TerminalConsole");
                rootLoggerConfig.removeAppender("ServerGuiConsole");
                rootLoggerConfig.removeAppender("File");
                rootLoggerConfig.addAppender(oldTerminalConsole, Level.INFO, null);
                rootLoggerConfig.addAppender(oldServerGuiConsole, Level.INFO, null);
                rootLoggerConfig.addAppender(oldFile, Level.INFO, null);
                ctx.updateLoggers();
            } else {
                mcLogger.log(logLevel, msg);
            }
        }
    }

    public void log(String msg, Level logLevel, boolean force) {
        log(msg, logLevel, force, true);
    }

    public void log(String msg, Level logLevel) {
        log(msg, logLevel, false);
    }

    public void log(String msg) {
        log(msg, Level.INFO);
    }

    public void unmutePlayer(OfflinePlayer player) {
        muteCache.removeUUID(player.getUniqueId().toString());
    }

    public void mutePlayer(OfflinePlayer player, Duration duration, String reason, boolean soft) {
        String id = player.getUniqueId().toString();
        getMuteCache().removeUUID(id);
        HashMap<String, Object> muteEntry = new HashMap<>();
        muteEntry.put("uuid", id);
        muteEntry.put("expiry", duration.getSeconds() != -1 ? duration.getSeconds() + System.currentTimeMillis() / 1000.0 : -1);
        muteEntry.put("reason", reason);
        muteEntry.put("soft", String.valueOf(soft));
        muteCache.mutes.add(muteEntry);
    }


    public MuteConfig getMuteCache() {
        return muteCache;
    }

    public List<HashMap<String, Object>> getMutes() {
        return muteCache.mutes;
    }

    public void setMutes(List<HashMap<String, Object>> newMutes) {
        muteCache.mutes = newMutes;
    }

    public static ChatCat getInstance() {
        return instance;
    }

}
