/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import io.github.xxyy.mtc.module.shop.ui.text.CommandShop;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Manages the shop module, allowing players to buy
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
public class ShopModule extends ConfigurableMTCModule {
    public static final String NAME = "Shop";
    private final XyComponentBuilder prefixBuilder = new XyComponentBuilder("[").color(ChatColor.AQUA)
            .append("Shop", ChatColor.GOLD).append("]", ChatColor.AQUA).append(" ", ChatColor.GOLD);
    private final String prefix = TextComponent.toLegacyText(new XyComponentBuilder(prefixBuilder).create());
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

    /**
     * Returns the configuration for this module. Note that this being the manager is considered an implementation
     * detail and, as such, may change without notice. Use {@link #getItemManager()} where possible.
     *
     * @return the configuration for this module
     */
    public ShopItemConfiguration getItemConfig() {
        return itemConfig;
    }

    /**
     * @return the shop item manager managing items for this module
     */
    public ShopItemManager getItemManager() {
        return getItemConfig();
    }

    /**
     * @return the module's chat prefix that should be used to indicated its messages
     */
    public String getChatPrefix() {
        return prefix;
    }

    /**
     * @return a component builder prefixed with the module's prefix to indicate where the message comes from
     */
    public XyComponentBuilder getPrefixBuilder() {
        return new XyComponentBuilder(prefixBuilder);
    }
}
