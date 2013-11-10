package io.github.xxyy.minotopiacore.games.teambattle;


public class RunnableLeaveBattle implements Runnable {
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
