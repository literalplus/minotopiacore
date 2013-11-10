package io.github.xxyy.minotopiacore.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DmgPotionListener implements Listener{
	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e){
		if(e.getDamager().getType() != EntityType.SPLASH_POTION) return;
		ThrownPotion pot = (ThrownPotion)e.getDamager();
		for(PotionEffect eff : pot.getEffects()){
		    if(eff.getType().equals(PotionEffectType.HARM)){
		        e.setDamage(eff.getAmplifier() + 1);
		    }
		}
	}
}
