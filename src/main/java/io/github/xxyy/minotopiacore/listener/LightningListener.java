package io.github.xxyy.minotopiacore.listener;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class LightningListener implements Listener {
	/**
	 * public void onLightningStrike(LightningStrikeEvent e)
	 * Catches LightningStrikes and turns hit cows to Mooshrooms at a 50% chance, names them
	 * "§6Salted", adds Speed 10, makes them adult, sets their Name Tag visible,
	 * gives them full health, stops them from despawning and allows them to breathe underwater for 42,000 ticks.
	 * @param e
	 */
	@EventHandler
	public void onLightningStrike(LightningStrikeEvent e){
		//System.out.println("Lightning!");
		LightningStrike bolt = e.getLightning();
		List<Entity> nearbyEntities = bolt.getNearbyEntities(2, 2, 2);
		for(Entity item : nearbyEntities){
			if(item.getType() == EntityType.COW){
				Cow cow=(Cow)item;
				cow.setFireTicks(0);
				cow.setHealth(cow.getMaxHealth());
				cow.setCustomName("§3Karl-Heinz Müller");
				cow.setCustomNameVisible(true);
				cow.addPotionEffect(new PotionEffect(PotionEffectType.HEAL,20*10,10),true);
				cow.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*10,20),true);
				int rand=(new Random()).nextInt(2);
				if(rand != 0) return;//50% chance
				Location loc = item.getLocation();
				MushroomCow moo = item.getWorld().spawn(loc, MushroomCow.class);
				moo.setFireTicks(0);
				moo.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*10,20),true);
				moo.setAdult();
				moo.setCustomName("§4§lPeppered");
				moo.setCustomNameVisible(true);
				moo.setHealth(moo.getMaxHealth());
				moo.setMaximumAir(42000);
				moo.setRemoveWhenFarAway(false);
				moo.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*600,20), true);
				//Bukkit.broadcast("§6[§3MTS§6]Eine Kuh wurde vom Blitz getroffen und ist jetzt eine Pilzkuh!", "mtc.misc.lightning.notify");
				//Bukkit.broadcast("§6[§3MTS§6]Die Pilzkuh befindet sich bei:x="+loc.getBlockX()+",y="+loc.getBlockY()+",z="+loc.getBlockZ()+".", "mtc.misc.lightning.notify.coordinates");
				
			}
		}
	}
}
