package com.dumptruckman.spamhammer.util;

import org.bukkit.command.CommandSender;

/**
 * The Perms enum
 * 
 * Shows the permissions and adds a possibility to add parent nodes
 * 
 * @author dumptruckman,slipcor
 */
public enum Perms {
    BYPASS("spamhammer.bypass.*","Allows user to bypass ban punishments"),
    
    BYPASS_REPEAT("spamhammer.bypass.repeat","Allows user to bypass repeat message limit.",BYPASS),
    BYPASS_PUNISH("spamhammer.bypass.punish.*","Allows user to bypass ban punishments",BYPASS),
    
    BYPASS_MUTE("spamhammer.bypass.punish.mute","Allows user to bypass mute punishments",BYPASS_PUNISH),
    BYPASS_KICK("spamhammer.bypass.punish.kick","Allows user to bypass kick punishments",BYPASS_PUNISH),
    BYPASS_BAN("spamhammer.bypass.punish.ban","Allows user to bypass ban punishments",BYPASS_PUNISH),
    BYPASS_IPS("spamhammer.bypass.punish.ips","Allows user to bypass IP check punishments",BYPASS_PUNISH),
    BYPASS_URLS("spamhammer.bypass.punish.urls","Allows user to bypass URL punishments",BYPASS_PUNISH),
    
    CMD_ALL("spamhammer.cmd.*", "Allows use of all commands"),
    
    CMD_UNMUTE("spamhammer.cmd.unmute","Allows use of unmute command",CMD_ALL),
    CMD_RESET("spamhammer.cmd.reset","Allows use of reset command",CMD_ALL),
    CMD_RELOAD("spamhammer.cmd.reload","Allows use of reload command",CMD_ALL);
    
    final String node;
    final String description;
    final Perms parent;
    
    Perms(final String node, final String desc) {
         this(node,desc,null);
    }
    
    Perms(final String node, final String desc, final Perms parent) {
         this.node = node;
         this.description = desc;
         this.parent = parent;
    }
    
    /**
     * Does a CommandSender have the permission?
     * (checks existing parents, recursively)
     * 
     * @param sender the CommandSender to check
     * @return if the player has the permission
     */
    public boolean has(final CommandSender sender) {
        if (parent != null && parent.has(sender)) {
            return true;
        }
        return sender.hasPermission(node);
    }
}
