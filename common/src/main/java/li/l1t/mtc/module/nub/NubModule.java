/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.yaml.ManagedConfiguration;

/**
 * MTC N.u.b. (German "neu und beschützt", which means "new and protected") provides temporary protection in form of
 * god
 * mode for new players.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class NubModule extends ConfigurableMTCModule {
    @InjectMe
    private NubConfig config;

    public NubModule() {
        super("Nub", "modules/nub.cfg.yml", ClearCacheBehaviour.RELOAD, false);
    }

    @Override
    protected void reloadImpl() {
        config.loadFrom(this);
    }

    ManagedConfiguration getConfiguration() {
        return configuration;
    }

    public NubConfig getConfig() {
        return config;
    }
}
