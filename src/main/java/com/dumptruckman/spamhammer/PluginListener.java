package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.Config.ConfigEntry;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author dumptruckman
 */
public class PluginListener implements Listener {

    private final SpamHandler handler;
    private final Config config;

    public PluginListener(final SpamHammer plugin) {
        this.handler = plugin.getSpamHandler();
        this.config = plugin.config();
    }

    @EventHandler()
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
        }
    }

    @EventHandler
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

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (event.getPlayer().isBanned()) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Language.BAN_MESSAGE.toString());
        }
    }
}
