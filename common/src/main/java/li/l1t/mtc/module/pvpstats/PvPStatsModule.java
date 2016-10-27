/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.pvpstats;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.hook.ProtocolLibHook;
import li.l1t.mtc.hook.TitleManagerHook;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.pvpstats.model.CachedPlayerStatsRepository;
import li.l1t.mtc.module.pvpstats.model.PlayerStatsRepository;
import li.l1t.mtc.module.pvpstats.model.PlayerStatsRepositoryImpl;
import li.l1t.mtc.module.pvpstats.model.QueuedPlayerStatsRepository;
import li.l1t.mtc.module.pvpstats.scoreboard.PvPStatsBoardManager;

/**
 * Manages PvP stats and stores them in a MySQL database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class PvPStatsModule extends ConfigurableMTCModule {
    public static final String NAME = "PvPStats";
    public static final String ADMIN_PERMISSION = "mtc.stats.admin";
    private PlayerStatsRepository repository;
    private TitleManagerHook titleManagerHook;

    public PvPStatsModule() {
        super(NAME, "modules/pvpstats.cfg.yml", ClearCacheBehaviour.RELOAD_ON_FORCED, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        registerCommand(new CommandStats(this), "stats");
        getPlugin().getServer().getPluginManager().registerEvents(new StatsDeathListener(this), getPlugin());

        repository = new CachedPlayerStatsRepository(
                new QueuedPlayerStatsRepository(new PlayerStatsRepositoryImpl(this), this), this
        );
        repository.setDatabaseTable(configuration.getString("sql.database"), configuration.getString("sql.table"));

        titleManagerHook = new TitleManagerHook(getPlugin());

        if (isFeatureEnabled("scoreboard")) {
            if (!new ProtocolLibHook(getPlugin()).isActive()) {
                getPlugin().getLogger().severe("If you wish to use PvP Stats with Scoreboards, please install ProtocolLib!");
            } else {
                new PvPStatsBoardManager(this).enable();
            }
        }
    }

    @Override
    public void disable(MTCPlugin plugin) {
        repository.cleanup();
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        repository.cleanup();
    }

    @Override
    protected void reloadImpl() {
        configuration.options().copyDefaults(true);
        configuration.addDefault("sql.database", "mt_main");
        configuration.addDefault("sql.table", "pvpstats");
        configuration.addDefault("enable.title.killer", true);
        configuration.addDefault("enable.title.victim", false);
        configuration.addDefault("enable.scoreboard", false);
        configuration.trySave();
    }

    public boolean isFeatureEnabled(String featureName) {
        return configuration.getBoolean("enable." + featureName, false);
    }

    public PlayerStatsRepository getRepository() {
        return repository;
    }

    public TitleManagerHook getTitleManagerHook() {
        return titleManagerHook;
    }
}
