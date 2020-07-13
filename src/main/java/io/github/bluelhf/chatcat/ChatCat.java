package io.github.bluelhf.chatcat;

import com.moderocky.mask.template.Plugin;

public class ChatCat extends Plugin {
    private static ChatCat instance;

    @Override
    public void startup() {
        instance = this;
    }

    @Override
    public void disable() { }

    public static ChatCat getInstance() {
        return instance;
    }
}
