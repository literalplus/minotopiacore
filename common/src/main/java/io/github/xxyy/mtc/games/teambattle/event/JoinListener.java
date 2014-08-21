package io.github.xxyy.mtc.games.teambattle.event;

import io.github.xxyy.mtc.games.teambattle.TeamBattle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public final class JoinListener implements Listener {
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e){
		if(!TeamBattle.leaveMan.doesLocExist(e.getPlayer().getName())) {
            return;
        }
		e.getPlayer().sendMessage(TeamBattle.CHAT_PREFIX +" Deine vorherige Position wurde gespeichert.");
		e.getPlayer().sendMessage(TeamBattle.CHAT_PREFIX +"Zurück? §3/war prev");
		e.getPlayer().sendMessage(TeamBattle.CHAT_PREFIX +" Nicht mehr anzeigen? §3/war prev clear");
	}
}
