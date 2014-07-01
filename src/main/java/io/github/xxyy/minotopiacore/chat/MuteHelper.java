package io.github.xxyy.minotopiacore.chat;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public final class MuteHelper {
    private MuteHelper() {

    }

    public static final File MUTE_CFG_FILE = new File("plugins/MinoTopiaCore", "mute.lst.yml");
    public static final YamlConfiguration MUTE_CFG = YamlConfiguration.loadConfiguration(MUTE_CFG_FILE);

    public static boolean isPlayerMuted(String plrName) {
        return MUTE_CFG.contains("muted." + plrName);
    }

    public static void mutePlayer(String plrName, String reason, String senderName) {
        if (!isPlayerMuted(plrName)) {
            //MUTE_CFG.set("muted."+plrName, true);
            MUTE_CFG.set("muted." + plrName + ".reason", reason);
            MUTE_CFG.set("muted." + plrName + ".mutedby", senderName);
            MUTE_CFG.set("muted." + plrName + ".mutetime", (new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime())));
            try {
                MUTE_CFG.save(MUTE_CFG_FILE);
            } catch (IOException e) {
                System.out.println("§cIOException when trying to save mute file :(");
                e.printStackTrace();
            }
        }
    }

    public static void unmutePlayer(String plrName) {
        if (isPlayerMuted(plrName)) {
            MUTE_CFG.set("muted." + plrName, null);
            try {
                MUTE_CFG.save(MUTE_CFG_FILE);
            } catch (IOException e) {
                System.out.println("§cIOException when trying to save mute file :(");
                e.printStackTrace();
            }
        }
    }

    public static Set<String> getMutedPlayerPaths() {
        return MUTE_CFG.getConfigurationSection("muted").getKeys(false);
    }

    public static String getReasonByPath(String path) {
        return MUTE_CFG.getString("muted." + path + ".reason");
    }

    public static String getMuterByPath(String path) {
        return MUTE_CFG.getString("muted." + path + ".mutedby");
    }

    public static String getMuteTimeByPath(String path) {
        return MUTE_CFG.getString("muted." + path + ".mutetime");
    }
}
