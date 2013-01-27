package com.dumptruckman.spamhammer.api;

import org.bukkit.OfflinePlayer;

/**
 * The SpamHandler interface
 * 
 * Providing all important API methods to gain information
 * about a player and to commit actions on players
 * 
 * @author dumptruckman,slipcor
 */
public abstract interface SpamHandler {
    
    void banPlayer(final OfflinePlayer player);
    
    boolean beenKicked(final OfflinePlayer name);
    
    boolean beenMuted(final OfflinePlayer name);

    boolean handleChat(OfflinePlayer player, String message);
    
    boolean hasDuplicateMessages(final OfflinePlayer name);

    boolean isMuted(OfflinePlayer player);
    
    void kickPlayer(final OfflinePlayer player);
    
    void mutePlayer(final OfflinePlayer player);

    void removeKickHistory(OfflinePlayer player);

    void removeMuteHistory(OfflinePlayer player);
    
    void unBanPlayer(final OfflinePlayer player);

    void unMutePlayer(OfflinePlayer player);
    
}
