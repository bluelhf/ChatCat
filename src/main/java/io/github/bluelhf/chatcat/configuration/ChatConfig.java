package io.github.bluelhf.chatcat.configuration;

import com.moderocky.mask.annotation.Configurable;
import com.moderocky.mask.template.Config;
import org.jetbrains.annotations.NotNull;


public class ChatConfig implements Config {
    public ChatConfig() {
        load();
    }


    @Configurable
    public boolean useMessagePrefix = true;

    @Configurable
    @Configurable.Keyed("message-prefix")
    public String messagePrefix = "<$name&r>: ";

    @Configurable("messages.generic")
    @Configurable.Keyed("no-permission")
    public String permissionMessage = "&cYou do not have permission to do that!";

    @Configurable("messages.generic")
    @Configurable.Keyed("invalid-arguments")
    public String invalidArguments = "&cInvalid arguments!";

    @Configurable("messages.generic")
    @Configurable.Keyed("never-played-before")
    public String neverPlayedBefore = "&cThat player has never played before!";

    @Configurable("messages.mutes")
    @Configurable.Keyed("muted")
    public String muted = "&fPlayer &a%s &fwas muted for &a%s &fwith reason '&a%s&f'";

    @Configurable("messages.mutes")
    @Configurable.Keyed("muted-no-reason")
    public String mutedNoReason = "&fPlayer &a%s &fwas muted for &a%s&f.";
    @Configurable("messages.mutes")
    @Configurable.Keyed("unmuted")
    public String unmuted = "&fPlayer &a%s &fwas unmuted.";

    @Configurable("messages.mutes")
    @Configurable.Keyed("not-muted")
    public String notmuted = "&cThat player is not muted!";

    @Configurable("messages.mutes")
    @Configurable.Keyed("mute-notification")
    public String muteMessage = "&fYou are &cmuted&f for &c%s&f! Reason: &c%s";

    @Configurable("messages.mutes")
    @Configurable.Keyed("mute-notification-no-reason")
    public String muteMessageNoReason = "&fYou are &cmuted&f for &c%s&f!";

    @Configurable
    @Configurable.Keyed("logLevelValue")
    public @NotNull String logLevel = "INFO";
    @Configurable
    @Configurable.Keyed("sam_mode")
    public boolean samMode = false;

    @Override
    public @NotNull String getFolderPath() {
        return "plugins/ChatCat/";
    }

    @Override
    public @NotNull String getFileName() {
        return "config.yml";
    }
}
