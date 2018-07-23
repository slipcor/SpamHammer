package com.dumptruckman.spamhammer.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ObjectUtils.Null;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The configuration api clas
 * 
 * Provides config entries and read access
 * 
 * @author dumptruckman,slipcor
 */
public class Config {
    private final JavaPlugin plugin;
    
    /**
     * The Config constructor;
     * 
     * Create the config and append comments
     * 
     * @param plugin the JavaPlugin which contains the config
     */
    public Config(final JavaPlugin plugin) {
        super();
        this.plugin = plugin;
        this.appendComments();
    }
    
    /**
     * The Config entry class
     * 
     * Each entry has an explicit class type, a node, optional comments
     * and a default value
     */
    public enum ConfigEntry {
        MESSAGE(Null.class, "settings.message",null,new String[]{"# === [ Message Spam Settings ] ==="}),
        MESSAGE_RATE(Null.class, "settings.message.rate",null,new String[]{
            "# The message rate settings determine how many messages per time frame are allowed before they are considered spam.",
            "# The default of limit: 3 and period: 1 means more than 3 messages per 1 second will be considered spam"}),
        MESSAGE_LIMIT(Integer.class, "settings.message.rate.limit",3,null),
        
        TIME_PERIOD(Integer.class, "settings.message.rate.period",1,null),

        PREVENT_MESSAGES(Boolean.class, "settings.message.rate.prevent",true,new String[]{"# Prevents messages above the rate limit from displaying"}),

        MESSAGE_REPEAT(Null.class, "settings.message.repeat",null,new String[]{"# The repeat settings allow you to prevent users from repeating the same message in a row"}),
        BLOCK_REPEATS(Boolean.class, "settings.message.repeat.block",true,new String[]{"# If set to true, this will block repeat messages."}),
        REPEAT_LIMIT(Integer.class, "settings.message.repeat.limit",2,new String[]{"# If SpamHammer is set to block repeat messages, this is how many messages before they are considered repeats."}),

        CAPS(Null.class, "settings.message.caps", null, new String[]{"# The caps limiter setting allows for a maximum amount/ratio of caps per message"}),
        
        CAPS_MAXAMOUNT(Integer.class, "settings.message.caps.maxamount", 0, new String[]{"# How many uppercase characters are allowed per message?"}),
        CAPS_MAXRATIO(Double.class, "settings.message.caps.maxratio", 0d, new String[]{"# How many uppercase characters relatively to the length are allowed?"}),
        CAPS_MINLENGTH(Integer.class, "settings.message.caps.minlength", 5, new String[]{"# How many letters are required before checking?"}),
        
        INCLUDE_COMMANDS(String.class, "settings.commandlist.possiblespam",Arrays.asList("/g", "/general", "/yell"),new String[]{
           "# The commands listed here will be included in spam checking."}),
        
        FIRST_RUN(Boolean.class, "settings.first_run",  false, new String[]{"# Will make the plugin perform tasks only done on a first run (if any.)"}),

        PUNISHMENTS(Null.class, "settings.punishments",null,new String[]{
                "# === [ Punishment Settings ] ==="}),
        USE_MUTE(Boolean.class, "settings.punishments.mute.use",true,new String[]{
                "# Setting this to true will mute players as the first level of punishment."}),
        MUTE_LENGTH(Integer.class, "settings.punishments.mute.length",30,new String[]{
                "# If mute punishment is used, this is how long the player will be muted for.",
                "# This time measured in seconds."}),
        MUTE_TYPE(String.class, "settings.punishments.mute.type", "both", new String[]{
        	"# What should be muted? Possible values: chat, command, both"}),
        	
        USE_KICK(Boolean.class, "settings.punishments.kick.use",true,new String[]{
                "# Setting this to true will kick players as the second level of punishment."}),
        USE_BAN(Boolean.class, "settings.punishments.ban.use",true,new String[]{
                "# Setting this to true will ban players as the final level of punishment."}),
        LANGUAGE_LOCALE(String.class, "settings.language.locale", "en", new String[]{"# This is the locale you wish to use."}),
        LANGUAGE_FILE(String.class, "settings.language.file", "en", new String[]{"# This is the language file you wish to use."}),
        COOL_OFF(Integer.class, "settings.cooloff.time",300,new String[]{
                "# This setting determines how long a player will be watched for additional spam before starting",
                "# them at the lowest punishment level.","# This time measured in seconds."}),
        CALLHOME(Boolean.class, "settings.callhome",true,new String[]{
                "# This activates phoning home to www.slipcor.net"}),
        
        CHECKIPS(Boolean.class, "settings.chat.checkips",false,new String[]{
                "# Check for IPs and punish when found"}),
        CHECKURLS(Boolean.class, "settings.chat.checkurls",false,new String[]{
                "# Check for URLs and punish when found"}),
        
        SILENT(Boolean.class, "settings.silent",false,new String[]{
                "# Never log anything"});
    
        Class type;
        String node;
        Object value;
        String[] comments;
        
        ConfigEntry(final Class oClass, final String node, final Object def, final String[] comments) {
            type = oClass;
            this.node = node;
            value = def;
            this.comments = comments == null ? null : comments.clone();
        }
        
        /**
         * Try to get a ConfigEntry based on a node string
         * @param node the node to search for
         * @return the entry or null if not found
         */
        private static ConfigEntry getByNode(final String node) {
            for (ConfigEntry c : values()) {
                if (c.getNode().equals(node)) {
                    return c;
                }
            }
            return null;
        }
        
        /**
         * Get the node string
         * @return the node string
         */
        String getNode() {
            return node;
        }
    }
    
    /**
     * Read a config String entry
     * @param entry the entry to read
     * @return the config string value
     */
    public String get(final ConfigEntry entry) {
        return plugin.getConfig().getString(entry.getNode());
    }
    
    /**
     * Read a config Boolean entry
     * @param entry the entry to read
     * @return the config boolean value
     */
    public boolean getBoolean(final ConfigEntry entry) {
        return plugin.getConfig().getBoolean(entry.getNode());
    }
    
    /**
     * Read a config Integer entry
     * @param entry the entry to read
     * @return the config int value
     */
    public int getInt(final ConfigEntry entry) {
        return plugin.getConfig().getInt(entry.getNode());
    }
    
    /**
     * Read a config Double entry
     * @param entry the entry to read
     * @return the config double value
     */
    public double getDouble(final ConfigEntry entry) {
        return plugin.getConfig().getDouble(entry.getNode());
    }
    
    /**
     * Read a config StringList entry
     * @param entry the entry to read
     * @return the config string list value
     */
    public List<String> getList(final ConfigEntry entry) {
        return plugin.getConfig().getStringList(entry.getNode());
    }
    
    /**
     * on reloading, append the comments
     */
    public void reload() {
        appendComments();
    }
    
    /**
     * Append the comments.
     * 
     * Iterate over the config file and add comments, if we didn't do that
     * alreaady.
     */
    private void appendComments() {
        final File ymlFile = new File(plugin.getDataFolder(), "config.yml");
        
        try {
            
            final FileInputStream fis = new FileInputStream(ymlFile);
            final DataInputStream dis = new DataInputStream(fis);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(dis));
            
            final File tempFile = new File(plugin.getDataFolder(), "config-temp.yml");
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            
            final FileOutputStream fos = new FileOutputStream(tempFile);
            final DataOutputStream dos = new DataOutputStream(fos);
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dos));
            
            String stringLine;
            
            int indent = 0;
            
            String key = null;
            
            while ((stringLine = reader.readLine()) != null) {
                
                if (key == null && writer.toString().length() < 1 && !stringLine.startsWith("#")) {
                    writer.append("# === [ SpamHammer Config ] ===");
                    writer.newLine();
                }
                
                if (stringLine.startsWith("  #")) {
                    writer.flush();
                    writer.close();
                    reader.close();
                    tempFile.delete();
                    return;
                }
                
                final int firstDigit = (indent * 2);
                
                if (stringLine.startsWith("#") || stringLine.length() < firstDigit+1 || stringLine.charAt(firstDigit) == '#') {
                    
                    writer.append(stringLine);
                    writer.newLine();
                    continue;
                }
                
                if (stringLine.contains(":")) {
                    final String newStringLine = stringLine.split(":")[0] + ":";
                    int pos;
                    final StringBuilder builder = new StringBuilder();
                    int newDigit = -1;
                    
                    for (pos = 0; pos<newStringLine.length(); pos++) {
                        if (newStringLine.charAt(pos) != ' '
                                && newStringLine.charAt(pos) != ':') {
                            if (newDigit == -1) {
                                newDigit = pos;
                            }
                            builder.append(newStringLine.charAt(pos));
                        }
                    }
                    
                    if (key == null) {
                        key = builder.toString();
                    }
                    
                    String[] split = key.split("\\.");
                    
                    if (newDigit > firstDigit) {
                        indent++;
                        
                        final String[] newString = new String[split.length+1];
                        System.arraycopy(split, 0, newString, 0, split.length);
                        newString[split.length] = builder.toString();
                        split = newString;
                    } else if (newDigit < firstDigit) {
                        
                        indent = (newDigit/2);
                        
                        final String[] newString = new String[indent+1];
                        
                        System.arraycopy(split, 0, newString, 0, indent);
                        
                        newString[newString.length-1] = builder.toString();
                        split = newString;
                    } else {
                        split[split.length-1] = builder.toString();
                    }
                    
                    final StringBuilder buffer = new StringBuilder();
                    for (String string : split) {
                        buffer.append('.');
                        buffer.append(string);
                    }
                    
                    key = buffer.substring(1);
                    
                    final ConfigEntry entry = ConfigEntry.getByNode(key);
                    
                    if (entry == null) {
                        writer.append(stringLine);
                        writer.newLine();
                        continue;
                    }
                    
                    final StringBuilder value = new StringBuilder();
                    
                    for (int k=0; k<indent; k++) {
                        value.append("  ");
                    }
                    if (entry.comments != null) {
                        for (String s : entry.comments) {
                            writer.append(value + s);
                            writer.newLine();
                        }
                    }
                }
                writer.append(stringLine);
                writer.newLine();
            }
            
            writer.flush();
            writer.close();
            writer.close();
            
            ymlFile.delete();
            tempFile.renameTo(ymlFile);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
