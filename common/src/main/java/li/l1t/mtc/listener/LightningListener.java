/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2018 Philipp Nowak (https://github.com/xxyy) and contributors.
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

import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.MushroomCow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

/**
 * Catches LightningStrikes and turns hit cows to Mooshrooms at a 50% chance, names them "§6Salted",
 * adds speed 10, makes them adult, sets their Name Tag visible, gives them full health, stops them
 * from despawning and allows them to breathe underwater for 42,000 ticks.
 */
public final class LightningListener implements Listener {

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e) {
        //System.out.println("Lightning!");
        LightningStrike bolt = e.getLightning();
        List<Entity> nearbyEntities = bolt.getNearbyEntities(2, 2, 2);
        for (Entity item : nearbyEntities) {
            if (item.getType() == EntityType.COW) {
                Cow cow = (Cow) item;
                cow.setFireTicks(0);
                cow.setHealth(cow.getMaxHealth());
                cow.setCustomName("§3Karl-Heinz Müller");
                cow.setCustomNameVisible(true);
                cow.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 * 10, 10), true);
                cow.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 10, 20), true);
                int rand = (new Random()).nextInt(2);
                if (rand != 0) {
                    return;//50% chance
                }
                Location loc = item.getLocation();
                MushroomCow moo = item.getWorld().spawn(loc, MushroomCow.class);
                moo.setFireTicks(0);
                moo.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 10, 20), true);
                moo.setAdult();
                moo.setCustomName("§4§lPeppered");
                moo.setCustomNameVisible(true);
                moo.setHealth(moo.getMaxHealth());
                moo.setMaximumAir(42000);
                moo.setRemoveWhenFarAway(false);
                moo.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 600, 20), true);
                //Bukkit.broadcast("§6[§3MTS§6]Eine Kuh wurde vom Blitz getroffen und ist jetzt eine Pilzkuh!", "mtc.misc.lightning.notify");
                //Bukkit.broadcast("§6[§3MTS§6]Die Pilzkuh befindet sich bei:x="+loc.getBlockX()+",y="+loc.getBlockY()+",z="+loc.getBlockZ()+".", "mtc.misc.lightning.notify.coordinates");

            }
        }
    }
}
