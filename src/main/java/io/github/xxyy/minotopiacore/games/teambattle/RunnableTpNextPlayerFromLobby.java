package io.github.xxyy.minotopiacore.games.teambattle;

import io.github.xxyy.minotopiacore.MTC;

import org.bukkit.Bukkit;


public class RunnableTpNextPlayerFromLobby implements Runnable {

	@Override
	public void run() {
		TeamBattle.instance().addNextLobbyPlayerToGame();
		if(TeamBattle.instance().arePlayersInLobby()){
			Bukkit.getScheduler().runTaskLater(MTC.instance(), this, 3);
		}
	}

}
