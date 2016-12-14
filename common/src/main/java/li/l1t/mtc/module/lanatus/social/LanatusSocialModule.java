/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.social;

import li.l1t.lanatus.api.purchase.Purchase;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.ChatConstants;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.lanatus.base.LanatusBaseModule;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A small module that allows players to share the fact that they just purchased something with the world.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class LanatusSocialModule extends ConfigurableMTCModule {
    private static final String SHARE_MESSAGE_PATH = "messages.share";
    private final Map<UUID, Instant> shareablePurchases = new HashMap<>();
    private String shareMessage = "";

    @InjectMe
    public LanatusSocialModule(LanatusBaseModule baseModule) {
        super("LanatusSocial", "modules/lanatus-social.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerListener(new PostPurchaseShareListener(this));
        registerCommand(inject(LanatusSocialCommand.class), "lasocial");
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        super.clearCache(forced, plugin);
        Instant now = Instant.now();
        shareablePurchases.values().removeIf(expiry -> expiry.isBefore(now));
    }

    @Override
    protected void reloadImpl() {
        configuration.options().copyDefaults(true);
        configuration.addDefault(SHARE_MESSAGE_PATH, "§x$player hat $product im /lashop gekauft!");
        shareMessage = ChatConstants.convertCustomColorCodes(configuration.getString(SHARE_MESSAGE_PATH));
    }

    public void markShareable(Purchase purchase) {
        shareablePurchases.put(purchase.getUniqueId(), Instant.now());
    }

    public boolean isShareable(UUID purchaseId) {
        return shareablePurchases.containsKey(purchaseId);
    }

    public boolean markShared(Purchase purchase) {
        return shareablePurchases.remove(purchase.getUniqueId()) != null;
    }

    public String getShareMessage() {
        return shareMessage;
    }
}
