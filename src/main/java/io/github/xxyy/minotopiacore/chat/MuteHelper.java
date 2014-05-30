package io.github.xxyy.minotopiacore.chat;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class MuteHelper {
    public static final File muteCfgFile = new File("plugins/MinoTopiaCore", "mute.lst.yml");
    public static final YamlConfiguration muteCfg = YamlConfiguration.loadConfiguration(muteCfgFile);

    public static boolean isPlayerMuted(String plrName) {
        return muteCfg.contains("muted." + plrName);
    }

    public static void mutePlayer(String plrName, String reason, String senderName) {
        if (!isPlayerMuted(plrName)) {
            //muteCfg.set("muted."+plrName, true);
            muteCfg.set("muted." + plrName + ".reason", reason);
            muteCfg.set("muted." + plrName + ".mutedby", senderName);
            muteCfg.set("muted." + plrName + ".mutetime", (new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime())));
            try {
                muteCfg.save(muteCfgFile);
            } catch (IOException e) {
                System.out.println("§cIOException when trying to save mute file :(");
                e.printStackTrace();
            }
        }
    }

    public static void unmutePlayer(String plrName) {
        if (isPlayerMuted(plrName)) {
            muteCfg.set("muted." + plrName, null);
            try {
                muteCfg.save(muteCfgFile);
            } catch (IOException e) {
                System.out.println("§cIOException when trying to save mute file :(");
                e.printStackTrace();
            }
        }
    }

    public static Set<String> getMutedPlayerPaths() {
        return muteCfg.getConfigurationSection("muted").getKeys(false);
    }

    public static String getReasonByPath(String path) {
        return muteCfg.getString("muted." + path + ".reason");
    }

    public static String getMuterByPath(String path) {
        return muteCfg.getString("muted." + path + ".mutedby");
    }

    public static String getMuteTimeByPath(String path) {
        return muteCfg.getString("muted." + path + ".mutetime");
    }
}
