package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * The unmute command
 * 
 * @author dumptruckman,slipcor
 */
public class SpamUnmute extends AbstractCommand {

    public SpamUnmute(final SpamHammerPlugin plugin) {
        super(plugin);
        this.setArgRange(1, 1);
        this.setPermission(Perms.CMD_UNMUTE);
    }

    @Override
    public boolean runCommand(final CommandSender sender, final String[] args) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (plugin.getSpamHandler().isMuted(player)) {
            plugin.getSpamHandler().unMutePlayer(player);
            plugin.getSpamHandler().removeMuteHistory(player);
            Messager.good(Language.UNMUTE_COMMAND_MESSAGE_SUCCESS, sender, args[0]);
        } else {
            Messager.good(Language.UNMUTE_COMMAND_MESSAGE_FAILURE, sender, args[0]);
        }
        return true;
    }
}
