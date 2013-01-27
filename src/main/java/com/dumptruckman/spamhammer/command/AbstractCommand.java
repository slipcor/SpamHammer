package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * SpamHammer abstract command superclass
 * 
 * A class that all subcommands have to inherit
 * 
 * @author slipcor
 */
public abstract class AbstractCommand implements CommandExecutor {
    protected SpamHammerPlugin plugin;
    
    private int min;
    private int max;
    
    private Perms permission;
    
    public AbstractCommand(final SpamHammerPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Command handling, check all vars and finally run the command
     * @param sender the CommandSender running the command
     * @param cmnd the Command being called
     * @param cmdLabel the Command string
     * @param strings the command arguments
     * @return false if help should be displayed
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmnd, final String cmdLabel, final String[] strings) {
        if (strings.length < min) {
            Messager.bad(Language.COMMAND_ARG_MIN, sender, String.valueOf(min));
            return false;
        }
        
        if (strings.length > max) {
            Messager.bad(Language.COMMAND_ARG_MAX, sender, String.valueOf(max));
            return false;
        }
        
        if (!sender.hasPermission(this.permission.toString())) {
            Messager.bad(Language.PERM_MISSING, sender, this.permission.toString());
            return true;
        }
        
        return this.runCommand(sender, strings);
    }
    
    /**
     * Run the actual command
     * 
     * @param sender the CommandSender running the command
     * @param args the command arguments
     * @return false if help should be displayed
     */
    public abstract boolean runCommand(final CommandSender sender, final String[] args);
    
    /**
     * Set the valid argument range
     * @param min the minimal needed arguments
     * @param max the maximal allowed arguments
     */
    protected void setArgRange(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
    
    /**
     * Set the needed permission to run the command
     * @param perm the permission
     */
    protected void setPermission(final Perms perm) {
        this.permission = perm;
    }
}
