package io.github.xxyy.minotopiacore.games.teambattle.tp;

import io.github.xxyy.minotopiacore.MTC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public final class RunnableTp implements Runnable {

	public List<Player> plrs;
	public List<Location> locs;
	public RunnableTp(){
		this.plrs=new ArrayList<>();
		this.locs=new ArrayList<>();
	}
	public RunnableTp(List<Player> plrs){
		this.plrs=plrs;
		this.locs=new ArrayList<>();
	}
	public RunnableTp(List<Player> plrs, List<Location> locs){
		this.plrs=plrs;
		this.locs=locs;
	}
	public RunnableTp(Player plr,Location loc){
		this.plrs=new ArrayList<>();
		this.locs=new ArrayList<>();
		this.plrs.add(plr);
		this.locs.add(loc);
	}
	@Override
	public void run() {
		System.out.println("Runnable!");
		Location loc = this.locs.get(0);
		Player plr = this.plrs.get(0);
		plr.chat("/spawn");
		plr.teleport(loc);
		this.locs.remove(0);
		this.plrs.remove(0);
		if(!this.locs.isEmpty() && !this.plrs.isEmpty()){
			Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableTp(this.plrs,this.locs), 1);
		}
	}

}
