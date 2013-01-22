package com.dumptruckman.spamhammer.util;

import org.bukkit.command.CommandSender;

public enum Perms {
    BYPASS("bypass.*","Allows user to bypass ban punishments"),
    
    BYPASS_REPEAT("bypass.repeat","Allows user to bypass repeat message limit.",BYPASS),
    BYPASS_PUNISH("bypass.punish.*","Allows user to bypass ban punishments",BYPASS),
    
    BYPASS_MUTE("bypass.punish.mute","Allows user to bypass mute punishments",BYPASS_PUNISH),
    BYPASS_KICK("bypass.punish.kick","Allows user to bypass kick punishments",BYPASS_PUNISH),
    BYPASS_BAN("bypass.punish.ban","Allows user to bypass ban punishments",BYPASS_PUNISH),
    
    CMD_ALL("cmd.*", "Allows use of all commands"),
    
    CMD_UNBAN("cmd.unban","Allows use of unban command",CMD_ALL),
    CMD_UNMUTE("cmd.unmute","Allows use of unmute command",CMD_ALL),
    CMD_RESET("cmd.reset","Allows use of reset command",CMD_ALL),
    CMD_RELOAD("cmd.reload","Allows use of reload command",CMD_ALL);
    
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
    
    public boolean has(final CommandSender sender) {
        if (parent != null && parent.has(sender)) {
            return true;
        }
        return sender.hasPermission(node);
    }
}
