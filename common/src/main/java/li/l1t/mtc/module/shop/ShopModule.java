/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop;

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.mtc.MTC;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.fulltag.FullTagModule;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import li.l1t.mtc.module.shop.task.UpdateDiscountTask;
import li.l1t.mtc.module.shop.transaction.ShopTransactionExecutor;
import li.l1t.mtc.module.shop.ui.inventory.ShopMenuListener;
import li.l1t.mtc.module.shop.ui.text.CommandShop;
import li.l1t.mtc.module.shop.ui.text.ShopTextOutput;
import li.l1t.mtc.module.shop.ui.text.admin.CommandShopAdmin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Manages the shop module. That module provides a more-or-less simple admin shop that allows
 * players to buy and sell items for Vault money.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
public class ShopModule extends ConfigurableMTCModule {
    public static final String NAME = "Shop";
    private static final String DISCOUNT_UPDATE_MINUTES_PATH = "sale_change_minutes";
    private final XyComponentBuilder prefixBuilder = new XyComponentBuilder("[").color(ChatColor.AQUA)
            .append("Shop", ChatColor.GOLD).append("]", ChatColor.AQUA).append(" ", ChatColor.GOLD);
    private final String prefix = TextComponent.toLegacyText(new XyComponentBuilder(prefixBuilder).create());
    private ShopItemConfiguration itemConfig;
    private ShopTextOutput textOutput;
    private ShopTransactionExecutor transactionExecutor;
    @InjectMe(required = false)
    private FullTagModule fullTagModule;
    private UpdateDiscountTask updateDiscountTask = new UpdateDiscountTask(this); //needs to be a field so that update interval can be reloaded on-the-fly

    public ShopModule() {
        super(NAME, "modules/shop/config.yml", ClearCacheBehaviour.RELOAD, false);
    }

    @Override
    public boolean canBeEnabled(MTCPlugin plugin) {
        if (!((MTC) plugin).getVaultHook().isEconomyHooked()) { //this also checks if Vault is installed at all
            plugin.getLogger().info("ShopModule requires Vault and a running economy provider, skipping.");
            return false;
        }
        return super.canBeEnabled(plugin);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        itemConfig = ShopItemConfiguration.fromDataFolderPath("modules/shop/items.yml", ClearCacheBehaviour.RELOAD, this);
        textOutput = new ShopTextOutput(this);
        transactionExecutor = new ShopTransactionExecutor(this);
        registerListener(new ShopMenuListener());

        registerCommand(new CommandShop(this), "shop", "xshop");
        registerCommand(new CommandShopAdmin(this), "shopadmin", "sa");
    }

    @Override
    protected void reloadImpl() {
        if (itemConfig != null) { //not yet set when called during enable
            itemConfig.trySave();
        }

        configuration.addDefault(DISCOUNT_UPDATE_MINUTES_PATH, 60);
        configuration.trySave();

        updateDiscountTask.tryCancel();
        updateDiscountTask.runTaskTimer(getPlugin(),
                60 * 20, //delay for one minute after reload for yolo reasons
                configuration.getInt(DISCOUNT_UPDATE_MINUTES_PATH, 60) * 60 * 20);
    }

    @Override
    public void disable(MTCPlugin plugin) {

    }

    @Override
    public void save() {
        itemConfig.trySave();
        super.save();
    }

    /**
     * Returns the configuration for this module. Note that this being the manager is considered an
     * implementation detail and, as such, may change without notice. Use {@link #getItemManager()}
     * where possible.
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
     * @return a component builder prefixed with the module's prefix to indicate where the message
     * comes from
     */
    public XyComponentBuilder getPrefixBuilder() {
        return new XyComponentBuilder(prefixBuilder);
    }

    /**
     * @return the text output used by this module
     */
    public ShopTextOutput getTextOutput() {
        return textOutput;
    }

    /**
     * @return the transaction executor used by this module
     */
    public ShopTransactionExecutor getTransactionExecutor() {
        return transactionExecutor;
    }

    /**
     * @return the full tag module this module interfaces with, or null if none
     */
    public FullTagModule getFullTagModule() {
        return fullTagModule;
    }
}
