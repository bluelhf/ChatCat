package io.github.bluelhf.chatcat.configuration;

import com.moderocky.mask.annotation.Configurable;
import com.moderocky.mask.template.Config;
import io.github.bluelhf.chatcat.ChatCat;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Configurable.Overwrite public class MuteConfig implements Config {
    public MuteConfig() { load(); }

    @Configurable
    @Configurable.Keyed("mutes")
    public List<HashMap<String, Object>> mutes = new ArrayList<>();

    @Override
    public @NotNull String getFolderPath() {
        return "plugins/ChatCat/";
    }

    @Override
    public @NotNull String getFileName() {
        return "mutes.yml";
    }

    public void cleanExpired() {
        mutes.removeIf(muteEntry -> {
            double expiry = Double.parseDouble(muteEntry.getOrDefault("expiry", -1).toString());
            return ((long) expiry <= (System.currentTimeMillis() / 1000.0) && expiry != -1 /*-1 = inf. duration*/);
        });
    }
    public boolean removeUUID(String uuid) {
        return mutes.removeIf(muteEntry -> (muteEntry.get("uuid").toString().equalsIgnoreCase(uuid)));
    }


    public boolean isMuted(String uuid) {
        List<HashMap<String, Object>> duplicate = new ArrayList<>(mutes);
        return duplicate.removeIf(muteEntry -> (muteEntry.get("uuid").toString().equalsIgnoreCase(uuid)));
    }
}
