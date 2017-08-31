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

package li.l1t.mtc.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class DmgPotionListener implements Listener {
    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.SPLASH_POTION) {
            return;
        }
        ThrownPotion pot = (ThrownPotion) e.getDamager();
        pot.getEffects().stream()
                .filter(eff -> eff.getType().equals(PotionEffectType.HARM))
                .forEach(eff -> e.setDamage(eff.getAmplifier() + 1));
    }
}
