/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */
package io.github.xxyy.mtc;

import me.minotopia.mitoscb.SBHelper;
import me.minotopia.mitoscb.SqlConsts2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.common.localisation.XyLocalizable;
import io.github.xxyy.common.misc.HelpManager;
import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.sql.SqlConnectable;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.version.PluginVersion;
import io.github.xxyy.common.xyplugin.SqlXyPlugin;
import io.github.xxyy.mtc.api.PlayerGameManager;
import io.github.xxyy.mtc.chat.ChatListener;
import io.github.xxyy.mtc.chat.CommandChatClear;
import io.github.xxyy.mtc.chat.CommandChatFarbe;
import io.github.xxyy.mtc.chat.CommandGlobalMute;
import io.github.xxyy.mtc.chat.CommandMute;
import io.github.xxyy.mtc.chat.CommandPrivateChat;
import io.github.xxyy.mtc.chat.MTCChatHelper;
import io.github.xxyy.mtc.chat.cmdspy.CmdSpyListener;
import io.github.xxyy.mtc.chat.cmdspy.CommandCmdSpy;
import io.github.xxyy.mtc.clan.ui.CommandClan;
import io.github.xxyy.mtc.clan.ui.CommandClanAdmin;
import io.github.xxyy.mtc.cron.RunnableCronjob5Minutes;
import io.github.xxyy.mtc.fulltag.CommandFull;
import io.github.xxyy.mtc.fulltag.FullTagListener;
import io.github.xxyy.mtc.gettime.CommandTime;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.helper.StatsHelper;
import io.github.xxyy.mtc.hook.PexHook;
import io.github.xxyy.mtc.hook.VaultHook;
import io.github.xxyy.mtc.hook.WorldGuardHook;
import io.github.xxyy.mtc.hook.XLoginHook;
import io.github.xxyy.mtc.listener.*;
import io.github.xxyy.mtc.misc.AntiLogoutHandler;
import io.github.xxyy.mtc.misc.PlayerGameManagerImpl;
import io.github.xxyy.mtc.misc.cmd.CommandBReload;
import io.github.xxyy.mtc.misc.cmd.CommandGiveAll;
import io.github.xxyy.mtc.misc.cmd.CommandList;
import io.github.xxyy.mtc.misc.cmd.CommandLore;
import io.github.xxyy.mtc.misc.cmd.CommandMTC;
import io.github.xxyy.mtc.misc.cmd.CommandPeace;
import io.github.xxyy.mtc.misc.cmd.CommandPlayerHead;
import io.github.xxyy.mtc.misc.cmd.CommandRandom;
import io.github.xxyy.mtc.misc.cmd.CommandTeam;
import io.github.xxyy.mtc.module.InfiniteBlockModule;
import io.github.xxyy.mtc.module.MTCModuleAdapter;
import io.github.xxyy.mtc.module.chal.ChalModule;
import io.github.xxyy.mtc.module.quiz.QuizModule;
import io.github.xxyy.mtc.module.repeater.RepeaterModule;
import io.github.xxyy.mtc.module.truefalse.TrueFalseModule;
import io.github.xxyy.mtc.module.website.WebsiteModule;

import java.util.logging.Level;

public class MTC extends SqlXyPlugin implements XyLocalizable {

    public static final PluginVersion PLUGIN_VERSION = PluginVersion.ofClass(MTC.class);
    public static SqlConsts2 tMconsts; //TODO
    public static int speedOnJoinPotency = -1; //TODO <--

    private static MTC instance;

    public static String priChatCol = "§6";
    public static String codeChatCol = "§3";
    public static String chatPrefix = "§6[§bMTS§6] ";
    public static String banChatPrefix = "§6[§bMTS§6] ";
    public static String warnChatPrefix = "§6[§bMTS§6] ";

    public SafeSql ssql2 = null; //TODO

    public String serverName = "UnknownServer"; //TODO whatever
    public String warnBanServerSuffix = "§7§o[UnknownServer]"; //TODO lol?

    public boolean pvpMode = true; //TODO otha clazz
    public boolean cycle = true; //TODO store somehere else
    //Hooks
    private VaultHook vaultHook;
    private XLoginHook xLoginHook;
    private WorldGuardHook worldGuardHook;
    private AntiLogoutHandler logoutHandler;
    private PexHook pexHook;

    private boolean showDisableMsg = true;
    private static boolean useHologram = false;
    private PlayerGameManager gameManager;

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigHelper.onConfigReload(this);
        MTCModuleAdapter.forEach(m -> m.reload(this));
    }

    @Override
    public void disable() {
        MTCModuleAdapter.forEach(m -> {
            try {
                m.disable(this);
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Error occurred while disabling MTC module " + m.getName() + ": ", e);
            }
        });

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
                LogHelper.getMainLogger().log(Level.FINE, "ItemOnCursor @" + plr.getName() + ": " + itemOnCursor);
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

        //COMMANDS
        registerCommands();

        //MODULES
        loadModules();

        //HELP
        MTCHelper.initHelp();

        //LSITENERS
        registerEventListeners(pluginManager);

        //RUNNABLES
        if (this.getConfig().getBoolean("enable.cron.5m", true)) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RunnableCronjob5Minutes(false, this), 2 * 60 * 20, 5 * 60 * 20);
            Bukkit.getConsoleSender().sendMessage("§8[MTC] AUTOSAVE enabled!");
        }

        //HOOKS
        this.xLoginHook = new XLoginHook(this);
        this.vaultHook = new VaultHook(this);
        this.worldGuardHook = new WorldGuardHook(this);
        this.pexHook = new PexHook(this);

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


            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SBHelper(this).getUpdateTask(),
                    ConfigHelper.getScBUpdateInterval(), ConfigHelper.getScBUpdateInterval());
        }

        //BUNGECORD
        if (this.getConfig().getBoolean("enable.bungeeapi", true)) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "mtcAPI");
        }

        //SERVER NAME
        this.warnBanServerSuffix = this.getConfig().getString("warnban.serversuffix", "§7§o[Unknown]");
        this.serverName = this.getConfig().getString("servername", "UNKNOWN");

        //LOGS
        LogHelper.initLogs();

        //SQL LOGGER
        this.getSql().errLogger = LogHelper.getMainLogger();

        //API
        gameManager = new PlayerGameManagerImpl(this);

        MTCModuleAdapter.getInstances().stream()
                .filter(m -> m.isEnabled(this))
                .forEach(m -> {
                    try {
                        m.enable(this);
                    } catch (Throwable e) {
                        getLogger().warning("Could not enable " + m.getName() + ":");
                        e.printStackTrace();
                    }
                });
        saveConfig(); //Save here so that changes from modules also apply to the config file

        //PREPARING FOR BEING DISABLED
        this.showDisableMsg = this.getConfig().getBoolean("enable.msg.disablePlug", true);

        MTC.useHologram = pluginManager.getPlugin("HolographicDisplays") != null;

        if (this.getConfig().getBoolean("enable.msg.enablePlug", true)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MTC]MTC enabled,Sir!");
        }
    }

    private void loadModules() { //TODO: proper, configurable, maybe even annotation-based loading thing
        MTCModuleAdapter.load(this, InfiniteBlockModule.class, TrueFalseModule.class, WebsiteModule.class,
                QuizModule.class, RepeaterModule.class, ChalModule.class);
    }

    private void registerCommands() {
        this.getCommand("gtime").setExecutor(new CommandTime());
        this.getCommand("lore").setExecutor(new CommandLore());
        this.getCommand("mtc").setExecutor(new CommandMTC(instance));
        this.getCommand("cmdspy").setExecutor(new CommandCmdSpy());
        this.getCommand("playerhead").setExecutor(new CommandPlayerHead());
        this.getCommand("giveall").setExecutor(new CommandGiveAll());
        if (this.getConfig().getBoolean("enable.command.breload", true)) {
            this.getCommand("breload").setExecutor(new CommandBReload());
        }
        if (this.getConfig().getBoolean("enable.chat", true)) { //CHAT
            this.getCommand("globalmute").setExecutor(new CommandGlobalMute());
            this.getCommand("chatclear").setExecutor(new CommandChatClear());
            this.getCommand("chatfarbe").setExecutor(new CommandChatFarbe(this));
            this.getCommand("chat").setExecutor(new CommandPrivateChat());
            this.getCommand("mute").setExecutor(new CommandMute());
        }
        if (this.getConfig().getBoolean("enable.fulltag", true)) {
            this.getCommand("full").setExecutor(new CommandFull());
        }
        if (ConfigHelper.isClanEnabled()) {
            this.getCommand("xclan").setExecutor(new CommandClan(this));
            this.getCommand("clanadmin").setExecutor(new CommandClanAdmin());
        }
        if (this.getConfig().getBoolean("enable.command.team", true)) {
            this.getCommand("team").setExecutor(new CommandTeam(this));
        }
        this.getCommand("list").setExecutor(new CommandList());
        this.setExecAndCompleter(new CommandPeace(), "frieden");
        this.setExec(new CommandRandom(), "random");
    }

    private void registerEventListeners(PluginManager pm) {
        if (this.getConfig().getBoolean("enable.misc.lighting.cow", true)) {
            pm.registerEvents(new LightningListener(), this);
        }
        if (this.getConfig().getBoolean("enable.chat", true)) {
            pm.registerEvents(new ChatListener(this), this);
            Bukkit.getConsoleSender().sendMessage("§8[MTC] CHAT enabled!");
        }
        if (ConfigHelper.isEnableTablist() || MTC.speedOnJoinPotency > 0 || ConfigHelper.isEnableItemOnJoin() || ConfigHelper.
                isClanEnabled()) {
            pm.registerEvents(new MainJoinListener(), this);
        }

        if (this.getConfig().getBoolean("enable.antilogout", false)) {
            AntiLogoutListener listener = new AntiLogoutListener(this);
            pm.registerEvents(listener, this);
            logoutHandler = listener;
        } else {
            logoutHandler = (id) -> false; //overrides isFighting method - I know this is dirty, suggest something better if you have it
        }

        this.regEvents(pm, new LightningListener(), "enable.misc.lighting.cow", true);
        this.regEvents(pm, new DmgPotionListener(), "enable.betterdmgpotions", true);
        this.regEvents(pm, new MinecartPortalListener(), "enable.fixes.minecartPortal", true);
        this.regEvents(pm, new NetherPortalExpDupeListener(this), "enable.fixes.expPortal", true);
        this.regEvents(pm, new EnderPearlProjectileLaunchListener(), "enable.enderpearlListener", true);
        this.regEvents(pm, new AntiFreeCamListener(), "enable.fixes.freecam", true);
        this.regEvents(pm, new AnvilNBrewingStandStackListener(), "enable.anvilNbrewingstandStackFix", true);
        this.regEvents(pm, new AntiInfPotionListener(), "enable.infPotionFix", true);
        this.regEvents(pm, new MoveNetherRoofListener(this), "enable.netherrooffix", true);
        this.regEvents(pm, new FullTagListener(), "enable.fulltag", true);
        this.regEvents(pm, new ColoredSignListener(), "enable.signcolor", true);
        this.regEvents(pm, new PlayerHideInteractListener(), "enable.playerhide", false);
        this.regEvents(pm, new CmdSpyListener(), "enable.cmdspy", true);
        pm.registerEvents(new MainDamageListener(this), this);
        if (ConfigHelper.isProhibitCmdsInBoats()) {
            pm.registerEvents(new VehicleInventoryOpenListener(), this);
        }
        if (ConfigHelper.isStatsEnabled() || ConfigHelper.isClanEnabled()) {
            pm.registerEvents(new StatsDeathListener(), this);
        }
        if (ConfigHelper.isMagicSnowballEnabled()) {
            pm.registerEvents(new MagicSnowballHitListener(), this);
        }
        pm.registerEvents(new MainCommandListener(this), this);
    }

    @Override
    public String getChatPrefix() {
        return MTC.chatPrefix;
    }

    @Override
    public String[] getShippedLocales() {
        return Const.SHIPPED_LANGUAGES;
    }

    @Override
    protected SqlConnectable getConnectable() {
        return new SqlConnectable() {
            @Override
            public String getSqlDb() {
                return getConfig().getString("sql.db");
            }

            @Override
            public String getSqlHost() {
                return getConfig().getString("sql.host");
            }

            @Override
            public String getSqlPwd() {
                return getConfig().getString("sql.password");
            }

            @Override
            public String getSqlUser() {
                return getConfig().getString("sql.user");
            }
        };
    }

    private <T extends Listener> T regEvents(PluginManager pm, T listener, String cfgOption, boolean defaultValue) {
        if (!this.getConfig().getBoolean(cfgOption, defaultValue)) {
            return null;
        }

        pm.registerEvents(listener, this);

        return listener;
    }

    public static MTC instance() {
        return MTC.instance;
    }

    public static boolean isUseHologram() {
        return MTC.useHologram;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public XLoginHook getXLoginHook() {
        return xLoginHook;
    }

    public PexHook getPexHook() {
        return pexHook;
    }

    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }

    public AntiLogoutHandler getLogoutHandler() {
        return logoutHandler;
    }

    public PlayerGameManager getGameManager() {
        return gameManager;
    }
}
