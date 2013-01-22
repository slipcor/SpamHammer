package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.command.CommandSender;

public class SpamReload extends SHCommand {

    public SpamReload(final SpamHammerPlugin plugin) {
        super(plugin);
        this.setArgRange(0, 0);
        this.setPermission(Perms.CMD_RELOAD);
    }

    @Override
    public boolean runCommand(final CommandSender sender, final String[] args) {
        plugin.reloadConfig();
        Messager.good(Language.RELOAD_COMMAND_MESSAGE_SUCCESS, sender);
        return true;
    }
}
