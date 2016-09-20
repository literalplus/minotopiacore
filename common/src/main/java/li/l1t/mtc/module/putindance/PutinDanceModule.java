/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;

/**
 * A module that provides the PutinDance mini-game for events. PutinDance is based around a board
 * filled with different wool colors. Every time an admin executes /pd tick, some blocks are removed
 * from the board. The last player to be on a wool block wins. The board consists of multiple
 * layers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public class PutinDanceModule extends ConfigurableMTCModule {
    public static final String NAME = "PutinDance";
    public static final String ADMIN_PERMISSION = "mtc.putindance.admin";
    private final PutinDanceConfig config = new PutinDanceConfig();
    private WandHandler wandHandler;

    protected PutinDanceModule() {
        super(NAME, "modules/events/putindance.cfg.yml", ClearCacheBehaviour.SAVE, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        wandHandler = new WandHandler(getPlugin(), config);
    }

    @Override
    protected void reloadImpl() {
        config.loadFrom(configuration);
    }

    @Override
    public void save() {
        config.saveTo(configuration);
        super.save();
    }

    public PutinDanceConfig getConfig() {
        return config;
    }
}
