/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;

/**
 * Manages the shop module, allowing players to buy
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
public class ShopModule extends ConfigurableMTCModule {
    public static final String NAME = "Shop";
    private ShopItemConfiguration itemConfig;

    public ShopModule() {
        super(NAME, "modules/shop/config.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public boolean canBeEnabled(MTC plugin) {
        if (!plugin.getVaultHook().isEconomyHooked()) { //this also checks if Vault is installed at all
            getPlugin().getLogger().info("ShopModule requires Vault and a running economy provider, skipping.");
            return false;
        }
        return super.canBeEnabled(plugin);
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        itemConfig = ShopItemConfiguration.fromDataFolderPath("modules/shop/items.yml", ClearCacheBehaviour.RELOAD, getPlugin());

        plugin.getCommand("shop").setExecutor(new CommandShop(this));
    }

    @Override
    protected void reloadImpl() {
        //FIXME reload item config
    }

    @Override
    public void save() {
        itemConfig.trySave();
        super.save();
    }

    public ShopItemConfiguration getItemConfig() {
        return itemConfig;
    }
}
