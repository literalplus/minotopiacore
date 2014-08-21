package io.github.xxyy.mtc.games.teambattle;


public final class RunnableLeaveBattle implements Runnable {
	public CommandTeamBattleHelper helper;
	
	public RunnableLeaveBattle(CommandTeamBattleHelper helper){
		this.helper = helper;
	}
	
	@Override
	public void run() {
		//System.out.println(helper.plr.getName()+": Trying to join TeamBattle!");
		//Bukkit.getScheduler().cancelTask(helper.taskId);
		helper.doLeaveGame();
	}

}
