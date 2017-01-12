/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;

/**
 * Main entry point for the Vote module which listens for votes and dispatches rewards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-28
 */
public class VoteModule extends ConfigurableMTCModule {
    protected VoteModule() {
        super("Vote", "modules/vote.cfg.yml", ClearCacheBehaviour.RELOAD);
        ConfigurationRegistration.registerAll();
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
    }

    @Override
    protected void reloadImpl() {

    }
}
