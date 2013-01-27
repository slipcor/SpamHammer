package com.dumptruckman.spamhammer.api;

import org.bukkit.plugin.Plugin;

/**
 * The SpamHammer plugin class, extending {@link org.bukkit.plugin.Plugin}
 * 
 * @author dumptruckman,slipcor
 */
public interface SpamHammer extends Plugin {
    
    /**
     * Get the Config instance
     * 
     * @return the instance
     */
    Config config();
    
    /**
     * Get the SpamHandler plugin instance
     * 
     * @return the instance
     */
    SpamHandler getSpamHandler();
    
}
