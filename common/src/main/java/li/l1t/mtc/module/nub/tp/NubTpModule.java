/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.tp;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.nub.NubModule;

/**
 * Module that allows players to teleport to random coordinates for ingame money. Provides one free usage
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class NubTpModule extends ConfigurableMTCModule {
    @InjectMe
    private NubTpConfig config;
    @InjectMe
    private NubTpCommand command;

    @InjectMe(failSilently = true)
    public NubTpModule(NubModule module) {
        super("NubTp", "modules/nub/tp.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(command, "ntp", "nubtp");
    }

    @Override
    protected void reloadImpl() {
        config.loadFrom(configuration);
    }
}
