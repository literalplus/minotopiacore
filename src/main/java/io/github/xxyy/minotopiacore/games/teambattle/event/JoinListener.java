package io.github.xxyy.minotopiacore.games.teambattle.event;

import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinListener implements Listener {
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e){
		if(!TeamBattle.leaveMan.doesLocExist(e.getPlayer().getName())) return;
		e.getPlayer().sendMessage(TeamBattle.chatPrefix+" Deine vorherige Position wurde gespeichert.");
		e.getPlayer().sendMessage(TeamBattle.chatPrefix+"Zurück? §3/war prev");
		e.getPlayer().sendMessage(TeamBattle.chatPrefix+" Nicht mehr anzeigen? §3/war prev clear");
	}
}
