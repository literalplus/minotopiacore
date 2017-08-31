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
