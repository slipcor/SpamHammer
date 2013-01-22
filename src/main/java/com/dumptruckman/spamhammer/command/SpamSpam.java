package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class SpamSpam extends SHCommand {

    public SpamSpam(final SpamHammerPlugin plugin) {
        super(plugin);
        this.setArgRange(1, 3);
        this.setPermission(Perms.CMD_ALL);
    }

    @Override
    public boolean runCommand(final CommandSender sender, final String[] strings) {
        
        if (strings.length < 1) {
            return false;
        }
        String[] args = new String[strings.length -1];
        int pos = 0;
        String subCommand = null;
        for (String string : strings) {
            if (subCommand == null) {
                subCommand = string;
                continue;
            }
            args[pos++] = string;
        }
        subCommand = "spam" + subCommand;
        final PluginCommand command = plugin.getCommand(subCommand);

        if (command == null || "spam".equalsIgnoreCase(subCommand)) {
            return false;
        }

        command.execute(sender, subCommand, args);
        return true;
    }
}
