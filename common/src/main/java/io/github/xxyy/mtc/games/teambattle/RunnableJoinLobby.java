package io.github.xxyy.mtc.games.teambattle;

import org.bukkit.Bukkit;

public final class RunnableJoinLobby implements Runnable {
	public CommandTeamBattleHelper helper;
	
	public RunnableJoinLobby(CommandTeamBattleHelper helper){
		this.helper = helper;
	}
	
	@Override
	public void run() {
		if(helper.plr.getLocation().distanceSquared(helper.startLoc) > 0.5){
			helper.plr.sendMessage(TeamBattle.CHAT_PREFIX +" §oDu kannst echt nicht 2 Sekunden stillhalten! Daher kannst du die Lobby leider nicht betreten :(");
			return;
		}
		if(helper.plr.getHealth() != helper.startHealth){
			helper.plr.sendMessage(TeamBattle.CHAT_PREFIX +" §oNa toll, jetzt hast du während der 2 Sekunden Schaden erhalten. Daher kannst du die Lobby leider nicht betreten :(");
			return;
		}
		//System.out.println(helper.plr.getName()+": Trying to join TeamBattle!");
		Bukkit.getScheduler().cancelTask(helper.taskId);
		helper.tryJoinLobby();
	}

}
