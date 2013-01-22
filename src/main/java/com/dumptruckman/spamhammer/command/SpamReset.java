package com.dumptruckman.spamhammer.command;

import com.dumptruckman.spamhammer.SpamHammerPlugin;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class SpamReset extends SHCommand {

    public SpamReset(final SpamHammerPlugin plugin) {
        super(plugin);
        this.setArgRange(1, 1);
        this.setPermission(Perms.CMD_RESET);
    }

    @Override
    public boolean runCommand(final CommandSender sender, final String[] args) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        plugin.getSpamHandler().removeKickHistory(player);
        plugin.getSpamHandler().removeMuteHistory(player);
        Messager.good(Language.RESET_COMMAND_MESSAGE_SUCCESS, sender, player.getName());
        return true;
    }
}
