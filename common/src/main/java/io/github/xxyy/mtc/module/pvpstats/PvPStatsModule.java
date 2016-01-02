/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats;

import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;

/**
 * Manages PvP stats and stores them in a MySQL database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class PvPStatsModule extends ConfigurableMTCModule {
    public static final String NAME = "PvPStats";
    public static final String ADMIN_PERMISSION = "mtc.stats.admin";

    public PvPStatsModule() {
        super(NAME, "modules/pvpstats.cfg.yml", ClearCacheBehaviour.RELOAD_ON_FORCED);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        registerCommand(new CommandStats(this), "stats");
    }

    @Override
    public void disable(MTCPlugin plugin) {
        super.disable(plugin); //TODO: not yet implemented overriden method
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        super.clearCache(forced, plugin); //TODO: not yet implemented overriden method
    }

    @Override
    protected void reloadImpl() {

    }
}
