/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk;

import li.l1t.mtc.yaml.ManagedConfiguration;

/**
 * Provides some common configurable properties and other properties about the Perks module.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class PerksConfig {

    private static final String MAX_CONCURRENT_PERKS_PATH = "concurrent-perk-limit";
    private int concurrentPerkLimit = 3;
    private boolean shopModuleAvailable = false;

    public void reloadFrom(LanatusPerkModule module) {
        ManagedConfiguration configuration = module.getConfig();
        configuration.options().copyDefaults(true);
        configuration.addDefault(MAX_CONCURRENT_PERKS_PATH, concurrentPerkLimit);
        concurrentPerkLimit = configuration.getInt(MAX_CONCURRENT_PERKS_PATH);
        shopModuleAvailable = module.isLanatusShopAvailable();
        module.save();
    }

    public int getConcurrentPerkLimit() {
        return concurrentPerkLimit;
    }

    public boolean isLanatusShopAvailable() {
        return shopModuleAvailable;
    }
}
