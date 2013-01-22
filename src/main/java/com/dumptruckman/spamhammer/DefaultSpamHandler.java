package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.Config;
import com.dumptruckman.spamhammer.api.Config.ConfigEntry;
import com.dumptruckman.spamhammer.api.SpamHammer;
import com.dumptruckman.spamhammer.api.SpamHandler;
import com.dumptruckman.spamhammer.util.Language;
import com.dumptruckman.spamhammer.util.Messager;
import com.dumptruckman.spamhammer.util.Perms;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DefaultSpamHandler implements SpamHandler {
    protected Config config;

    final private ConcurrentHashMap<OfflinePlayer, ArrayDeque<Long>> playerChatTimes = new ConcurrentHashMap<OfflinePlayer, ArrayDeque<Long>>();
    final private ConcurrentHashMap<OfflinePlayer, ArrayDeque<String>> playerChatHistory = new ConcurrentHashMap<OfflinePlayer, ArrayDeque<String>>();
    final private ConcurrentHashMap<OfflinePlayer, Long> actionTime = new ConcurrentHashMap<OfflinePlayer, Long>();

    final private List<OfflinePlayer> mutedPlayers = new ArrayList<OfflinePlayer>();
    final private List<OfflinePlayer> beenMutedPlayers = new ArrayList<OfflinePlayer>();
    final private List<OfflinePlayer> beenKickedPlayers = new ArrayList<OfflinePlayer>();

    public DefaultSpamHandler(final SpamHammer plugin) {
        this.config = plugin.config();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                checkTimes();
            }
        }, 0, 20L);
    }

    public void banPlayer(final OfflinePlayer player) {
        if (playerChatHistory.contains(player)) {
            playerChatHistory.get(player).clear();
        }
        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null && !Perms.BYPASS_BAN.has(onlinePlayer)) {
            player.setBanned(true);
            onlinePlayer.kickPlayer(Language.BAN_MESSAGE.toString());
        }
    }

    public boolean beenKicked(final OfflinePlayer name) {
        if (beenKickedPlayers.contains(name)) {
            return true;
        }
        for (OfflinePlayer player : beenKickedPlayers) {
            if (player.getName().equals(name.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean beenMuted(final OfflinePlayer name) {
        if (beenMutedPlayers.contains(name)) {
            return true;
        }
        for (OfflinePlayer player : beenMutedPlayers) {
            if (player.getName().equals(name.getName())) {
                return true;
            }
        }
        return false;
    }

    public void checkTimes() {
        final long time = System.nanoTime() / 1000000;
        for (OfflinePlayer player : actionTime.keySet()) {
            final long action = actionTime.get(player);
            if (isMuted(player)) {
                final long muteLength = config.getInt(ConfigEntry.MUTE_LENGTH) * 1000;
                if (time > (action + muteLength)) {
                    unMutePlayer(player);
                }
            }
            final long coolOff = config.getInt(ConfigEntry.COOL_OFF) * 1000;
            if ((time > (action + coolOff))
                    && (config.getInt(ConfigEntry.COOL_OFF) != 0)) {
                if (beenKicked(player)) {
                    removeKickHistory(player);
                }
                if (beenMuted(player)) {
                    final Player onlinePlayer = Bukkit.getPlayer(player.getName());
                    if (onlinePlayer != null) {
                        Messager.good(Language.COOL_OFF_MESSAGE, onlinePlayer);
                    }
                    removeMuteHistory(player);
                }
            }
        }
    }

    @Override
    public boolean handleChat(final OfflinePlayer player, final String message) {
        boolean isSpamming = false;

        // Detect rate limited messages
        ArrayDeque<Long> times = playerChatTimes.get(player);
        if (times == null) {
            times = new ArrayDeque<Long>();
        }
        final long curtime = System.nanoTime() / 1000000;
        times.add(curtime);
        if (times.size() > config.getInt(ConfigEntry.MESSAGE_LIMIT)) {
            times.remove();
        }
        if (times.isEmpty()) {
            times.add(curtime);
        } else {
            final long timediff = times.getLast() - times.getFirst();
            if (timediff > (config.getInt(ConfigEntry.TIME_PERIOD) * 1000)) {
                times.clear();
                times.add(curtime);
            }
        }
        
        if (times.size() >= config.getInt(ConfigEntry.MESSAGE_LIMIT)) {
            isSpamming = true;
        }
        playerChatTimes.put(player, times);

        // Detect duplicate messages
        if (config.getBoolean(ConfigEntry.BLOCK_REPEATS) && !isSpamming) {
            ArrayDeque<String> playerChat = playerChatHistory.get(player);
            if (playerChat == null) {
                playerChat = new ArrayDeque<String>();
            }
            playerChat.add(message);
            if (playerChat.size() > (config.getInt(ConfigEntry.REPEAT_LIMIT) + 1)) {
                playerChat.remove();
            }
            playerChatHistory.put(player, playerChat);
            isSpamming = hasDuplicateMessages(player);
        }

        if (isSpamming) {
            playerIsSpamming(player);
        }
        return isSpamming;
    }

    public boolean hasDuplicateMessages(final OfflinePlayer name) {
        boolean isSpamming = false;
        int samecount = 1;
        String lastMessage = null;
        for (Object m : playerChatHistory.get(name).toArray()) {
            final String message = m.toString();
            if (lastMessage == null) {
                lastMessage = message;
                continue;
            }
            if (message.equals(lastMessage)) {
                samecount++;
            } else {
                playerChatHistory.get(name).clear();
                playerChatHistory.get(name).add(message);
                break;
            }
            isSpamming = (samecount > config.getInt(ConfigEntry.REPEAT_LIMIT));
        }
        return isSpamming;
    }

    @Override
    public boolean isMuted(final OfflinePlayer name) {
        return mutedPlayers.contains(name);
    }

    public void kickPlayer(final OfflinePlayer player) {
        if (playerChatHistory.get(player) != null) {
            playerChatHistory.get(player).clear();
        }
        beenKickedPlayers.add(player);
        actionTime.put(player, System.nanoTime() / 1000000);
        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null && !Perms.BYPASS_KICK.has(onlinePlayer)) {
            onlinePlayer.kickPlayer(Language.KICK_MESSAGE.toString());
        }
    }

    public void mutePlayer(final OfflinePlayer player) {
        mutedPlayers.add(player);
        beenMutedPlayers.add(player);
        actionTime.put(player, System.nanoTime() / 1000000);
        playerChatTimes.get(player).clear();
        playerChatHistory.get(player).clear();

        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            Messager.normal(Language.MUTE, onlinePlayer, String.valueOf(config.getInt(ConfigEntry.MUTE_LENGTH)));
        }
    }

    public void playerIsSpamming(final OfflinePlayer name) {
        final boolean useMute = config.getBoolean(ConfigEntry.USE_MUTE);
        final boolean useKick = config.getBoolean(ConfigEntry.USE_KICK);
        final boolean useBan = config.getBoolean(ConfigEntry.USE_BAN);
        if(useMute && (!beenMuted(name) || (!useKick && !useBan))) {
            mutePlayer(name);
            return;
        }
        if (useKick && (!beenKicked(name) || !useBan)) {
            kickPlayer(name);
            return;
        }
        if (useBan) {
            banPlayer(name);
        }
    }
    
    @Override
    public void removeKickHistory(final OfflinePlayer player) {
        beenKickedPlayers.remove(player);
    }
    
    @Override
    public void removeMuteHistory(final OfflinePlayer player) {
        beenMutedPlayers.remove(player);
    }

    @Override
    public void unMutePlayer(final OfflinePlayer player) {
        mutedPlayers.remove(player);
        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            Messager.normal(Language.UNMUTE, onlinePlayer);
        }
    }

    public void unBanPlayer(final OfflinePlayer player) {
        player.setBanned(false);
    }
}
