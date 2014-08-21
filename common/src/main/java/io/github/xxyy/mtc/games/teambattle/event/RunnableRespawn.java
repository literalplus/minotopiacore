package io.github.xxyy.mtc.games.teambattle.event;

import io.github.xxyy.mtc.games.teambattle.TeamBattle;
import org.bukkit.entity.Player;

@Deprecated
public final class RunnableRespawn implements Runnable {

	public Player plr;
	public RunnableRespawn(Player plr){
		this.plr=plr;
	}
	
	@Override
	public void run() {
		TeamBattle.instance().tpPlayerToTeamSpawn(plr);
		plr.sendMessage(TeamBattle.CHAT_PREFIX +" Du bist gestorben. Dein Team hat §3"+TeamBattle.instance().getTeamPoints(TeamBattle.instance().getPlayerTeam(plr))+" §7Punkte.");
		plr.sendMessage(TeamBattle.CHAT_PREFIX +" Das andere Team hat §3"+TeamBattle.instance().getTeamPoints(TeamBattle.instance().invertTeam(TeamBattle.instance().getPlayerTeam(plr)))+" §7Punkte.");
		TeamBattle.instance().giveKitToPlayer(plr);
	}

}
