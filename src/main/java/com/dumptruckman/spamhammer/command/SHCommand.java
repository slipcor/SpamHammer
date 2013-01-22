/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dumptruckman.spamhammer.command;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Chris
 */
public class SHCommand implements CommandExecutor {
    protected SpamHammer plugin;
    
    private int min;
    private int max;
    
    private Perms permission;
    
    public SHCommand(final SpamHammer plugin) {
        this.plugin = plugin;
    }
    
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
    
    public boolean runCommand(final CommandSender sender, final String[] args) {
        throw new UnsupportedOperationException("runCommand fail!");
    }
    
    protected void setArgRange(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
    
    protected void setPermission(final Perms perm) {
        this.permission = perm;
    }
}
