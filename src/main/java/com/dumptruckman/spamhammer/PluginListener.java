package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.Config.ConfigEntry;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * The listener class
 * 
 * @author dumptruckman,slipcor
 */
public class PluginListener implements Listener {

    private final SpamHandler handler;
    private final Config config;
    private final boolean checkIPs;
    private final boolean checkURLs;

    public PluginListener(final SpamHammer plugin) {
        this.handler = plugin.getSpamHandler();
        this.config = plugin.config();
        
        checkIPs = config.getBoolean(ConfigEntry.CHECKIPS);
        checkURLs = config.getBoolean(ConfigEntry.CHECKURLS);
    }

    /**
     * Listen to chatting players
     * 
     * @param event the async player chat event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        // TODO change this to detect long messages and see if they're different. could then assume chat mod in use.
        if (handler.isMuted(event.getPlayer()) && !Perms.BYPASS_MUTE.has(event.getPlayer())) {
            event.setCancelled(true);
            Messager.bad(Language.MUTED, event.getPlayer());
            return;
        }
        
        if (handler.handleChat(event.getPlayer(), event.getMessage())
                && config.getBoolean(ConfigEntry.PREVENT_MESSAGES) && !Perms.BYPASS_REPEAT.has(event.getPlayer())) {
            event.setCancelled(true);
            Messager.bad(Language.SPAMMING_MESSAGE, event.getPlayer());
            return;
        }
        
        if (this.checkIPs && handler.handleChatIP(event.getPlayer(), event.getMessage())
                && !Perms.BYPASS_IPS.has(event.getPlayer())) {
            Messager.normal(Language.LOG_CHECK_IP, Bukkit.getConsoleSender(),
                    event.getPlayer().getName() + ": " + event.getMessage());
            event.setCancelled(true);
            Messager.bad(Language.SPAMMING_MESSAGE_IP, event.getPlayer());
            return;
        }
        
        if (this.checkURLs && handler.handleChatURL(event.getPlayer(), event.getMessage())
                && !Perms.BYPASS_IPS.has(event.getPlayer())) {
            Messager.normal(Language.LOG_CHECK_URL, Bukkit.getConsoleSender(),
                    event.getPlayer().getName() + ": " + event.getMessage());
            event.setCancelled(true);
            Messager.bad(Language.SPAMMING_MESSAGE_URL, event.getPlayer());
        }
    }

    /**
     * Listen to players issueing commands
     * 
     * @param event the command preprocess event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        if (handler.isMuted(event.getPlayer()) && !Perms.BYPASS_MUTE.has(event.getPlayer())) {
            event.setCancelled(true);
            Messager.bad(Language.MUTED, event.getPlayer());
            return;
        }
        if (!config.getList(ConfigEntry.INCLUDE_COMMANDS).contains(event.getMessage().split("\\s")[0])) {
            return;
        }
        if (handler.handleChat(event.getPlayer(), event.getMessage())
                && config.getBoolean(ConfigEntry.PREVENT_MESSAGES) && !Perms.BYPASS_REPEAT.has(event.getPlayer())) {
            event.setCancelled(true);
            Messager.bad(Language.SPAMMING_MESSAGE, event.getPlayer());
        }
    }

    /**
     * Listen to players logging in
     * 
     * @param event the login event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (event.getPlayer().isBanned()) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Language.BAN_MESSAGE.toString());
        }
    }
}
