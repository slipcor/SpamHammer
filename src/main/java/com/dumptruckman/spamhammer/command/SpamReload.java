package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;

/**
 * The reload command
 * 
 * @author dumptruckman,slipcor
 */
public class SpamReload extends AbstractCommand {

    public SpamReload(final SpamHammerPlugin plugin) {
        super(plugin);
        this.setArgRange(0, 0);
        this.setPermission(Perms.CMD_RELOAD);
    }

    @Override
    public boolean runCommand(final CommandSender sender, final String[] args) {
        plugin.reloadConfig();
        Messager.good(Language.RELOAD_COMMAND_MESSAGE_SUCCESS, sender);
        if (plugin.config().getBoolean(Config.ConfigEntry.SILENT)) {
            plugin.getLogger().setLevel(Level.WARNING);
        }
        return true;
    }
}
