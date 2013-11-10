package io.github.xxyy.minotopiacore.games.teambattle.event;

import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;
import org.bukkit.entity.Player;

@Deprecated
public class RunnableRespawn implements Runnable {

	public Player plr;
	public RunnableRespawn(Player plr){
		this.plr=plr;
	}
	
	@Override
	public void run() {
		TeamBattle.instance().tpPlayerToTeamSpawn(plr);
		plr.sendMessage(TeamBattle.chatPrefix+" Du bist gestorben. Dein Team hat §3"+TeamBattle.instance().getTeamPoints(TeamBattle.instance().getPlayerTeam(plr))+" §7Punkte.");
		plr.sendMessage(TeamBattle.chatPrefix+" Das andere Team hat §3"+TeamBattle.instance().getTeamPoints(TeamBattle.instance().invertTeam(TeamBattle.instance().getPlayerTeam(plr)))+" §7Punkte.");
		TeamBattle.instance().giveKitToPlayer(plr);
	}

}
