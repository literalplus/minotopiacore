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

package li.l1t.mtc.module.lanatus.shop;

import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.LanatusConnected;
import li.l1t.lanatus.shop.api.ItemIconService;
import li.l1t.lanatus.shop.api.ProductBuyService;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.MTCModuleAdapter;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.lanatus.shop.category.SqlCategoryRepository;
import li.l1t.mtc.module.lanatus.shop.command.LanatusCategoryCommand;
import li.l1t.mtc.module.lanatus.shop.command.LanatusShopCommand;
import li.l1t.mtc.module.lanatus.shop.listener.CategoryDisplayNotAffordableListener;
import li.l1t.mtc.module.lanatus.shop.metrics.StatsdPurchaseRecorder;
import li.l1t.mtc.module.lanatus.shop.service.SimpleItemIconService;
import li.l1t.mtc.module.lanatus.shop.service.SimpleProductBuyService;
import li.l1t.mtc.module.metrics.StatsdModule;
import org.apache.logging.log4j.Logger;

/**
 * Module providing a GUI shop for Lanatus.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class LanatusShopModule extends MTCModuleAdapter implements LanatusConnected {
    public static final String NAME = "LanatusShop";
    private static final Logger LOGGER = LogManager.getLogger(LanatusShopModule.class);
    @InjectMe(failSilently = true)
    private MTCLanatusClient lanatus;
    @InjectMe(required = false)
    private StatsdModule statsdModule;
    @InjectMe
    private SqlCategoryRepository categoryRepository;
    @InjectMe
    private SimpleProductBuyService buyService;
    @InjectMe
    private SimpleItemIconService iconService;

    protected LanatusShopModule() {
        super(NAME, true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(new LanatusShopCommand(this), "lashop", "pshop");
        registerCommand(new LanatusCategoryCommand(this), "lacat")
                .behaviour(CommandBehaviours.permissionChecking("mtc.lanatus.admin"));
        if (statsdModule != null) {
            LOGGER.info("Using Statsd purchase recorder.");
            buyService.setPurchaseRecorder(new StatsdPurchaseRecorder(statsdModule.statsd()));
        } else {
            LOGGER.info("Using dummy purchase recorder.");
        }
        registerListener(new CategoryDisplayNotAffordableListener(iconService));
    }

    public SqlCategoryRepository categories() {
        return categoryRepository;
    }

    public ItemIconService iconService() {
        return iconService;
    }

    public ProductBuyService buyService() {
        return buyService;
    }

    @Override
    public LanatusClient client() {
        return lanatus;
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        if (forced) {
            categoryRepository.clearCache();
        }
    }
}
