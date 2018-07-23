package com.dumptruckman.spamhammer.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The messager class
 * 
 * Contains messaging methods, only used staticly
 * 
 * @author dumptruckman,slipcor
 */
public final class Messager {
    
    private Messager() {
    }
    
    /**
     * The MessagePrefix enum
     * 
     * Used to prepend prefixes before messages
     */
    enum MessagePrefix {
        ERROR("[Error] "),
        SUCCESS("[Success] ");
        
        private String prefix;
        
        MessagePrefix(final String prefix) {
            this.prefix = prefix;
        }
        
        @Override
        public String toString() {
            return prefix;
        }
    }
    
    /**
     * Positive message (GREEN)
     * 
     * @param lang the Language node
     * @param sender the receiving CommandSender
     * @param arg a string argument to replace
     */
    public static void good(final Language lang, final CommandSender sender, final String arg) {
        send(sender, ChatColor.GREEN.toString() + MessagePrefix.SUCCESS + lang.toString(), arg);
    }
    
    /**
     * Positive message (GREEN)
     * 
     * @param lang the Language node
     * @param sender the receiving CommandSender
     */
    public static void good(final Language lang, final CommandSender sender) {
        send(sender, ChatColor.GREEN.toString() + MessagePrefix.SUCCESS + lang.toString(), "");
    }
    
    /**
     * Normal message (default)
     * 
     * @param lang the Language node
     * @param sender the receiving CommandSender
     */
    public static void normal(final Language lang, final CommandSender sender) {
        send(sender, lang.toString(), "");
    }
    
    /**
     * Normal message (default)
     * 
     * @param lang the Language node
     * @param sender the receiving CommandSender
     * @param arg a string argument to replace
     */
    public static void normal(final Language lang, final CommandSender sender, final String arg) {
        send(sender, lang.toString(), arg);
    }
    
    /**
     * Bad message (RED)
     * 
     * @param lang the Language node
     * @param sender the receiving CommandSender
     */
    public static void bad(final Language lang, final CommandSender sender) {
        send(sender, ChatColor.RED.toString() + MessagePrefix.ERROR + lang.toString(), "");
    }
    
    /**
     * Bad message (RED)
     * 
     * @param lang the Language node
     * @param sender the receiving CommandSender
     * @param arg a string argument to replace
     */
    public static void bad(final Language lang, final CommandSender sender, final String arg) {
        send(sender, ChatColor.RED.toString() + MessagePrefix.ERROR + lang.toString(), arg);
    }
    
    /**
     * Send a message to a CommandSender
     * 
     * @param sender the receiving CommandSender
     * @param message the message to send
     * @param arg a string argument to replace
     */
    private static void send(final CommandSender sender, final String message, final String arg) {
        sender.sendMessage(message.replace("%1", arg));
    }
}
