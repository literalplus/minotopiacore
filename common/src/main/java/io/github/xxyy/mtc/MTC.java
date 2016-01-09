/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */
package io.github.xxyy.mtc;

import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.common.localisation.XyLocalizable;
import io.github.xxyy.common.misc.HelpManager;
import io.github.xxyy.common.sql.SqlConnectable;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.version.PluginVersion;
import io.github.xxyy.common.xyplugin.SqlXyPlugin;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.api.PlayerGameManager;
import io.github.xxyy.mtc.api.module.ModuleManager;
import io.github.xxyy.mtc.chat.*;
import io.github.xxyy.mtc.chat.cmdspy.CommandCmdSpy;
import io.github.xxyy.mtc.chat.cmdspy.CommandSpyListener;
import io.github.xxyy.mtc.clan.ui.CommandClan;
import io.github.xxyy.mtc.clan.ui.CommandClanAdmin;
import io.github.xxyy.mtc.cron.RunnableCronjob5Minutes;
import io.github.xxyy.mtc.gettime.CommandTime;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.hook.PexHook;
import io.github.xxyy.mtc.hook.VaultHook;
import io.github.xxyy.mtc.hook.WorldGuardHook;
import io.github.xxyy.mtc.hook.XLoginHook;
import io.github.xxyy.mtc.listener.*;
import io.github.xxyy.mtc.logging.LogManager;
import io.github.xxyy.mtc.misc.AntiLogoutHandler;
import io.github.xxyy.mtc.misc.DummyLogoutHandler;
import io.github.xxyy.mtc.misc.PlayerGameManagerImpl;
import io.github.xxyy.mtc.misc.cmd.*;
import io.github.xxyy.mtc.module.MTCModuleManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class MTC extends SqlXyPlugin implements XyLocalizable, MTCPlugin {

    public static final PluginVersion PLUGIN_VERSION = PluginVersion.ofClass(MTC.class);
    public static int speedOnJoinPotency = -1; //TODO <--
    public static String priChatCol = "§6";
    public static String codeChatCol = "§3";
    public static String chatPrefix = "§6[§bMTS§6] ";
    private static MTC instance;
    private static boolean useHologram = false;
    public String serverName = "UnknownServer"; //TODO whatever
    public String warnBanServerSuffix = "§7§o[UnknownServer]"; //TODO lol?
    //Hooks
    private VaultHook vaultHook;
    private XLoginHook xLoginHook;
    private WorldGuardHook worldGuardHook;
    private AntiLogoutHandler logoutHandler;
    private PexHook pexHook;
    private PlayerGameManager gameManager;
    private MTCModuleManager moduleManager = new MTCModuleManager(this, this.getDataFolder());
    private Logger logger;

    public MTC() {
        initialise();
    }

    public MTC(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        super(loader, descriptionFile, dataFolder, file);
        initialise();
    }

    private void initialise() {
        //Must be here because command spy filters need the logging system before #enable()
        LogManager.setPlugin(this); // I don't like this either, but this enables us to specify static LOGGER fields
    }

    /**
     * @return an instance of MTC. No guarantees are made as to which and if it's actually usable.
     * @deprecated static `getInstance()` methods are a code smell and should not be used unless absolutely necessary
     */
    @Deprecated
    public static MTC instance() {
        return MTC.instance;
    }

    public static boolean isUseHologram() {
        return MTC.useHologram;
    }
    //Enabling the Plugin

    @Override
    public void reloadConfig() {
        log(Level.INFO, "(Re)loading MTC config...");
        super.reloadConfig();
        ConfigHelper.onConfigReload(this);
        log(Level.DEBUG, "Reloading MTC module configs...");
        moduleManager.getEnabledModules().forEach(m -> m.reload(this));
        log(Level.DEBUG, "Reloaded MTC configs!");
    }

    @Override
    public void disable() {
        try {
            log(Level.DEBUG, "Disabling modules...");
            moduleManager.getEnabledModules().forEach(m -> moduleManager.setEnabled(m, false));
            log(Level.DEBUG, "Disabled modules!");

            ///HELP
            HelpManager.clearHelpManagers();

            //CHAT
            MTCChatHelper.clearPrivateChats();

            //SCOREBOARD
            for (Player plr : Bukkit.getOnlinePlayers()) {
                plr.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
            LogHelper.flushAll();
        } catch (Exception e) {
            getLogger().warning("Unable to disable MTC. Resources might not have been freed up properly, restart asap.");
            e.printStackTrace();
            logger.error("Encountered an exception while attempting to disable. Resources might still be open. " +
                    "Please restart the container as soon as possible.");
            logger.error("Encountered this: ", e);
            logger.info("Attempting to finish important cleanup tasks...");
            CommandHelper.broadcast(String.format("§cMTC failed to disable - %s: %s",
                    e.getClass().getSimpleName(), e.getMessage()), "mtc.ignore");
        }


        for (Player plr : Bukkit.getOnlinePlayers()) {
            final ItemStack itemOnCursor = plr.getItemOnCursor();
            if (itemOnCursor != null && itemOnCursor.getType() != Material.AIR) {
                getLog().info("itemOnCursor @{}: {}", plr.getName(), itemOnCursor);
                plr.setItemOnCursor(null);
            }
            plr.closeInventory();
        }

        MTC.instance = null;
        log(Level.INFO, "I'm afraid, Dave.");
        LogManager.setPlugin(null);

        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MTC] MTC disabled =(");
    }

    @Override
    public void enable() {
        MTC.instance = this;
        try {
            logger = LogManager.getLogger(getClass());
            logger.info("====== Enabling {}...", PLUGIN_VERSION.toString());
            logger.info("Container: {}", getServer().getVersion());

            this.reloadConfig();
            final PluginManager pluginManager = this.getServer().getPluginManager();

            if (!this.getConfig().getBoolean("enable.mtc", true)) {
                logger.warn("idk why anyone use that option but we're disabled");
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
            logger.debug("Loading modules!");
            moduleManager.load(moduleManager.findShippedModules());

            //HELP
            MTCHelper.initHelp();

            //LISTENERS
            registerEventListeners(pluginManager);

            //RUNNABLES
            if (this.getConfig().getBoolean("enable.cron.5m", true)) {
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RunnableCronjob5Minutes(false, this), 2 * 60 * 20, 5 * 60 * 20);
                getLogger().info("Automatically saving world every 5 minutes.");
            }

            //HOOKS
            logger.debug("Hooking hooks!");
            this.xLoginHook = new XLoginHook(this);
            this.vaultHook = new VaultHook(this);
            this.worldGuardHook = new WorldGuardHook(this);
            this.pexHook = new PexHook(this);

            //BUNGEECORD
            if (this.getConfig().getBoolean("enable.bungeeapi", true)) {
                Bukkit.getMessenger().registerOutgoingPluginChannel(this, "mtcAPI");
            }

            //SERVER NAME
            this.warnBanServerSuffix = this.getConfig().getString("warnban.serversuffix", "§7§o[Unknown]");
            this.serverName = this.getConfig().getString("servername", "UNKNOWN");

            //LOGS
            logger.debug("Initialising legacy logging system!");
            logger.debug("Don't tell it, but I hate it. It's just ugly and stuff.");
            LogHelper.initLogs();

            //SQL LOGGER
            this.getSql().errLogger = LogHelper.getMainLogger();

            //API
            gameManager = new PlayerGameManagerImpl(this);

            logger.debug("Enabling modules!");
            moduleManager.enableLoaded();
            saveConfig(); //Save here so that changes from modules also apply to the config file
            logger.debug("Enabled MTC modules!");

            MTC.useHologram = pluginManager.getPlugin("HolographicDisplays") != null;

            if (this.getConfig().getBoolean("enable.msg.enablePlug", true)) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MTC] MTC enabled!");
            }
            logger.info("Hello world!");
        } catch (Exception e) {
            getLogger().warning("MTC encountered an exception at enable!");
            e.printStackTrace();
            logger.error("Encountered an exception while attempting to enable. Entering undefined behaviour!");
            logger.error("Here's what I caught: " + e);
            CommandHelper.broadcast(String.format("§4§lMTC failed to enable - %s: %s; Notify devops asap!",
                    e.getClass().getSimpleName(), e.getMessage()), "mtc.ignore");
            throw e;
        }
    }

    private void registerCommands() {
        this.getCommand("gtime").setExecutor(new CommandTime());
        this.getCommand("lore").setExecutor(new CommandLore());
        this.getCommand("mtc").setExecutor(new CommandMTC(instance));
        this.getCommand("cmdspy").setExecutor(new CommandCmdSpy());
        this.getCommand("playerhead").setExecutor(new CommandPlayerHead());
        this.getCommand("giveall").setExecutor(new CommandGiveAll());
        if (this.getConfig().getBoolean("enable.command.breload", true)) {
            this.getCommand("breload").setExecutor(new CommandBReload(this));
        }
        if (this.getConfig().getBoolean("enable.chat", true)) { //CHAT
            this.getCommand("globalmute").setExecutor(new CommandGlobalMute());
            this.getCommand("chatclear").setExecutor(new CommandChatClear(this));
            this.getCommand("chatfarbe").setExecutor(new CommandChatFarbe(this));
            this.getCommand("chat").setExecutor(new CommandPrivateChat());
            this.getCommand("mute").setExecutor(new CommandMute());
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
        this.setExec(new CommandRandom(this), "random");
    }

    private void registerEventListeners(PluginManager pm) {
        if (this.getConfig().getBoolean("enable.misc.lighting.cow", true)) {
            pm.registerEvents(new LightningListener(), this);
        }
        if (this.getConfig().getBoolean("enable.chat", true)) {
            pm.registerEvents(new ChatListener(this), this);
        }
        if (ConfigHelper.isEnableTablist() || MTC.speedOnJoinPotency > 0 || ConfigHelper.isEnableItemOnJoin() || ConfigHelper.
                isClanEnabled()) {
            pm.registerEvents(new MainJoinListener(this), this);
        }

        if (this.getConfig().getBoolean("enable.antilogout", false)) {
            AntiLogoutListener listener = new AntiLogoutListener(this);
            pm.registerEvents(listener, this);
            logoutHandler = listener;
        } else {
            logoutHandler = new DummyLogoutHandler();
        }

        this.regEvents(pm, new LightningListener(), "enable.misc.lighting.cow", true);
        this.regEvents(pm, new DmgPotionListener(), "enable.betterdmgpotions", true);
        this.regEvents(pm, new MinecartPortalListener(), "enable.fixes.minecartPortal", true);
        this.regEvents(pm, new NetherPortalExpDupeListener(this), "enable.fixes.expPortal", true);
        this.regEvents(pm, new EnderPearlProjectileLaunchListener(this), "enable.enderpearlListener", true);
        this.regEvents(pm, new AntiFreeCamListener(), "enable.fixes.freecam", true);
        this.regEvents(pm, new AnvilNBrewingStandStackListener(), "enable.anvilNbrewingstandStackFix", true);
        this.regEvents(pm, new AntiInfPotionListener(), "enable.infPotionFix", true);
        this.regEvents(pm, new MoveNetherRoofListener(this), "enable.netherrooffix", true);
        this.regEvents(pm, new ColoredSignListener(), "enable.signcolor", true);
        this.regEvents(pm, new PlayerHideInteractListener(), "enable.playerhide", false);
        this.regEvents(pm, new CommandSpyListener(), "enable.cmdspy", true);
        pm.registerEvents(new MainDamageListener(this), this);
        if (ConfigHelper.isProhibitCmdsInBoats()) {
            pm.registerEvents(new VehicleInventoryOpenListener(), this);
        }
        if (ConfigHelper.isMagicSnowballEnabled()) {
            pm.registerEvents(new MagicSnowballHitListener(this), this);
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


    private void log(Level level, String message) {
        if (logger != null) {
            logger.log(level, message);
        }
    }

    private <T extends Listener> T regEvents(PluginManager pm, T listener, String cfgOption, boolean defaultValue) {
        if (!this.getConfig().getBoolean(cfgOption, defaultValue)) {
            return null;
        }

        pm.registerEvents(listener, this);

        return listener;
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

    @Override
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    /**
     * Obtains the logger instance used by this plugin to log to its custom Log4j logging context. This does not
     * use Bukkit defaults or provide a java.util.logging API, try {@link #getLogger()} for that.
     *
     * @return Obtains the logger instance used by this plugin
     */
    public Logger getLog() {
        return logger;
    }
}
