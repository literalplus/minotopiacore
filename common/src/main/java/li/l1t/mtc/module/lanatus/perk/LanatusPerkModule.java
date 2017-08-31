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

package li.l1t.mtc.module.lanatus.perk;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.lanatus.perk.api.CompoundPerkFactory;
import li.l1t.mtc.module.lanatus.perk.listener.NoHungerPerkListener;
import li.l1t.mtc.module.lanatus.perk.listener.PostPurchasePerkListener;
import li.l1t.mtc.module.lanatus.perk.perk.StringPerkFactory;
import li.l1t.mtc.module.lanatus.perk.repository.SqlPerkRepository;
import li.l1t.mtc.module.lanatus.perk.ui.text.CommandPerks;
import li.l1t.mtc.module.lanatus.shop.LanatusShopModule;
import li.l1t.mtc.yaml.ManagedConfiguration;

/**
 * Provides selectable perks that may be purchased through Lanatus.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class LanatusPerkModule extends ConfigurableMTCModule {
    public static final String MODULE_NAME ="mtc-laperk";
    @InjectMe
    private SqlPerkRepository perkRepository;
    @InjectMe
    private StringPerkFactory perkFactory;
    @InjectMe
    private LocalPerkManager manager;
    @InjectMe(required = false)
    private LanatusShopModule shopModule;
    @InjectMe
    private CommandPerks commandPerks;
    @InjectMe
    private PerksConfig config;

    public LanatusPerkModule() {
        super("LanatusPerk", "modules/lanatus-perk.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(commandPerks, "laperks", "perks");
        registerListener(new PostPurchasePerkListener(perkRepository));
        registerListener(new NoHungerPerkListener());
    }

    @Override
    protected void reloadImpl() {
        config.reloadFrom(this);
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        if (forced) {
            perkRepository.clearCache();
        }
    }

    public CompoundPerkFactory perkFactory() {
        return perkFactory;
    }

    public LocalPerkManager getManager() {
        return manager;
    }

    boolean isLanatusShopAvailable() {
        return shopModule != null;
    }

    ManagedConfiguration getConfig() {
        return configuration;
    }
}
