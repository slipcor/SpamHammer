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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * SpamHandler implementation class
 * 
 * Contains the actual functionality
 * 
 * @author dumptruckman,slipcor
 */
public class DefaultSpamHandler implements SpamHandler {
    protected Config config;

    final private ConcurrentHashMap<UUID, ArrayDeque<Long>> playerChatTimes = new ConcurrentHashMap<>();
    final private ConcurrentHashMap<UUID, ArrayDeque<String>> playerChatHistory = new ConcurrentHashMap<>();
    final private ConcurrentHashMap<UUID, Long> playerActionTime = new ConcurrentHashMap<>();
 
    final private List<UUID> mutedPlayers = new ArrayList<>();
    final private List<UUID> beenMutedPlayers = new ArrayList<>();
    final private List<UUID> beenKickedPlayers = new ArrayList<>();
    
    final Pattern patIP = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
    final Pattern patURL = Pattern.compile("(http://)|(https://)?(www)?\\S{2,}((\\.com)|(\\.net)|(\\.org)|(\\.co\\.uk)|(\\.tk)|(\\.info)|(\\.es)|(\\.de)|(\\.arpa)|(\\.edu)|(\\.firm)|(\\.int)|(\\.mil)|(\\.mobi)|(\\.nato)|(\\.to)|(\\.fr)|(\\.ms)|(\\.vu)|(\\.eu)|(\\.nl)|(\\.us)|(\\.dk))");
    
    final private SpamHammer plugin;

    /**
     * SpamHandler constructor
     * 
     * Hands over the plugin configuration and starts the check timer
     * 
     * @param plugin the plug
     */
    public DefaultSpamHandler(final SpamHammer plugin) {
        this.plugin = plugin;
        this.config = plugin.config();
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                checkTimes();
            }
        }, 0, 20L);
    }

    /**
     * Clean a player's history and ban him
     * 
     * @param player the player to ban
     */
    @Override
    public void banPlayer(final OfflinePlayer player) {
        if (playerChatHistory.containsKey(player.getUniqueId())) {
            playerChatHistory.get(player.getUniqueId()).clear();
        }
        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null && !Perms.BYPASS_BAN.has(onlinePlayer) && !Perms.BYPASS_KICK.has(onlinePlayer)) {
            class RunLater implements Runnable {
                @Override
                public void run() {
                    onlinePlayer.kickPlayer(Language.BAN_MESSAGE.toString());
                    Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(player.getName(), null, null, null);
                    plugin.getLogger().log(Level.INFO, "Player banned: {0}", player.getName());
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, new RunLater(), 1L);
        }
    }

    /**
     * Has a player been kicked before?
     * 
     * @param name the player to check
     * 
     * @return true if the player has been kicked
     */
    @Override
    public boolean beenKicked(final OfflinePlayer name) {
        return beenKickedPlayers.contains(name.getUniqueId());
    }

    /**
     * Has a player been muted before?
     * 
     * @param name the player to check
     * 
     * @return true if the player has been muted
     */
    @Override
    public boolean beenMuted(final OfflinePlayer name) {
        return beenMutedPlayers.contains(name.getUniqueId());
    }
    
    private boolean checkRegEx(final Pattern regEx, final String message) {
        final Matcher matcher = regEx.matcher(message);
        return matcher.find();
    }

    /**
     * Check player timings.
     * 
     * Iterate over all players to check if cooldowns should deplete
     */
    private void checkTimes() {
        final long time = System.nanoTime() / 1000000;
        for (UUID uuid : playerActionTime.keySet()) {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            final long action = playerActionTime.get(uuid);
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
                    final Player onlinePlayer = Bukkit.getPlayer(uuid);
                    if (onlinePlayer != null) {
                        Messager.good(Language.COOL_OFF_MESSAGE, onlinePlayer);
                    }
                    removeMuteHistory(player);
                }
            }
        }
    }

    /**
     * Handle a player chat message
     * 
     * @param player the chatting player
     * @param message the message content
     * @return if the player is spamming
     */
    @Override
    public boolean handleChat(final OfflinePlayer player, final String message) {
        boolean isSpamming = false;

        // Detect rate limited messages
        ArrayDeque<Long> times = playerChatTimes.get(player.getUniqueId());
        if (times == null) {
            times = new ArrayDeque<>();
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
        playerChatTimes.put(player.getUniqueId(), times);

        // Detect duplicate messages
        if (config.getBoolean(ConfigEntry.BLOCK_REPEATS) && !isSpamming) {
            ArrayDeque<String> playerChat = playerChatHistory.get(player.getUniqueId());
            if (playerChat == null) {
                playerChat = new ArrayDeque<>();
            }
            playerChat.add(message);
            if (playerChat.size() > (config.getInt(ConfigEntry.REPEAT_LIMIT) + 1)) {
                playerChat.remove();
            }
            playerChatHistory.put(player.getUniqueId(), playerChat);
            isSpamming = hasDuplicateMessages(player);
        }
        
        if (!isSpamming) {
        	// nothing bad yet. But maybe there is a caps issue?
        	final int maxCaps = config.getInt(ConfigEntry.CAPS_MAXAMOUNT);
        	final double maxRatio = config.getDouble(ConfigEntry.CAPS_MAXRATIO);
        	final int minLength = config.getInt(ConfigEntry.CAPS_MINLENGTH);
        	
        	if (minLength < message.length() && (maxCaps > 0 || maxRatio > 0)) {
        		int sum = 0;
        		int uppercase = 0;
        		final char[] cArray = message.toCharArray();
        		for (char c : cArray) {
        			if (Character.isLowerCase(c)) {
        				sum++;
        			} else if (c != ' ') {
        				sum++;
        				uppercase++;
        			}
        		}
        		
        		// sum now is all actual letters and signs
        		// uppercase is the actual uppercase count, including signs
        		isSpamming = (maxCaps < uppercase) || (maxRatio < uppercase/sum);
        	}
        }
        
        if (isSpamming) {
            playerIsSpamming(player);
        }
        return isSpamming;
    }

    /**
     * Handle a player chat message
     * 
     * @param player the chatting player
     * @param message the message content
     * @return if the player is spamming
     */
    @Override
    public boolean handleChatIP(final OfflinePlayer player, final String message) {
        if (checkRegEx(patIP, message)) {
            playerIsSpamming(player);
            return true;
        }
        return false;
    }

    /**
     * Handle a player chat message
     * 
     * @param player the chatting player
     * @param message the message content
     * @return if the player is spamming
     */
    @Override
    public boolean handleChatURL(final OfflinePlayer player, final String message) {
        if (checkRegEx(patURL, message)) {
            playerIsSpamming(player);
            return true;
        }
        return false;
    }

    /**
     * Did a player say the same thing too often?
     * 
     * @param name the player to check
     * 
     * @return if the player has too many duplicates
     */
    @Override
    public boolean hasDuplicateMessages(final OfflinePlayer name) {
        if (Perms.BYPASS_REPEAT.has(Bukkit.getPlayer(name.getUniqueId()))) {
            return false; // if he has the permission, he never has any duplicates
        }
        
        boolean isSpamming = false;
        int samecount = 1;
        String lastMessage = null;
        for (Object m : playerChatHistory.get(name.getUniqueId()).toArray()) {
            final String message = m.toString();
            if (lastMessage == null) {
                lastMessage = message;
                continue;
            }
            if (message.equals(lastMessage)) {
                samecount++;
            } else {
                playerChatHistory.get(name.getUniqueId()).clear();
                playerChatHistory.get(name.getUniqueId()).add(message);
                break;
            }
            isSpamming = (samecount > config.getInt(ConfigEntry.REPEAT_LIMIT));
        }
        return isSpamming;
    }

    /**
     * Is a player muted?
     * 
     * @param name the player to check
     * 
     * @return if the player is muted
     */
    @Override
    public boolean isMuted(final OfflinePlayer name) {
        return mutedPlayers.contains(name.getUniqueId());
    }

    /**
     * Clean a player's history and ban him
     * 
     * @param player the player to ban
     */
    @Override
    public void kickPlayer(final OfflinePlayer player) {
        if (playerChatHistory.get(player.getUniqueId()) != null) {
            playerChatHistory.get(player.getUniqueId()).clear();
        }
        beenKickedPlayers.add(player.getUniqueId());
        playerActionTime.put(player.getUniqueId(), System.nanoTime() / 1000000);
        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null && !Perms.BYPASS_KICK.has(onlinePlayer)) {
            class RunLater implements Runnable {
                @Override
                public void run() {
                    onlinePlayer.kickPlayer(Language.KICK_MESSAGE.toString());
                    plugin.getLogger().log(Level.INFO, "Player kicked: {0}", player.getName());
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, new RunLater(), 1L);
        }
    }

    /**
     * Clear a player's history and mute him
     * 
     * @param player the player to mute
     */
    @Override
    public void mutePlayer(final OfflinePlayer player) {
        mutedPlayers.add(player.getUniqueId());
        beenMutedPlayers.add(player.getUniqueId());
        playerActionTime.put(player.getUniqueId(), System.nanoTime() / 1000000);
        playerChatTimes.get(player.getUniqueId()).clear();
        if (playerChatHistory.containsKey(player.getUniqueId())) {
            playerChatHistory.get(player.getUniqueId()).clear();
        }

        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            Messager.bad(Language.MUTE, onlinePlayer, String.valueOf(config.getInt(ConfigEntry.MUTE_LENGTH)));
            plugin.getLogger().log(Level.INFO, "Player muted: {0}", player.getName());
        }
    }

    /**
     * A player is spamming. Punish him!
     * 
     * @param name the spamming player
     */
    private void playerIsSpamming(final OfflinePlayer name) {
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
    
    /**
     * Clear a player's kick history
     * 
     * @param player the player to clear
     */
    @Override
    public void removeKickHistory(final OfflinePlayer player) {
        beenKickedPlayers.remove(player.getUniqueId());
    }
    
    /**
     * Clear a player's mute history
     * 
     * @param player the player to clear
     */
    @Override
    public void removeMuteHistory(final OfflinePlayer player) {
        beenMutedPlayers.remove(player.getUniqueId());
    }

    /**
     * Unban a player
     * 
     * @param player the player to unban
     */
    @Override
    public void unBanPlayer(final OfflinePlayer player) {
        Bukkit.getServer().getBanList(BanList.Type.NAME).pardon(player.getName());
    }

    /**
     * Unmute a player
     * 
     * @param player the player to unmute
     */
    @Override
    public void unMutePlayer(final OfflinePlayer player) {
        mutedPlayers.remove(player.getUniqueId());
        final Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            Messager.normal(Language.UNMUTE, onlinePlayer);
        }
    }
}
