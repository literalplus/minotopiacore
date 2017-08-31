/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.pvpstats;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.hook.TitleManagerHook;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.pvpstats.model.CachedPlayerStatsRepository;
import li.l1t.mtc.module.pvpstats.model.PlayerStatsRepository;
import li.l1t.mtc.module.pvpstats.model.PlayerStatsRepositoryImpl;
import li.l1t.mtc.module.pvpstats.model.QueuedPlayerStatsRepository;
import li.l1t.mtc.module.pvpstats.scoreboard.PlayerStatsBoardManager;
import li.l1t.mtc.module.scoreboard.CommonScoreboardProvider;

/**
 * Manages PvP stats and stores them in a MySQL database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class PlayerStatsModule extends ConfigurableMTCModule {
    public static final String NAME = "PvPStats";
    public static final String ADMIN_PERMISSION = "mtc.stats.admin";
    private PlayerStatsRepository repository;
    private TitleManagerHook titleManagerHook;
    @InjectMe(required = true)
    private CommonScoreboardProvider scoreboardProvider;

    public PlayerStatsModule() {
        super(NAME, "modules/pvpstats.cfg.yml", ClearCacheBehaviour.RELOAD_ON_FORCED, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        getPlugin().getServer().getPluginManager().registerEvents(new StatsDeathListener(this), getPlugin());

        repository = new CachedPlayerStatsRepository(
                new QueuedPlayerStatsRepository(new PlayerStatsRepositoryImpl(this), this), this
        );
        repository.setDatabaseTable(configuration.getString("sql.database"), configuration.getString("sql.table"));
        getPlugin().getModuleManager().getInjector().registerInstance(repository, PlayerStatsRepository.class);
        titleManagerHook = new TitleManagerHook(getPlugin());
        if (isFeatureEnabled("scoreboard") && scoreboardProvider != null) {
            inject(PlayerStatsBoardManager.class).enable();
        }
        registerCommand(inject(CommandStats.class), "stats");
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
