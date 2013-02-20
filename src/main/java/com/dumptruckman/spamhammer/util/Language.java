package com.dumptruckman.spamhammer.util;

import com.dumptruckman.spamhammer.api.SpamHammer;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * The language enum
 * 
 * Contains all language nodes and their values
 * 
 * @author dumptruckman,slipcor
 */
public enum Language {
    
    CFG_LOAD_ERROR("config.load.error", "Error while loading config! Plugin will be disabled!"),
    PERM_MISSING("perm.missing", "You don't have permission! (%1)"),
    COMMAND_ARG_MIN("command.arg.min",
            "Not enough arguments! Expected min: %1"),
    COMMAND_ARG_MAX("command.arg.max",
            "Too many arguments! Expected max: %1"),
    MUTE("mute.message.mute",
            "You will be muted for %1 second(s) for spamming.  Keep it up and you'll be kicked."),
    UNMUTE("mute.message.unmute", "You are no longer muted."),
    MUTED("mute.message.muted", "You are muted!"),
    KICK_MESSAGE("kick.message",
            "You have been kicked for spamming.  Keep it up and you'll be banned."),
    BAN_MESSAGE("ban.message", "You have been banned for spamming."),
    COOL_OFF_MESSAGE("cooloff.message",
            "Spamming punishment reset.  Be nice!"),

    SPAMMING_MESSAGE("spamming.message", "You are spamming! Chill out!"),
    SPAMMING_MESSAGE_IP("spamming.messageip", "Sharing IPs is not allowed!"),
    SPAMMING_MESSAGE_URL("spamming.messageurl", "Sharing URLs is not allowed!"),
    
    UNMUTE_COMMAND_MESSAGE_SUCCESS("command.unmute.success",
            "%1 has been unmuted."),
    UNMUTE_COMMAND_MESSAGE_FAILURE("command.unmute.failure",
            "%1 is not muted."),
    RELOAD_COMMAND_MESSAGE_SUCCESS("command.reload.success",
            "Config reloaded!"),
    RESET_COMMAND_MESSAGE_SUCCESS("command.reset.success",
            "%1's punishment level reset."),
    
    LANGUAGE_FILE("languagefile",
            "Successfully loaded: %1"),
    LOG_CHECK_IP("log.checkip",
            "Found IP by %1"),
    LOG_CHECK_URL("log.checkurl",
            "Found URL by %1"),
    
    VALID_GREATER_ZERO("validation.greater_than_zero",
            "Must be a number greater than zero!");
    
    String node;
    String msg;
    
    static YamlConfiguration cfgFile = null;
    
    Language(final String node, final String msg) {
        this.node = node;
        this.msg = msg;
    }
    
    /**
     * Initiate the language class
     * 
     * @param plugin the SpamHammer plugin
     * @param file the configuration file
     * @throws IOException 
     */
    public static void init(final SpamHammer plugin, final String file) throws IOException {
        final File cfg = new File(plugin.getDataFolder(), file);
        if (!cfg.exists()) {
            cfg.createNewFile();
        }
        cfgFile = YamlConfiguration.loadConfiguration(cfg);
        
        cfgFile.options().copyDefaults(true);
        for (Language l : Language.values()) {
            cfgFile.addDefault(l.node, l.msg);
        }
        cfgFile.save(cfg);
    }
    
    /**
     * Override the toString() method in order to get the actual values easier
     * 
     * @return the config value
     */
    @Override
    public String toString() {
        return cfgFile.getString(node);
    }
}
