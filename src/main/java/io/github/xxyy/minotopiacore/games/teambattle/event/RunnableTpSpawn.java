package io.github.xxyy.minotopiacore.games.teambattle.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class RunnableTpSpawn implements Runnable {
	public Player plr;
	public RunnableTpSpawn(Player plr){
		this.plr=plr;
	}
	@Override
    public void run(){
		this.plr.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
	}
}
