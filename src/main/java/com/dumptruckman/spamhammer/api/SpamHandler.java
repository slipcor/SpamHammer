package com.dumptruckman.spamhammer.api;

import org.bukkit.OfflinePlayer;

public abstract interface SpamHandler {

    boolean handleChat(OfflinePlayer player, String message);
    
    boolean isMuted(OfflinePlayer player);

    void unMutePlayer(OfflinePlayer player);

    void removeKickHistory(OfflinePlayer player);

    void removeMuteHistory(OfflinePlayer player);
    
}
