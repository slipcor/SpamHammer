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
import java.io.IOException;
import org.bukkit.Bukkit;

/**
 *
 * @author Chris
 */
public class SpamHammerPlugin extends SpamHammer {
    
    protected Config cfg = null;
    private SpamHandler spamHandler = null;
    
    /**
     * JavaPlugin constructor to not make the instantiation freak out
     */
    public SpamHammerPlugin() {
        super();
        // just do nothing
    }
    
    public SpamHammerPlugin(final SpamHammer plugin) {
        super();
        this.cfg = plugin.config();
    }
    
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
    
    @Override
    public SpamHandler getSpamHandler() {
        if (this.spamHandler == null) {
            this.spamHandler = new DefaultSpamHandler(this);
        }
        return this.spamHandler;
    }
    
    private Config newConfigInstance() throws IOException {
        
        if (getConfig().getInt("ver", 0) < 1) {
            getConfig().options().copyDefaults(true);
            getConfig().set("ver", 1);
            saveConfig();
        }

        this.reloadConfig();
        return new Config(this);
    }
    
    
    @Override
    public void onEnable() {
        try {
            Language.init(this, config().get(ConfigEntry.LANGUAGE_FILE));
        } catch (IOException e) {
            Messager.bad(Language.CFG_LOAD_ERROR, Bukkit.getConsoleSender());
            this.onDisable();
            return;
        }
        
        final PluginListener listener = new PluginListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        registerCommands();
    }
    
    private void registerCommands() {
        getCommand("spam").setExecutor(new SpamSpam(this));
        getCommand("spamreload").setExecutor(new SpamReload(this));
        getCommand("spamreset").setExecutor(new SpamReset(this));
        getCommand("spamunmute").setExecutor(new SpamUnmute(this));
    }
    
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (cfg != null) {
            cfg.reload();
        }
    }
}
