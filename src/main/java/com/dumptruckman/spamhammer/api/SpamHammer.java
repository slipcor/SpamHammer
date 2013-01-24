package com.dumptruckman.spamhammer.api;

import org.bukkit.plugin.Plugin;

public interface SpamHammer extends Plugin {
    
    Config config();
    
    SpamHandler getSpamHandler();
    
}
