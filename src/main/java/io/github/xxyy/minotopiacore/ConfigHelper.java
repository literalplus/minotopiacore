package io.github.xxyy.minotopiacore;

import io.github.xxyy.common.util.ChatHelper;
import io.github.xxyy.common.util.CommandHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ConfigHelper {
    private static short adDetectionLevel;
    private static boolean adDetectionReplacePointLikeChars;
    private static int SpeedOnJoinPotency;
    private static List<String> badCmds;
    private static String tabListAllowedColors;
    private static boolean cmdSpyEnabled;
    private static boolean prohibitCmdsInBoats;
    private static boolean enableTablist;
    private static boolean noobProtection;
    private static int noobProtectionDuration;
    private static boolean fullLogEnabled;
    private static boolean fullTagEnabled;
    private static boolean mainLoggerEnabled;
    private static List<String> fullTagAllowedPlayers;
    private static boolean enableMsgOnFirstJoin;
    private static boolean removeUnknownFulls;
    private static ItemStack itemOnJoin;
    private static boolean enableItemOnJoin;
    private static List<Integer> anvilAllowedItems;
    private static boolean clanEnabled;
    @Deprecated private static String langRevision;
    private static boolean statsEnabled;
    private static HashMap<String, String> teamMap;//name -> prefix
    private static LinkedList<String> teamGroupsInOrder = new LinkedList<>();
    private static int clanMaxUsers;
    private static int clanMaxUsersExtended;
    private static boolean chatUseClan;
    private static boolean enableScB;
    private static int scBUpdateInterval;
    private static String scBMode;
    private static boolean scBDisplayPlayerCount;
    private static boolean scBReverseSql;
    private static ArrayList<PotionEffect> snowballEffects;
    private static boolean magicSnowballEnabled;
    private static boolean invseeEnabled;
    private static boolean userStatisticsEnabled;
    private static long snowballTimeoutTicks;
    private static boolean peaceEnabled;
    private static boolean worldSpecificChat;
    private static boolean antiLogoutEnabled;
    private static int secsInFight;
    private static List<String> fightAllowedCmds;
    private static List<String> vehicleAllowedCmds;
    public static short getAdDetectionLevel() {
        return ConfigHelper.adDetectionLevel;
    }
    public static List<Integer> getAnvilAllowedItems() {
        return ConfigHelper.anvilAllowedItems;
    }
    public static List<String> getBadCmds() {
        return ConfigHelper.badCmds;
    }
    public static boolean getChatUseClan(){
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
    public static List<String> getFightAllowedCmds()
    {
        return ConfigHelper.fightAllowedCmds;
    }
    public static List<String> getFullTagAllowedPlayers() {
        return ConfigHelper.fullTagAllowedPlayers;
    }
    public static ItemStack getItemOnJoin() {
        return ConfigHelper.itemOnJoin;
    }
    public static String getLangRevision() {
        return ConfigHelper.langRevision;
    }
    public static int getNoobProtectionDuration() {
        return ConfigHelper.noobProtectionDuration;
    }
    public static boolean getRemoveUnknownFulls(){
        return ConfigHelper.removeUnknownFulls;
    }
    public static String getScBMode() {
        return ConfigHelper.scBMode;
    }
    public static int getScBUpdateInterval() {
        return ConfigHelper.scBUpdateInterval;
    }
    /**
     * @return the secsInFight
     */
    public static int getSecsInFight()
    {
        return ConfigHelper.secsInFight;
    }
    public static ArrayList<PotionEffect> getSnowballEffects() {
        return ConfigHelper.snowballEffects;
    }
    public static long getSnowballTimeoutTicks()
    {
        return ConfigHelper.snowballTimeoutTicks;
    }
    public static int getSpeedOnJoinPotency() {
        return ConfigHelper.SpeedOnJoinPotency;
    }
public static String getTabListAllowedColors() {
    return ConfigHelper.tabListAllowedColors;
}
    public static LinkedList<String> getTeamGroupsInOrder()
    {
        return ConfigHelper.teamGroupsInOrder;
    }
    public static HashMap<String, String> getTeamMap() {
            return ConfigHelper.teamMap;
        }
    public static List<String> getVehicleAllowedCmds()
    {
        return ConfigHelper.vehicleAllowedCmds;
    }
    //    public static ArrayList<ArrayList<String>> getTeamList() {
//        return ConfigHelper.teamList;
//    }
    public static void initMainConfig(){
        FileConfiguration cfg = MTC.instance().getConfig();
        cfg.options().copyDefaults(true);
        cfg.options().header("MTC Config! MTC by xxyy98. Use YAML!");
        
        ConfigHelper.addEnableDefaults(cfg);
        ConfigHelper.addSqlDefaults(cfg);
        ConfigHelper.addChatDefaults(cfg);
        ConfigHelper.addClanDefaults(cfg);
        ConfigHelper.addFixDefaults(cfg);
        ConfigHelper.addMiscDefaults(cfg);
        MTC.instance().saveConfig();
        
        ConfigHelper.initClassProperties(cfg);
    }
    public static boolean isAdDetectionReplacePointLikeChars() {
        return ConfigHelper.adDetectionReplacePointLikeChars;
    }
    /**
     * @return the antiLogoutEnabled
     */
    public static boolean isAntiLogoutEnabled()
    {
        return ConfigHelper.antiLogoutEnabled;
    }
    public static boolean isChatUseClan() {
        return ConfigHelper.chatUseClan;
    }
    public static boolean isClanEnabled() {
        return ConfigHelper.clanEnabled;
    }
    public static boolean isCmdSpyEnabled() {
        return ConfigHelper.cmdSpyEnabled;
    }
    public static boolean isEnableItemOnJoin() {
        return ConfigHelper.enableItemOnJoin;
    }
    public static boolean isEnableMsgOnFirstJoin() {
        return ConfigHelper.enableMsgOnFirstJoin;
    }
    public static boolean isEnableScB() {
        return ConfigHelper.enableScB;
    }
    public static boolean isEnableTablist() {
        return ConfigHelper.enableTablist;
    }
    public static boolean isFullLogEnabled() {
        return ConfigHelper.fullLogEnabled;
    }
    public static boolean isFullTagEnabled() {
        return ConfigHelper.fullTagEnabled;
    }
    public static boolean isInvseeEnabled()
    {
        return ConfigHelper.invseeEnabled;
    }
    public static boolean isMagicSnowballEnabled() {
        return ConfigHelper.magicSnowballEnabled;
    }
    public static boolean isMainLoggerEnabled() {
        return ConfigHelper.mainLoggerEnabled;
    }
    public static boolean isNoobProtection() {
        return ConfigHelper.noobProtection;
    }
    public static boolean isPeaceEnabled()
    {
        return ConfigHelper.peaceEnabled;
    }
    public static boolean isProhibitCmdsInBoats() {
        return ConfigHelper.prohibitCmdsInBoats;
    }
    public static boolean isRemoveUnknownFulls() {
        return ConfigHelper.removeUnknownFulls;
    }
    public static boolean isScBDisplayPlayerCount() {
        return ConfigHelper.scBDisplayPlayerCount;
    }
    public static boolean isScBReverseSql() {
        return ConfigHelper.scBReverseSql;
    }
    public static boolean isStatsEnabled() {
        return ConfigHelper.statsEnabled;
    }
    public static boolean isUserStatisticsEnabled()
    {
        return ConfigHelper.userStatisticsEnabled;
    }
    public static boolean isWorldSpecificChat()
    {
        return ConfigHelper.worldSpecificChat;
    }
    
    public static void onConfigReload(){
        ConfigHelper.initClassProperties(MTC.instance().getConfig());
    }
    public static void setClanEnabled(boolean clanEnabled) {
        ConfigHelper.clanEnabled = clanEnabled;
    }
    public static void setLangRevision(String langRevision) {
        ConfigHelper.langRevision = langRevision;
        MTC.instance().getConfig().set("lang.revision", langRevision);
    }
    private static void addChatDefaults(FileConfiguration cfg){
        cfg.addDefault("chat.adDetectionLevel", 1);
        cfg.addDefault("chat.adDetectionLevelALLOWEDVALUES", "For Ad Detection: 0=number ips, 1 = tlds 2 = tlds w/ space-trimming 3 = regex (will filter ANY occurences of '.'");
        cfg.addDefault("chat.adDetectionReplacepointLikeChars", false);
        cfg.addDefault("chat.maxCapsInPercent", 50);
        cfg.addDefault("chat.farbe.default","§f");
        cfg.addDefault("chat.farbe.allowed","012356789AaBbCcDdEeFfRr");
        cfg.addDefault("chat.useclan", true);
    }
    private static void addClanDefaults(FileConfiguration cfg){
//        cfg.addDefault("", "");
        cfg.addDefault("clan.maxusers",15);
        cfg.addDefault("clan.maxusersextended", 25);
    }
    private static void addEnableDefaults(FileConfiguration cfg){
        cfg.addDefault("enable.mtc", true);
        cfg.addDefault("enable.falldown", true);
        cfg.addDefault("enable.chat", true);
        cfg.addDefault("enable.teambattle", true);
        cfg.addDefault("enable.dmgevent", true);
        cfg.addDefault("enable.command.lore", true);
        cfg.addDefault("enable.command.gtime", true);
        cfg.addDefault("enable.command.playerhead.command", true);
        cfg.addDefault("enable.command.playerhead.getall-op-notify", true);
        cfg.addDefault("enable.command.breload", true);
        cfg.addDefault("enable.command.mtc", true);
        cfg.addDefault("enable.command.war", true);
        cfg.addDefault("enable.command.invsee", true);
        cfg.addDefault("enable.command.team", true);
        cfg.addDefault("enable.command.rename", true);
        cfg.addDefault("enable.bungeeapi", true);
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
        cfg.addDefault("enable.betterdmgpotions",true);
        cfg.addDefault("enable.speedonjoin", false);
        cfg.addDefault("enable.cmdspy", true);
        cfg.addDefault("enable.noobprotection", true);
        cfg.addDefault("enable.fulltag",true);
        cfg.addDefault("enable.log.main",true);
        cfg.addDefault("enable.log.cmds", true);
        cfg.addDefault("enable.log.chat", true);
        cfg.addDefault("enable.log.warnban", true);
        cfg.addDefault("enable.log.fulls", true);
        cfg.addDefault("enable.fixes.minecartPortal", true);
        cfg.addDefault("enable.enderpearlListener", true);
        cfg.addDefault("enable.fixes.freecam", true);
        cfg.addDefault("enable.fixes.minecartPortal", true);
        cfg.addDefault("enable.fixes.boatCmds", true);
        cfg.addDefault("enable.msgOnFirstJoin", true);
        cfg.addDefault("enable.itemonjoin", false);
        cfg.addDefault("enable.clan", true);
        cfg.addDefault("enable.stats", true);
        cfg.addDefault("enable.scoreboard", true);
        cfg.addDefault("enable.snowball", true);
        cfg.addDefault("enable.userstatistics", true);
        cfg.addDefault("enable.signcolor", true);
        cfg.addDefault("enable.playerhide", false);
        cfg.addDefault("enable.peace", false);
        cfg.addDefault("enable.worldspecificchat", false);
        cfg.addDefault("enable.antilogout", false);
        cfg.addDefault("enable.infdisp", true);
    }
    private static void addFixDefaults(FileConfiguration cfg){
        cfg.addDefault("fixes.netherroof.spawn.worldName", "world");
        cfg.addDefault("fixes.netherroof.spawn.x", 0);
        cfg.addDefault("fixes.netherroof.spawn.y", 70);
        cfg.addDefault("fixes.netherroof.spawn.z", 0);
        cfg.addDefault("fixes.netherroof.spawn.pitch", 0);
        cfg.addDefault("fixes.netherroof.spawn.yaw", 0);
        cfg.addDefault("fixes.anvil-allowed-item-ids", Arrays.asList(new Integer[]{0, 256, 257, 261, 267, 264,
            265, 266, 268, 269, 270, 272, 273, 274, 275, 276, 277, 278, 283, 285, 284, 298, 299, 300, 301,
            302, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317}));
    }
    private static void addMiscDefaults(FileConfiguration cfg){
        cfg.addDefault("badCmds", Arrays.asList(new String[]{"op","gamemode","full","banip"}));
        cfg.addDefault("tablist.allowedColors", "0123456789abcdef");
        cfg.addDefault("warnban.serversuffix","§7§o[UnknownServer]");
        cfg.addDefault("motd","&6&lMinoTopia.me");
        cfg.addDefault("speedonjoin.potency", 5);
        cfg.addDefault("servername","Unknown-"+(new File("").getAbsolutePath()));
        cfg.addDefault("noobprotection.durationInMinutes", 20);
        cfg.addDefault("fulltag.allowedPlayers", Arrays.asList(new String[]{"xxyy98","chris301234","kANNEY","dani448","SkillerProfiZ"}));
        cfg.addDefault("fulltag.removeUnkownFulls", false);
        cfg.addDefault("fulltag.checkEveryInMinutes",20);
        cfg.addDefault("itemonjoin", new ItemStack(Material.WATCH));
        //cfg.addDefault("lang.revision", MTC.langRevision);
        cfg.addDefault("team.groups", Arrays.asList(new String[]{"Owner=&4&lOwner:","Developer=&3Developer:",
                "Admin=&cAdmins:","Moderator=&5Moderatoren:","Supporter=&bSupporter:"}));
        cfg.addDefault("team.overridePrefixes", false);
        cfg.addDefault("scoreboard.updateIntervalTicks", 100);
        cfg.addDefault("scoreboard.mode", "ALL");
        cfg.addDefault("scoreboard.modeExplanation", "ALL=cycle through; PVP=only PvP; TM=only TM");
        cfg.addDefault("scoreboard.showPlayerCount", false);
        cfg.addDefault("scoreboard.reversesql", false);
        cfg.addDefault("snowball.effects", new Integer[]{4, 15, 22});
        cfg.addDefault("snowball.timeoutTicks", 12000);
        cfg.addDefault("antilogout.secsInFight", 20);
        cfg.addDefault("antilogout.allowedCmds", new String[]{ "msg","m","fix","heal","eat" });
        cfg.addDefault("vehicles.allowedCmds", new String[]{ "msg","m","fix","heal","eat","reply","team" });
    }
    private static void addSqlDefaults(FileConfiguration cfg){
        cfg.addDefault("sql.password","");
        cfg.addDefault("sql.user","");
        cfg.addDefault("sql.db","ni176987_1_DB");
        cfg.addDefault("sql.host","jdbc:mysql://localhost:3306/");
        cfg.addDefault("sql2.password", "");
        cfg.addDefault("sql2.user", "");
        cfg.addDefault("sql2.db","troublemine");
        cfg.addDefault("sql2.table", "tomt_users");
        cfg.addDefault("sql2.host", "jdbc:mysql://localhost:3306/");
    }
    private static void initClassProperties(FileConfiguration cfg){
        ConfigHelper.adDetectionLevel = (short)cfg.getInt("chat.adDetectionLevel");
        ConfigHelper.adDetectionReplacePointLikeChars = cfg.getBoolean("chat.adDetectionReplacepointLikeChars");
        ChatHelper.percentForCaps = cfg.getInt("chat.maxCapsInPercent",50);
        ConfigHelper.SpeedOnJoinPotency = (cfg.getBoolean("enable.speedonjoin",false) ? (cfg.getInt("speedonjoin.potency", 5) - 1) : -1);
        ConfigHelper.badCmds = cfg.getStringList("badCmds");
        ConfigHelper.tabListAllowedColors = cfg.getString("tablist.allowedColors","7012345689abcdef");
        ConfigHelper.cmdSpyEnabled = cfg.getBoolean("enable.cmdspy",true);
        ConfigHelper.prohibitCmdsInBoats = cfg.getBoolean("enable.fixes.boatCmds",true);
        ConfigHelper.enableTablist = cfg.getBoolean("enable.tablist",true);
        ConfigHelper.noobProtection = cfg.getBoolean("enable.noobprotection",true);
        ConfigHelper.noobProtectionDuration = cfg.getInt("noobprotection.durationInMinutes",20);
        ConfigHelper.fullLogEnabled = cfg.getBoolean("enable.log.fulls",true);
        ConfigHelper.fullTagEnabled = cfg.getBoolean("enable.fulltag",true);
        ConfigHelper.mainLoggerEnabled = cfg.getBoolean("enable.log.main",true);
        ConfigHelper.fullTagAllowedPlayers = cfg.getStringList("fulltag.allowedPlayers");
        ConfigHelper.enableMsgOnFirstJoin = cfg.getBoolean("enable.msgOnFirstJoin",true);
        ConfigHelper.removeUnknownFulls = cfg.getBoolean("fulltag.removeUnkownFulls",false);
        ConfigHelper.itemOnJoin = cfg.getItemStack("itemonjoin");
        ConfigHelper.enableItemOnJoin = cfg.getBoolean("enable.itemonjoin");
        ConfigHelper.anvilAllowedItems = cfg.getIntegerList("fixes.anvil-allowed-item-ids");
        ConfigHelper.clanEnabled = cfg.getBoolean("enable.clan",true);
//        ConfigHelper.langRevision = cfg.getString("lang.revision",MTC.langRevision);
        ConfigHelper.statsEnabled = cfg.getBoolean("enable.stats",true);
       // ConfigHelper.teamList = (ArrayList<ArrayList<String>>) cfg.getList("team", new ArrayList<ArrayList<String>>());
        ConfigHelper.initTeamList(cfg);
        ConfigHelper.clanMaxUsers = cfg.getInt("clan.maxusers",15);
        ConfigHelper.clanMaxUsersExtended = cfg.getInt("clan.maxusersextended",25);
        ConfigHelper.chatUseClan = CommandHelper.writeAndPass(cfg.getBoolean("chat.useclan",true));
        ConfigHelper.enableScB = cfg.getBoolean("enable.scoreboard",true);
        ConfigHelper.scBUpdateInterval = cfg.getInt("scoreboard.updateIntervalTicks",100);
        ConfigHelper.scBMode = cfg.getString("scoreboard.mode","ALL");
        ConfigHelper.scBDisplayPlayerCount = cfg.getBoolean("scoreboard.showPlayerCount",false);
        ConfigHelper.scBReverseSql = cfg.getBoolean("scoreboard.reversesql",false);
        ConfigHelper.initPotionProps(cfg);
        ConfigHelper.invseeEnabled = cfg.getBoolean("enable.command.invsee",true);
        ConfigHelper.userStatisticsEnabled = cfg.getBoolean("enable.userstatistics",true);
        ConfigHelper.snowballTimeoutTicks = cfg.getLong("snowball.timeoutTicks", 12000L);
        ConfigHelper.peaceEnabled = cfg.getBoolean("enable.peace", false);
        ConfigHelper.worldSpecificChat = cfg.getBoolean("enable.worldspecificchat", false);
        ConfigHelper.antiLogoutEnabled = cfg.getBoolean("enable.antilogout", false);
        ConfigHelper.secsInFight = cfg.getInt("antilogout.secsInFight", 20);
        ConfigHelper.fightAllowedCmds = cfg.getStringList("antilogout.allowedCmds");
        ChatHelper.allowedChatColors = cfg.getString("chat.farbe.allowed","012356789AaBbDdEeFfRr");
        ConfigHelper.vehicleAllowedCmds = cfg.getStringList("vehicles.allowedCmds");
    }
    private static void initPotionProps(FileConfiguration cfg){//4, 9, 15
        ConfigHelper.magicSnowballEnabled = cfg.getBoolean("enable.snowball",true);
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
//                PotionEffectType type = PotionEffectType.getById(i);
                PotionEffectType type = PotionEffectType.getByName(typeName);
                if(type == null) {
                    continue;
                }
                ConfigHelper.snowballEffects.add(new PotionEffect(type, 100, 0));//5s
            }
        }
    }
    private static void initTeamList(FileConfiguration cfg){
        //cfg.addDefault("team.groups", new String[]{"Owner=&4&lOwner:","Developer=&3Developer:",
        //        "Admin=&cAdmins:","Moderator=&5Moderatoren:","Supporter=&bSupporter"});
        List<String> lst = cfg.getStringList("team.groups");
        ConfigHelper.teamMap = new HashMap<>();//diamonds! :D
        for(int i = 0;i < lst.size(); i++){//with advanced for, the order gets messed up
            String[] strs = lst.get(i).split("=");
            ConfigHelper.teamMap.put(strs[0], strs[1]);//name->prefix
            ConfigHelper.teamGroupsInOrder.add(strs[0]);
        }
    }
}
