/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc;

import io.github.xxyy.common.util.ChatHelper;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.chat.cmdspy.CommandSpyFilters;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class ConfigHelper {
    private static String tabListAllowedColors;
    private static boolean prohibitCmdsInBoats;
    private static boolean enableTablist;
    private static boolean mainLoggerEnabled;
    private static ItemStack itemOnJoin;
    private static boolean enableItemOnJoin;
    private static List<Integer> anvilAllowedItems;
    private static boolean clanEnabled;
    private static int clanMaxUsers;
    private static int clanMaxUsersExtended;
    private static boolean chatUseClan;
    private static ArrayList<PotionEffect> snowballEffects;
    private static boolean magicSnowballEnabled;
    private static long snowballTimeoutTicks;
    private static boolean worldSpecificChat;
    private static boolean antiLogoutEnabled;
    private static int secsInFight;
    private static int hologramTimeout;
    private static List<String> fightAllowedCmds;
    private static List<String> vehicleAllowedCmds;
    private static List<Integer> brewStackCheckpoints;

    public static List<Integer> getAnvilAllowedItems() {
        return ConfigHelper.anvilAllowedItems;
    }

    public static boolean getChatUseClan() {
        return ConfigHelper.chatUseClan;
    }

    public static int getClanMaxUsers() {
        return ConfigHelper.clanMaxUsers;
    }

    public static int getClanMaxUsersExtended() {
        return ConfigHelper.clanMaxUsersExtended;
    }

    /**
     * @return the fightAllowedCmds
     */
    public static List<String> getFightAllowedCmds() {
        return ConfigHelper.fightAllowedCmds;
    }

    public static ItemStack getItemOnJoin() {
        return ConfigHelper.itemOnJoin;
    }

    /**
     * @return the secsInFight
     */
    public static int getSecsInFight() {
        return ConfigHelper.secsInFight;
    }

    /**
     * @return the hologramTimeout
     */
    public static int getHologramTimeout() {
        return ConfigHelper.hologramTimeout;
    }

    public static ArrayList<PotionEffect> getSnowballEffects() {
        return ConfigHelper.snowballEffects;
    }

    public static long getSnowballTimeoutTicks() {
        return ConfigHelper.snowballTimeoutTicks;
    }

    public static String getTabListAllowedColors() {
        return ConfigHelper.tabListAllowedColors;
    }

    public static List<String> getVehicleAllowedCmds() {
        return ConfigHelper.vehicleAllowedCmds;
    }

    public static void initMainConfig() {
        FileConfiguration cfg = MTC.instance().getConfig();
        cfg.options().copyDefaults(true);
        cfg.options().header("MTC Config! MTC by xxyy98. Use YAML!");

        ConfigHelper.addEnableDefaults(cfg);
        ConfigHelper.addSqlDefaults(cfg);
        ConfigHelper.addChatDefaults(cfg);
        ConfigHelper.addClanDefaults(cfg);
        ConfigHelper.addFixDefaults(cfg);
        ConfigHelper.addMiscDefaults(cfg);

        ConfigHelper.initClassProperties(cfg);
    }

    public static boolean isAntiLogoutEnabled() {
        return ConfigHelper.antiLogoutEnabled;
    }

    public static boolean isClanEnabled() {
        return ConfigHelper.clanEnabled;
    }
    public static boolean isEnableItemOnJoin() {
        return ConfigHelper.enableItemOnJoin;
    }

    public static boolean isEnableTablist() {
        return ConfigHelper.enableTablist;
    }

    public static boolean isMagicSnowballEnabled() {
        return ConfigHelper.magicSnowballEnabled;
    }

    public static boolean isMainLoggerEnabled() {
        return ConfigHelper.mainLoggerEnabled;
    }

    public static boolean isProhibitCmdsInBoats() {
        return ConfigHelper.prohibitCmdsInBoats;
    }

    public static boolean isWorldSpecificChat() {
        return ConfigHelper.worldSpecificChat;
    }

    public static List<Integer> getBrewStackCheckpoints() {
        return brewStackCheckpoints;
    }

    public static void setBrewStackCheckpoints(List<Integer> brewStackCheckpoints) {
        brewStackCheckpoints.sort(Comparator.<Integer>reverseOrder()); //Higher numbers first
        ConfigHelper.brewStackCheckpoints = brewStackCheckpoints;
    }

    protected static void onConfigReload(MTCPlugin plugin) {
        ConfigHelper.initClassProperties(plugin.getConfig());
    }

    private static void addChatDefaults(FileConfiguration cfg) {
        cfg.addDefault("chat.adDetectionLevel", 1);
        cfg.addDefault("chat.adDetectionLevelALLOWEDVALUES", "For Ad Detection: 0=number ips, 1 = tlds 2 = tlds w/ space-trimming 3 = regex (will filter ANY occurences of '.'");
        cfg.addDefault("chat.adDetectionReplacepointLikeChars", false);
        cfg.addDefault("chat.maxCapsInPercent", 50);
        cfg.addDefault("chat.farbe.default", "§f");
        cfg.addDefault("chat.farbe.allowed", "012356789AaBbCcDdEeFfRr");
        cfg.addDefault("chat.useclan", true);
    }

    private static void addClanDefaults(FileConfiguration cfg) {
        cfg.addDefault("clan.maxusers", 15);
        cfg.addDefault("clan.maxusersextended", 25);
    }

    private static void addEnableDefaults(FileConfiguration cfg) {
        cfg.addDefault("enable.mtc", true);
        cfg.addDefault("enable.chat", true);
        cfg.addDefault("enable.dmgevent", true);
        cfg.addDefault("enable.command.lore", true);
        cfg.addDefault("enable.command.gtime", true);
        cfg.addDefault("enable.command.playerhead.command", true);
        cfg.addDefault("enable.command.playerhead.getall-op-notify", true);
        cfg.addDefault("enable.command.breload", true);
        cfg.addDefault("enable.command.mtc", true);
        cfg.addDefault("enable.command.invsee", true);
        cfg.addDefault("enable.command.team", true);
        cfg.addDefault("enable.command.rename", true);
        cfg.addDefault("enable.misc.lighting.cow", true);
        cfg.addDefault("enable.help", true);
        cfg.addDefault("enable.msg.enablePlug", true);
        cfg.addDefault("enable.msg.disablePlug", true);
        cfg.addDefault("enable.motd", false);
        cfg.addDefault("enable.cron.5m", true);
        cfg.addDefault("enable.bans", true);
        cfg.addDefault("enable.warns", true);
        cfg.addDefault("enable.anvilNbrewingstandStackFix", true);
        cfg.addDefault("enable.infPotionFix", true);
        cfg.addDefault("enable.netherrooffix", true);
        cfg.addDefault("enable.tablist", false);
        cfg.addDefault("enable.betterdmgpotions", true);
        cfg.addDefault("enable.speedonjoin", false);
        cfg.addDefault("enable.cmdspy", true);
        cfg.addDefault("enable.log.main", true);
        cfg.addDefault("enable.log.cmds", true);
        cfg.addDefault("enable.log.chat", true);
        cfg.addDefault("enable.fixes.minecartPortal", true);
        cfg.addDefault("enable.enderpearlListener", true);
        cfg.addDefault("enable.fixes.freecam", true);
        cfg.addDefault("enable.fixes.minecartPortal", true);
        cfg.addDefault("enable.fixes.boatCmds", true);
        cfg.addDefault("enable.clan", true);
        cfg.addDefault("enable.snowball", true);
        cfg.addDefault("enable.signcolor", true);
        cfg.addDefault("enable.playerhide", false);
        cfg.addDefault("enable.peace", false);
        cfg.addDefault("enable.worldspecificchat", false);
        cfg.addDefault("enable.antilogout", false);
        cfg.addDefault("enable.infdisp", true);
    }

    private static void addFixDefaults(FileConfiguration cfg) {
        cfg.addDefault("fixes.anvil-allowed-item-ids", Arrays.asList(0, 256, 257, 261, 267, 264,
                265, 266, 268, 269, 270, 272, 273, 274, 275, 276, 277, 278, 283, 285, 284, 298, 299, 300, 301,
                302, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317));
        cfg.addDefault("fixes.brewstack.checkpoints", Arrays.asList(4, 8, 12, 16));
    }

    private static void addMiscDefaults(FileConfiguration cfg) {
        cfg.addDefault("badCmds", Arrays.asList("op", "gamemode", "full", "banip"));
        cfg.addDefault("tablist.allowedColors", "0123456789abcdef");
        cfg.addDefault("motd", "&6&lMinoTopia.me");
        cfg.addDefault("speedonjoin.potency", 5);
        cfg.addDefault("servername", "Unknown-" + (new File("").getAbsolutePath()));
        cfg.addDefault("snowball.effects", new Integer[]{4, 15, 22});
        cfg.addDefault("snowball.timeoutTicks", 12000);
        cfg.addDefault("antilogout.secsInFight", 20);
        cfg.addDefault("antilogout.hologramTimeout", 60);
        cfg.addDefault("antilogout.allowedCmds", new String[]{"msg", "m", "fix", "heal", "eat"});
        cfg.addDefault("vehicles.allowedCmds", new String[]{"msg", "m", "fix", "heal", "eat", "reply", "team"});
    }

    private static void addSqlDefaults(FileConfiguration cfg) {
        cfg.addDefault("sql.password", "");
        cfg.addDefault("sql.user", "");
        cfg.addDefault("sql.db", "ni176987_1_DB");
        cfg.addDefault("sql.host", "jdbc:mysql://localhost:3306/");
    }

    private static void initClassProperties(FileConfiguration cfg) {
        ChatHelper.percentForCaps = cfg.getInt("chat.maxCapsInPercent", 50);
        ConfigHelper.tabListAllowedColors = cfg.getString("tablist.allowedColors", "7012345689abcdef");
        ConfigHelper.prohibitCmdsInBoats = cfg.getBoolean("enable.fixes.boatCmds", true);
        ConfigHelper.enableTablist = cfg.getBoolean("enable.tablist", true);
        ConfigHelper.mainLoggerEnabled = cfg.getBoolean("enable.log.main", true);
        ConfigHelper.itemOnJoin = cfg.getItemStack("itemonjoin");
        ConfigHelper.enableItemOnJoin = cfg.getBoolean("enable.itemonjoin");
        ConfigHelper.anvilAllowedItems = cfg.getIntegerList("fixes.anvil-allowed-item-ids");
        ConfigHelper.clanEnabled = cfg.getBoolean("enable.clan", true);
        ConfigHelper.clanMaxUsers = cfg.getInt("clan.maxusers", 15);
        ConfigHelper.clanMaxUsersExtended = cfg.getInt("clan.maxusersextended", 25);
        ConfigHelper.chatUseClan = cfg.getBoolean("chat.useclan", true);
        ConfigHelper.initPotionProps(cfg);
        ConfigHelper.snowballTimeoutTicks = cfg.getLong("snowball.timeoutTicks", 12000L);
        ConfigHelper.worldSpecificChat = cfg.getBoolean("enable.worldspecificchat", false);
        ConfigHelper.antiLogoutEnabled = cfg.getBoolean("enable.antilogout", false);
        ConfigHelper.secsInFight = cfg.getInt("antilogout.secsInFight", 20);
        ConfigHelper.hologramTimeout = cfg.getInt("antilogout.hologramTimeout", 60);
        ConfigHelper.fightAllowedCmds = cfg.getStringList("antilogout.allowedCmds");
        ChatHelper.allowedChatColors = cfg.getString("chat.farbe.allowed", "012356789AaBbDdEeFfRr");
        ConfigHelper.vehicleAllowedCmds = cfg.getStringList("vehicles.allowedCmds");
        setBrewStackCheckpoints(cfg.getIntegerList("fixes.brewstack.checkpoints"));
        cfg.getStringList("badCmds").stream().forEach(CommandSpyFilters::addBadCommand);
    }

    private static void initPotionProps(FileConfiguration cfg) {//4, 9, 15
        ConfigHelper.magicSnowballEnabled = cfg.getBoolean("enable.snowball", true);
        ConfigHelper.snowballEffects = new ArrayList<>();
        List<String> potTypes = cfg.getStringList("snowball.effects");
        for (String typeName : potTypes) {
            switch (typeName) {
                case "SLOW_DIGGING":
                    ConfigHelper.snowballEffects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, 600, 1));//30s
                    break;
                case "BLINDNESS":
                    ConfigHelper.snowballEffects.add(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));//5s
                    break;
                case "ABSORPTION":
                    ConfigHelper.snowballEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 40, 2));//2s
                    break;
                default:
                    PotionEffectType type = PotionEffectType.getByName(typeName);
                    if (type == null) {
                        continue;
                    }
                    ConfigHelper.snowballEffects.add(new PotionEffect(type, 100, 0));//5s
            }
        }
    }
}
