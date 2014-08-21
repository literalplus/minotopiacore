package io.github.xxyy.mtc.games.teambattle;

import io.github.xxyy.mtc.MTC;

import org.bukkit.Bukkit;


public final class RunnableTpNextPlayerFromLobby implements Runnable {

	@Override
	public void run() {
		TeamBattle.instance().addNextLobbyPlayerToGame();
		if(TeamBattle.instance().arePlayersInLobby()){
			Bukkit.getScheduler().runTaskLater(MTC.instance(), this, 3);
		}
	}

}
