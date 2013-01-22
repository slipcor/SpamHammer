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

public class Config {
    private final JavaPlugin plugin;
    
    public Config(final JavaPlugin plugin) {
        super();
        this.plugin = plugin;
        this.appendComments();
    }
    
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

        USE_KICK(Boolean.class, "settings.punishments.kick.use",true,new String[]{
                "# Setting this to true will kick players as the second level of punishment."}),
        USE_BAN(Boolean.class, "settings.punishments.ban.use",true,new String[]{
                "# Setting this to true will ban players as the final level of punishment."}),
        LANGUAGE_LOCALE(String.class, "settings.language.locale", "en", new String[]{"# This is the locale you wish to use."}),
        LANGUAGE_FILE(String.class, "settings.language.file", "en", new String[]{"# This is the language file you wish to use."}),
        COOL_OFF(Integer.class, "settings.cooloff.time",300,new String[]{
                "# This setting determines how long a player will be watched for additional spam before starting",
                "# them at the lowest punishment level.","# This time measured in seconds."});
    
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
        
        private static ConfigEntry getByNode(final String node) {
            for (ConfigEntry c : values()) {
                if (c.getNode().equals(node)) {
                    return c;
                }
            }
            return null;
        }
        
        String getNode() {
            return node;
        }
    }
    
    public String get(final ConfigEntry entry) {
        return plugin.getConfig().getString(entry.getNode());
    }
    
    public boolean getBoolean(final ConfigEntry entry) {
        return plugin.getConfig().getBoolean(entry.getNode());
    }
    
    public int getInt(final ConfigEntry entry) {
        return plugin.getConfig().getInt(entry.getNode());
    }
    
    public List<String> getList(final ConfigEntry entry) {
        return plugin.getConfig().getStringList(entry.getNode());
    }
    
    public void reload() {
        appendComments();
    }
    
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
                    
                    //System.out.print(firstDigit);
                    //System.out.print(pos);
                    
                    if (newDigit > firstDigit) {
                        // new indent, add to key
                        //System.out.print(">");
                        indent++;
                        
                        final String[] newString = new String[split.length+1];
                        System.arraycopy(split, 0, newString, 0, split.length);
                        newString[split.length] = builder.toString();
                        split = newString;
                    } else if (newDigit < firstDigit) {
                        // indent back, strip from key
                        //System.out.print("<");
                        
                        indent = (int) (newDigit/2);
                        
                        //System.out.print("new indent: " + indent);
                        
                        final String[] newString = new String[indent+1];
                        
                        System.arraycopy(split, 0, newString, 0, indent);
                        
                        newString[newString.length-1] = builder.toString();
                        split = newString;
                    } else {
                        //System.out.print("=");
                        // same indent, update key
                        split[split.length-1] = builder.toString();
                    }
                    
                    //System.out.print("Debug --START--");
                    //System.out.print("Indent: " + indent);
                    //System.out.print("Key: " + key);
                    //System.out.print("SB: " + sb.toString());
                    //System.out.print("Pos: " + pos);
                    //System.out.print("StringLine: " + stringLine);
                    //System.out.print("Debug ---END---");
                    
                    final StringBuffer buffer = new StringBuffer();
                    for (String string : split) {
                        buffer.append('.');
                        buffer.append(string);
                    }
                    
                    key = buffer.substring(1);
                    
                    
                    //System.out.print("NewKey: " + key);
                    //System.out.print("Debug ---END---");
                    
                    final ConfigEntry entry = ConfigEntry.getByNode(key);
                    
                    if (entry == null) {
                        //plugin.getLogger().log(Level.SEVERE, "node null: {0}", key);
                        writer.append(stringLine);
                        writer.newLine();
                        continue;
                    }
                    
                    final StringBuffer value = new StringBuffer();
                    
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
