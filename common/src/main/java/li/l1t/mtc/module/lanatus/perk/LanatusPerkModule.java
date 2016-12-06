/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.lanatus.perk.repository.PerkRepository;

/**
 * Provides selectable perks that may be purchased through Lanatus.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class LanatusPerkModule extends ConfigurableMTCModule {
    private static final String MAX_CONCURRENT_PERKS_PATH = "concurrent-perk-limit";
    private int concurrentPerkLimit = 3;
    @InjectMe
    private PerkRepository perkRepository;

    public LanatusPerkModule() {
        super("LanatusPerk", "modules/lanatus-perk.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
    }

    @Override
    protected void reloadImpl() {
        configuration.options().copyDefaults(true);
        configuration.addDefault(MAX_CONCURRENT_PERKS_PATH, concurrentPerkLimit);
        concurrentPerkLimit = configuration.getInt(MAX_CONCURRENT_PERKS_PATH);
        save();
    }

    public int getConcurrentPerkLimit() {
        return concurrentPerkLimit;
    }
}
