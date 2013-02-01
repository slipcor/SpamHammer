package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.Config.ConfigEntry;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.command.SpamReload;
import com.dumptruckman.spamhammer.command.SpamReset;
import com.dumptruckman.spamhammer.command.SpamSpam;
import com.dumptruckman.spamhammer.command.SpamUnmute;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Tracker;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The SpamHammer implementation class
 * 
 * Contains the actual functionality
 * 
 * @author dumptruckman,slipcor
 */
public class SpamHammerPlugin extends JavaPlugin implements SpamHammer {
    
    protected Config cfg = null;
    private SpamHandler spamHandler = null;
    private static final int CFGVERSION = 2;
    
    /**
     * Return the {@link com.dumptruckman.spamhammer.api.Config} instance
     * 
     * If necessary, create one
     * 
     * @return the instance
     */
    @Override
    public Config config() {
        if (this.cfg == null) {
        try
        {
          this.cfg = newConfigInstance();
          getLogger().info("Loaded config file!");
        } catch (IOException e) {
          getLogger().severe("Error loading config file!");
          getLogger().severe(e.getMessage());
          Bukkit.getPluginManager().disablePlugin(this);
          return null;
        }
      }
      return this.cfg;
    }
    
    /**
     * Get the SpamHandler instance
     * 
     * If necessary, create one
     * 
     * @return the instance
     */
    @Override
    public SpamHandler getSpamHandler() {
        if (this.spamHandler == null) {
            this.spamHandler = new DefaultSpamHandler(this);
        }
        return this.spamHandler;
    }
    
    /**
     * Create and return a new {@link com.dumptruckman.spamhammer.api.Config} instance
     * @return the instnace
     * @throws IOException 
     */
    private Config newConfigInstance() throws IOException {
        
        if (getConfig().getInt("ver", 0) < CFGVERSION) {
            getConfig().options().copyDefaults(true);
            getConfig().set("ver", CFGVERSION);
            saveConfig();
        }

        this.reloadConfig();
        return new Config(this);
    }
    
    /**
     * When disabling the plugin, stop the Tracker thread
     */
    @Override
    public void onDisable() {
        Tracker.stop();
    }
    
    /**
     * When enabling the plugin, initiate the Language, register
     * the events, commands and start the Tracker
     */
    @Override
    public void onEnable() {
        try {
            Language.init(this, config().get(ConfigEntry.LANGUAGE_FILE));
            Messager.normal(Language.LANGUAGE_FILE, Bukkit.getConsoleSender(), config().get(ConfigEntry.LANGUAGE_FILE));
        } catch (IOException e) {
            Messager.bad(Language.CFG_LOAD_ERROR, Bukkit.getConsoleSender());
            this.onDisable();
            return;
        }
        
        final PluginListener listener = new PluginListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        registerCommands();
        
        final Tracker trackMe = new Tracker(this);
        trackMe.start();
    }
    
    /**
     * Register the SpamHammer commands
     */
    private void registerCommands() {
        getCommand("spam").setExecutor(new SpamSpam(this));
        getCommand("spamreload").setExecutor(new SpamReload(this));
        getCommand("spamreset").setExecutor(new SpamReset(this));
        getCommand("spamunmute").setExecutor(new SpamUnmute(this));
    }
    
    /**
     * Reload the configuration
     */
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (cfg != null) {
            cfg.reload();
        }
    }
}
