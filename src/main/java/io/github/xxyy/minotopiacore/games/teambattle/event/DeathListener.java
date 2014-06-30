package io.github.xxyy.minotopiacore.games.teambattle.event;

import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;



public class DeathListener implements Listener {
	@EventHandler
	public void onPlayerDeath(EntityDeathEvent e){
		if(e.getEntityType() != EntityType.PLAYER) {
            return;
        }
		Player plr = (Player)e.getEntity();
		if(plr.getKiller() == null) {
            return;
        }
		if(plr.getKiller().getType() != EntityType.PLAYER) {
            return;
        }
		if(!TeamBattle.instance().isPlayerInGame(plr.getKiller())) {
            return;
        }
		if(!TeamBattle.instance().isPlayerInGame(plr)) {
            return;
        }
		TeamBattle.instance().addTeamPoint(TeamBattle.instance().invertTeam(TeamBattle.instance().getPlayerTeam(plr)));
		TeamBattle.instance().notifyPlayersKill(plr, plr.getKiller());
		e.setDroppedExp(0);
		e.getDrops().clear();
		//TeamBattle.instance().tpPlayerToTeamSpawn(plr); //does not work
	}
}
