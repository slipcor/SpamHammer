package com.dumptruckman.spamhammer.api;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class SpamHammer extends JavaPlugin {
    
    public abstract Config config();
    
    public abstract SpamHandler getSpamHandler();
    
}
