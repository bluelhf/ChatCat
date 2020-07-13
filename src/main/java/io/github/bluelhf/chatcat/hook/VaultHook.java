package io.github.bluelhf.chatcat.hook;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class VaultHook {
    private boolean usingVault;
    public VaultHook() {
        usingVault = Bukkit.getPluginManager().isPluginEnabled("Vault");
    }


    public boolean isUsingVault() { return usingVault; }

    @Nullable
    public Chat getVaultChat() {
        return usingVault ? (Chat) ((RegisteredServiceProvider) Bukkit.getServer().getServicesManager().getRegistration(Chat.class)).getProvider() : null;
    }
}
