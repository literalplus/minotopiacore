/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.simple;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

/**
 * Simple module that attempts to restore the 1.9 saturation healing mechanics to something more like the (saner) 1.8
 * behaviour.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class RegenNerfModule extends ConfigurableMTCModule implements Listener {
    private static final String SATURATION_REGEN_FACTOR_PATH = "saturation-regen-factor-this-is-half-a-heart-per-half-a-second-by-default-so-zero-point-five-could-be-a-sane-value";
    private double saturationRegenFactor = 0.5D;

    private RegenNerfModule() {
        super("RegenNerf", "modules/regennerf.cfg.yml", ClearCacheBehaviour.RELOAD, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerListener(this);
    }

    @Override
    protected void reloadImpl() {
        configuration.options().copyDefaults(true);
        configuration.addDefault(SATURATION_REGEN_FACTOR_PATH, saturationRegenFactor);
        configuration.trySave();
        saturationRegenFactor = configuration.getDouble(SATURATION_REGEN_FACTOR_PATH);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setAmount(event.getAmount() * saturationRegenFactor);
        }
    }
}
