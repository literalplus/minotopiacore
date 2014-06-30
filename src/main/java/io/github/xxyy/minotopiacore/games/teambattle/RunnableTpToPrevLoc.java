package io.github.xxyy.minotopiacore.games.teambattle;


public final class RunnableTpToPrevLoc implements Runnable {
	public CommandTeamBattleHelper helper;
	
	public RunnableTpToPrevLoc(CommandTeamBattleHelper helper){
		this.helper = helper;
	}
	
	@Override
	public void run() {
		if(helper.plr.getLocation().distanceSquared(helper.startLoc) > 0.5){
			helper.plr.sendMessage(TeamBattle.chatPrefix+" §oDu kannst echt nicht 2 Sekunden stillhalten! Daher kannst du nicht teleportiert werden :(");
			return;
		}
		if(helper.plr.getHealth() != helper.startHealth){
			helper.plr.sendMessage(TeamBattle.chatPrefix+" §oNa toll, jetzt hast du während der 2 Sekunden Schaden erhalten. Daher kannst du nicht teleportiert werden :(");
			return;
		}
		helper.doTpPrev();
	}

}
