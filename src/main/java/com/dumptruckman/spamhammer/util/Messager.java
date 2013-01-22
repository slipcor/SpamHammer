/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dumptruckman.spamhammer.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Chris
 */
public final class Messager {
    
    private Messager() {
    }
    
    static enum MessagePrefix {
        BLANK(""),
        ERROR("[Error] "),
        SUCCESS("[Success] "),
        INFO("[Info] "),
        HELP("[Help] "),
        OFF("OFF");
        
        private String prefix;
        
        MessagePrefix(final String prefix) {
            this.prefix = prefix;
        }
        
        @Override
        public String toString() {
            return prefix;
        }
    }
    
    public static void good(final Language lang, final CommandSender sender, final String arg) {
        send(sender, ChatColor.GREEN.toString() + MessagePrefix.SUCCESS + lang.toString(), arg);
    }
    
    public static void good(final Language lang, final CommandSender sender) {
        send(sender, ChatColor.GREEN.toString() + MessagePrefix.SUCCESS + lang.toString(), "");
    }
    
    public static void normal(final Language lang, final CommandSender sender) {
        send(sender, lang.toString(), "");
    }
    
    public static void normal(final Language lang, final CommandSender sender, final String arg) {
        send(sender, lang.toString(), arg);
    }
    
    public static void bad(final Language lang, final CommandSender sender) {
        send(sender, ChatColor.RED.toString() + MessagePrefix.ERROR + lang.toString(), "");
    }
    
    public static void bad(final Language lang, final CommandSender sender, final String arg) {
        send(sender, ChatColor.RED.toString() + MessagePrefix.ERROR + lang.toString(), arg);
    }
    
    private static void send(final CommandSender sender, final String message, final String arg) {
        sender.sendMessage(message.replace("%1", arg));
    }
}
