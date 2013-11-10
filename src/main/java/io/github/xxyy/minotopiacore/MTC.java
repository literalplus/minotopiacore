/*
 ▒█▀▄▀█ ░▀░ █▀▀▄ █▀▀█ ▀▀█▀▀ █▀▀█ █▀▀█ ░▀░ █▀▀█ ▒█▀▀█ █▀▀█ █▀▀█ █▀▀
 ▒█▒█▒█ ▀█▀ █░░█ █░░█ ░▒█░░ █░░█ █░░█ ▀█▀ █▄▄█ ▒█░░░ █░░█ █▄▄▀ █▀▀
 ▒█░░▒█ ▀▀▀ ▀░░▀ ▀▀▀▀ ░▒█░░ ▀▀▀▀ █▀▀▀ ▀▀▀ ▀░░▀ ▒█▄▄█ ▀▀▀▀ ▀░▀▀ ▀▀▀
 Copyright (C) 2013 xxyy98.
 Decompilation, redistribution or usage
 without explicit written permission
 by the author are not permitted and
 may result in legal steps being
 taken.

 This program uses the Bukkit API but
 is not in any way affiliated with it
 and/or it's authors.
 */
package io.github.xxyy.minotopiacore;

import io.github.xxyy.common.HelpManager;
import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.common.localisation.XyLocalizable;
import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.xyplugin.SqlXyPlugin;
import io.github.xxyy.minotopiacore.bans.cmd.*;
import io.github.xxyy.minotopiacore.bans.listener.BanJoinListener;
import io.github.xxyy.minotopiacore.chat.*;
import io.github.xxyy.minotopiacore.clan.ui.*;
import io.github.xxyy.minotopiacore.cron.RunnableCronjob5Minutes;
import io.github.xxyy.minotopiacore.fulltag.CommandFull;
import io.github.xxyy.minotopiacore.fulltag.FullTagListener;
import io.github.xxyy.minotopiacore.games.teambattle.CommandTeamBattle;
import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;
import io.github.xxyy.minotopiacore.games.teambattle.admin.CommandTeamBattleAdmin;
import io.github.xxyy.minotopiacore.games.teambattle.event.*;
import io.github.xxyy.minotopiacore.gettime.CommandTime;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import io.github.xxyy.minotopiacore.helper.StatsHelper;
import io.github.xxyy.minotopiacore.listener.*;
import io.github.xxyy.minotopiacore.misc.cmd.*;
import io.github.xxyy.minotopiacore.warns.CommandDeleteWarn;
import io.github.xxyy.minotopiacore.warns.CommandListWarns;
import io.github.xxyy.minotopiacore.warns.CommandWarn;
import io.github.xxyy.minotopiacore.warns.CommandWarnStats;
import java.util.logging.Level;
import lombok.Getter;
import me.minotopia.mitoscb.RunnableUpdateBoards;
import me.minotopia.mitoscb.SBHelper;
import me.minotopia.mitoscb.SqlConsts2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MTC extends SqlXyPlugin implements XyLocalizable {

    private static MTC instance;
    private boolean showDisableMsg = true;
    public static String priChatCol = "§6";
    public static String codeChatCol = "§3";
//    public static String versionName = "'Loligo vulgaris'";
    public static String chatPrefix = "§6[§bMTS§6] ";
    public static String banChatPrefix = "§6[§bMTS§6] ";
    public static String warnChatPrefix = "§6[§bMTS§6] ";
//    public String motd = "§6§lMinoTopia.me";
    public SafeSql ssql2 = null;
    public static SqlConsts2 tMconsts;
    public TeamBattle tb;
    @Getter
    private static net.milkbowl.vault.chat.Chat chat = null;
    @Getter
    private static net.milkbowl.vault.permission.Permission perms;
    @Getter
    private static net.milkbowl.vault.economy.Economy econ;
    public static int SpeedOnJoinPotency = -1; //TODO <--
    public String serverName = "UnknownServer"; //TODO whatever
    public String warnBanServerSuffix = "§7§o[UnknownServer]"; //TODO lol?
    public Location spawn = null; //TODO wtf
    public boolean pvpMode = true; //TODO otha clazz
    public boolean cycle = true; //TODO store somehere else

    @Override
    public void disable() {

        //TEAMBATTLE
        if (this.tb != null) {
            TeamBattle.instance().tpAllPlayersToPrevLoc();
            this.tb.finish();
        }

        //SQL
        if (this.ssql2 != null) {
            this.ssql2.preReload();
        }

        ///HELP
        HelpManager.clearHelpManagers();

        //CHAT
        MTCChatHelper.clearPrivateChats();

        //CLEANING
        StatsHelper.flushQueue();

        //SCOREBOARD
        for (Player plr : Bukkit.getOnlinePlayers()) {
            plr.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        if (this.showDisableMsg) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MTC]MTC disabled :(");
        }

        for (Player plr : Bukkit.getOnlinePlayers()) {
            final ItemStack itemOnCursor = plr.getItemOnCursor();
            if (itemOnCursor != null) {
                LogHelper.getMainLogger().log(Level.FINE, "ItemOnCursor @{0}: {1}", new Object[] {plr.getName(), itemOnCursor});
                plr.setItemOnCursor(null);
            }
            plr.closeInventory();
        }
        LogHelper.flushAll();

        MTC.instance = null;
    }
    //Enabling the Plugin

    @Override
    public void enable() {
        MTC.instance = this;
        this.reloadConfig();
        final PluginManager pluginManager = this.getServer().getPluginManager();

        if (!this.getConfig().getBoolean("enable.mtc", true)) {
            Bukkit.getConsoleSender().sendMessage("§8[MTC]§eAborting! Disabled in config.");
            pluginManager.disablePlugin(this);
            return;
        }

        //XYC LOCALIZATION
        LangHelper.copyLangsFromJar(this, this);
        if (LangHelper.localiseString("XU-name", "xxyy98", this.getName()).equals("XU-name")) {
            CommandHelper.sendMessageToOpsAndConsole("§e§l[MTC][WARNING] Language files not loaded. There may be some funny messages.");
        }

        //CONFIG
        ConfigHelper.initMainConfig();
//        this.motd = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("motd", "§6§lMinoTopia.me"));

        //COMMANDS
        registerCommands();

        //HELP
        MTCHelper.initHelp();

        //LSITENERS
        registerEventListeners(pluginManager);

        //RUNNABLES
        if (this.getConfig().getBoolean("enable.cron.5m", true)) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RunnableCronjob5Minutes(false), 2 * 60 * 20, 5 * 60 * 20);
            Bukkit.getConsoleSender().sendMessage("§8[MTC] AUTOSAVE enabled!");
        }

        //VAULT
        if (this.getConfig().getBoolean("enable.chat", true)) //not needed if not TODO: ConfigHelper
        {
            if (!MTC.setupChat()) {
                CommandHelper.sendMessageToOpsAndConsole(
                        "§4[MTC][WARNING] Could not find any Valut Chat Provider! Chat will not display prefixes.");
            }

            if (!MTC.setupPermissions()) {
                CommandHelper.sendMessageToOpsAndConsole(
                        "§4[MTC][WARNING] Could not find any Vault Permission Provider!");
            }
        }
        if (ConfigHelper.isClanEnabled() && !MTC.setupEconomy()) {
            CommandHelper.sendMessageToOpsAndConsole(
                    "§4[MTC][WARNING] Could not find any Vault Economy Provider!");
        }


        //SCOREBOARD
        if (ConfigHelper.isEnableScB()) {
            String mode = ConfigHelper.getScBMode();
            switch (mode) {
            case "PVP":
                this.pvpMode = true;
                this.cycle = false;
                break;
            case "TM":
                this.pvpMode = false;
                this.cycle = false;
                break;
            case "ALL":
            default:
                this.cycle = true;
            }

            MTC.tMconsts = new SqlConsts2();
            if (!mode.equalsIgnoreCase("PVP") && !MTC.tMconsts.getSqlUser().equalsIgnoreCase("")) {
                this.ssql2 = new SafeSql(MTC.tMconsts);
            }

            SBHelper.init();

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RunnableUpdateBoards(),
                    ConfigHelper.getScBUpdateInterval(), ConfigHelper.getScBUpdateInterval());
        }

        //BUNGECORD
        if (this.getConfig().getBoolean("enable.bungeeapi", true)) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "mtcAPI");
        }

        //TEAMBATTLE
        if (this.getConfig().getBoolean("enable.teambattle", true)) { //TODO remove?
            this.tb = new TeamBattle();
        }

        //SERVER NAME
        this.warnBanServerSuffix = this.getConfig().getString("warnban.serversuffix", "§7§o[Unknown]");
        this.serverName = this.getConfig().getString("servername", "UNKNOWN");

        //NETHERROOFSPAWN
        this.refreshSpawn();

        //LOGS
        LogHelper.initLogs();

        //SQL LOGGER
        this.getSql().errLogger = LogHelper.getMainLogger();

        //PREPARING FOR BEING DIABLED
        this.showDisableMsg = this.getConfig().getBoolean("enable.msg.disablePlug", true);

        if (this.getConfig().getBoolean("enable.msg.enablePlug", true)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MTC]MTC enabled,Sir!");
        }
    }

    private void registerCommands() {
        this.getCommand("gtime").setExecutor(new CommandTime());
        this.getCommand("lore").setExecutor(new CommandLore());
        this.getCommand("mtc").setExecutor(new CommandMTC());
        this.getCommand("cmdspy").setExecutor(new CommandCmdSpy());
        this.getCommand("playerhead").setExecutor(new CommandPlayerHead());
        this.getCommand("giveall").setExecutor(new CommandGiveAll());
        if (this.getConfig().getBoolean("enable.command.breload", true)) {
            this.getCommand("breload").setExecutor(new CommandBReload());
        }
        if (this.getConfig().getBoolean("enable.teambattle", true)) {
            this.getCommand("war").setExecutor(new CommandTeamBattle());
            this.getCommand("waradmin").setExecutor(new CommandTeamBattleAdmin());
        }
        if (this.getConfig().getBoolean("enable.chat", true)) {//CHAT
            this.getCommand("globalmute").setExecutor(new CommandGlobalMute());
            this.getCommand("chatclear").setExecutor(new CommandChatClear());
            this.getCommand("chatfarbe").setExecutor(new CommandChatFarbe());
            this.getCommand("chat").setExecutor(new CommandPrivateChat());
            this.getCommand("mute").setExecutor(new CommandMute());
        }
        if (this.getConfig().getBoolean("enable.bans", true)) {//BANS
            this.getCommand("ban").setExecutor(new CommandBan());
            this.getCommand("baninfo").setExecutor(new CommandBaninfo());
            this.getCommand("tempban").setExecutor(new CommandTempban());
            this.getCommand("unban").setExecutor(new CommandUnban());
        }
        if (this.getConfig().getBoolean("enable.warns", true)) {//WARNS
            this.getCommand("addwarn").setExecutor(new CommandWarn());
            this.getCommand("remwarn").setExecutor(new CommandDeleteWarn());
            this.getCommand("listwarns").setExecutor(new CommandListWarns());
            this.getCommand("warnstats").setExecutor(new CommandWarnStats());
        }
        if (this.getConfig().getBoolean("enable.fulltag", true)) {
            this.getCommand("full").setExecutor(new CommandFull());
        }
        if (ConfigHelper.isClanEnabled()) {
            this.getCommand("xclan").setExecutor(new CommandClan());
            this.getCommand("clanadmin").setExecutor(new CommandClanAdmin());
        }
        if (this.getConfig().getBoolean("enable.command.team", true)) {
            this.getCommand("team").setExecutor(new CommandTeam());
        }
        if (this.getConfig().getBoolean("enable.infdisp", true)) {
            this.getCommand("infdisp").setExecutor(new CommandInfiniteDispenser());
        }
        this.getCommand("list").setExecutor(new CommandList());
        this.setExecAndCompleter(new CommandPeace(), "frieden");
        this.setExec(new CommandRandom(), "random");
    }

    private void registerEventListeners(PluginManager pm) {
        if (this.getConfig().getBoolean("enable.misc.lighting.cow", true)) {
            pm.registerEvents(new LightningListener(), this);
        }
        if (this.getConfig().getBoolean("enable.teambattle", true)) {
            pm.registerEvents(new LeaveListener(), this);
            pm.registerEvents(new CmdListener(), this);
            pm.registerEvents(new JoinListener(), this);
            if (this.getConfig().getBoolean("enable.dmgevent", true)) {
                pm.registerEvents(new DmgListener(), this);
            } else {
                pm.registerEvents(new DeathListener(), this);
                pm.registerEvents(new RespawnListener(), this);
            }
        }
        if (this.getConfig().getBoolean("enable.chat", true)) {
            pm.registerEvents(new ChatListener(), this);
            Bukkit.getConsoleSender().sendMessage("§8[MTC] CHAT enabled!");
        }
        if (ConfigHelper.isEnableTablist() || ConfigHelper.isNoobProtection() || MTC.SpeedOnJoinPotency > 0 || ConfigHelper.isEnableItemOnJoin() || ConfigHelper.
                isClanEnabled()) {
            pm.registerEvents(new MainJoinListener(), this);
        }

        this.regEvents(pm, new LightningListener(), "enable.misc.lighting.cow", true);
        this.regEvents(pm, new DmgPotionListener(), "enable.betterdmgpotions", true);
        this.regEvents(pm, new MinecartPortalListener(), "enable.fixes.minecartPortal", true);
        this.regEvents(pm, new EnderPearlProjectileLaunchListener(), "enable.enderpearlListener", true);
        this.regEvents(pm, new AntiFreeCamListener(), "enable.fixes.freecam", true);
        this.regEvents(pm, new BanJoinListener(), "enable.bans", true);
        this.regEvents(pm, new AnvilNBrewingStandStackListener(), "enable.anvilNbrewingstandStackFix", true);
        this.regEvents(pm, new AntiInfPotionListener(), "enable.infPotionFix", true);
        this.regEvents(pm, new MoveNetherRoofListener(), "enable.netherrooffix", true);
        this.regEvents(pm, new MotdPingListener(), "enable.motd", true);
        this.regEvents(pm, new FullTagListener(), "enable.fulltag", true);
        this.regEvents(pm, new ColoredSignListener(), "enable.signcolor", true);
        this.regEvents(pm, new PlayerHideInteractListener(), "enable.playerhide", false);
        this.regEvents(pm, new AntiLogoutListener(), "enable.antilogout", false);
        this.regEvents(pm, new InfiniteDispenseListener(), "enable.infdisp", true);
        if (ConfigHelper.isClanEnabled()) {
            pm.registerEvents(new MainDamageListener(), this);
        }
        if (ConfigHelper.isProhibitCmdsInBoats()) {
            pm.registerEvents(new MainInventoryOpenListener(), this);
        }
        if (ConfigHelper.isStatsEnabled() || ConfigHelper.isClanEnabled()) {
            pm.registerEvents(new StatsDeathListener(), this);
        }
        if (ConfigHelper.isMagicSnowballEnabled()) {
            pm.registerEvents(new MagicSnowballHitListener(), this);
        }
        pm.registerEvents(new MainCommandListener(), this);
    }

    @Override
    public String getChatPrefix() {
        return MTC.chatPrefix;
    }

    @Override
    public String[] getShippedLocales() {
        return Const.shippedLanguages;
    }

    @Override
    public String getSqlDb() {
        return this.getConfig().getString("sql.db");
    }

    @Override
    public String getSqlHost() {
        return this.getConfig().getString("sql.host");
    }

    @Override
    public String getSqlPwd() {
        return this.getConfig().getString("sql.password");
    }

    @Override
    public String getSqlUser() {
        return this.getConfig().getString("sql.user");
    }

    public void refreshSpawn() {
        this.spawn = new Location(Bukkit.getWorld(this.getConfig().getString("fixes.netherroof.spawn.worldName", "world")),
                this.getConfig().getInt("fixes.netherroof.spawn.x", 0),
                this.getConfig().getInt("fixes.netherroof.spawn.y", 70),
                this.getConfig().getInt("fixes.netherroof.spawn.z", 0),
                (float) this.getConfig().getDouble("fixes.netherroof.spawn.yaw", 0),
                (float) this.getConfig().getDouble("fixes.netherroof.spawn.pitch", 0));
    }

    private <T extends Listener> void regEvents(PluginManager pm, T listener, String cfgOption, boolean defaultValue) {
        if (!this.getConfig().getBoolean(cfgOption, defaultValue)) {
            return;
        }
        pm.registerEvents(listener, this);
    }

    /**
     * public static Plugin instance() gets the instance of MinoTopiaCore.
     *
     * @return Plugin
     */
    public static MTC instance() {
        //PluginManager pm = Bukkit.getPluginManager();
        return MTC.instance;
    }

    public static boolean setupChat() {
        try {
            RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> rsp = MTC.instance.getServer().getServicesManager().getRegistration(
                    net.milkbowl.vault.chat.Chat.class);
            MTC.chat = rsp.getProvider();
            return MTC.chat != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setupEconomy() {
        try {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = MTC.instance.getServer().getServicesManager().getRegistration(
                    net.milkbowl.vault.economy.Economy.class);
            MTC.econ = rsp.getProvider();
            return MTC.econ != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setupPermissions() {
        try {
            RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> rsp = MTC.instance.getServer().getServicesManager().getRegistration(
                    net.milkbowl.vault.permission.Permission.class);
            MTC.perms = rsp.getProvider();
            return MTC.perms != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
