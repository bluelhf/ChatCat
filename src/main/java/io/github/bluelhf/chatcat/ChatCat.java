package io.github.bluelhf.chatcat;

import com.moderocky.mask.template.BukkitPlugin;
import io.github.bluelhf.chatcat.command.ChatCatCommand;
import io.github.bluelhf.chatcat.command.MuteCommand;
import io.github.bluelhf.chatcat.command.UnmuteCommand;
import io.github.bluelhf.chatcat.configuration.ChatConfig;
import io.github.bluelhf.chatcat.configuration.MuteConfig;
import io.github.bluelhf.chatcat.hook.PAPIHook;
import io.github.bluelhf.chatcat.hook.VaultHook;
import io.github.bluelhf.chatcat.listener.ChatFormatter;
import io.github.bluelhf.chatcat.listener.MuteHandler;
import io.github.bluelhf.chatcat.util.TextUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public class ChatCat extends BukkitPlugin {

    public static String[] BANNER = {
        "&b_________ .__            __   _________         __",
        "&b\\_   ___ \\|  |__ _____ _/  |_ \\_   ___ \\_____ _/  |_",
        "&b/    \\  \\/|  |  \\\\__  \\\\   __\\/    \\  \\/\\__  \\\\   __\\    &3ChatCat &bv{ver}",
        "&b\\     \\___|   Y  \\/ __ \\|  |  \\     \\____/ __ \\|  |      Hooked into {hooks}",
        "&b \\______  /___|  (____  /__|   \\______  (____  /__|",
        "&b        \\/     \\/     \\/              \\/     \\/",
        ""
    };

    private VaultHook vaultAPI;
    private PAPIHook PAPI;

    private MuteConfig muteCache;
    private Timer muteInvalidator = new Timer();

    private ChatConfig config;

    @Override
    public void startup() {
        long timeTracker = System.currentTimeMillis();
        config = new ChatConfig();
        log("Initialised config in " + (System.currentTimeMillis() - timeTracker) + "ms", Level.TRACE);


        timeTracker = System.currentTimeMillis();
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
        log("Initialised mute cache in " + (System.currentTimeMillis() - timeTracker) + "ms", Level.TRACE);


        timeTracker = System.currentTimeMillis();
        List<String> hooks = new ArrayList<>();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PAPI = new PAPIHook();
            hooks.add("PAPI");
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

        log("Loaded " + hooks.size() + " hooks in " + (System.currentTimeMillis() - timeTracker) + "ms", Level.TRACE);
        timeTracker = System.currentTimeMillis();
        Bukkit.getPluginManager().registerEvents(new ChatFormatter(), this);
        Bukkit.getPluginManager().registerEvents(new MuteHandler(), this);
        register(new MuteCommand());
        register(new ChatCatCommand());
        register(new UnmuteCommand());
        log("Registered events and commands in " + (System.currentTimeMillis() - timeTracker) + "ms", Level.TRACE);

        for (String s : BANNER) {
            BaseComponent[] line = TextUtils.fromLegacyString(s
                .replace("{ver}", getDescription().getVersion())
                .replace("{hooks}", hookString), true);
            Bukkit.getConsoleSender().spigot().sendMessage(line);
        }

    }

    @Override
    public void disable() {
        config = null;
        muteInvalidator.cancel();
        muteInvalidator = null;
        muteCache.cleanExpired();
        muteCache.save();
        muteCache = null;
        vaultAPI = null;
    }


    public VaultHook getVault() {
        return vaultAPI;
    }

    public PAPIHook getPAPI() {
        return PAPI;
    }

    public ChatConfig getChatConfig() {
        return config;
    }


    /**
     * Logs a message if the config log level allows it.
     *
     * @param logLevel The level to log the message at
     * @param msg      The message to log
     *                 logging prefix (usually its name, unless otherwise defined in plugin.yml)
     */
    public void log(String msg, Level logLevel) {
        if (logLevel.intLevel() <= Level.toLevel(config.logLevel, Level.INFO).intLevel())
            getLogger().info(msg);
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

    public static @NotNull ChatCat get() {
        return (ChatCat) JavaPlugin.getProvidingPlugin(ChatCat.class);
    }

}
