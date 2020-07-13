package io.github.bluelhf.chatcat.configuration;

import com.moderocky.mask.annotation.Configurable;
import com.moderocky.mask.template.Config;
import org.jetbrains.annotations.NotNull;


public class ChatConfig implements Config {
    public ChatConfig() {
        load();
    }


    @Configurable
    @Configurable.Comment(text = {
            "Toggles prefixing messages entirely."
    })
    public boolean useMessagePrefix = true;

    @Configurable
    @Configurable.Keyed(nodeName="message-prefix")
    @Configurable.Comment(text = {
            "What to prefix messages with (default is '<$name&r>: ')"
    })
    public String messagePrefix = "<$name&r>: ";
    @Configurable(section = "messages.generic")
    @Configurable.Keyed(nodeName="no-permission")
    public String permissionMessage = "&cYou do not have permission to do that!";
    @Configurable(section = "messages.generic")
    @Configurable.Keyed(nodeName="invalid-arguments")
    public String invalidArguments = "&cInvalid arguments!";
    @Configurable(section = "messages.generic")
    @Configurable.Keyed(nodeName = "never-played-before")
    public String neverPlayedBefore = "&cThat player has never played before!";

    @Configurable(section = "messages.mutes")
    @Configurable.Keyed(nodeName="muted")
    public String muted = "&fPlayer &a%s &fwas muted for &a%s &fwith reason '&a%s&f'";
    @Configurable(section = "messages.mutes")
    @Configurable.Keyed(nodeName="muted-no-reason")
    public String mutednoreason = "&fPlayer &a%s &fwas muted for &a%s&f.";
    @Configurable(section = "messages.mutes")
    @Configurable.Keyed(nodeName="unmuted")
    public String unmuted = "&fPlayer &a%s &fwas unmuted.";
    @Configurable(section = "messages.mutes")
    @Configurable.Keyed(nodeName="not-muted")
    public String notmuted = "&cThat player is not muted!";
    @Configurable(section = "messages.mutes")
    @Configurable.Keyed(nodeName="mute-notification")
    public String muteMessage = "&fYou are &cmuted&f for &c%s&f! Reason: &c%s";
    @Configurable(section = "messages.mutes")
    @Configurable.Keyed(nodeName = "mute-notification-no-reason")
    public String muteMessageNoReason = "&fYou are &cmuted&f for &c%s&f!";

    @Configurable
    @Configurable.Keyed(nodeName="logLevelValue")
    @Configurable.Comment(text = {
            "You probably don't need to change this",
            "unless you're asked to."
    })
    public @NotNull String logLevel = "INFO";

    @Override
    public @NotNull String getFolderPath() {
        return "plugins/Chat/";
    }

    @Override
    public @NotNull String getFileName() {
        return "config.yml";
    }
}
