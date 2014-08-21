package io.github.xxyy.mtc.games.teambattle;

import io.github.xxyy.mtc.MTC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;


public final class TeamBattleSign { //REFACTOR class name

	public static Location signLocation;
	public static boolean enabled=false;
	public static int taskId=-1;

    private TeamBattleSign() {

    }

	/**
	 * public static boolean updateSign()
	 * Updates the sign.
	 * @return If something is invalid and the task should be stopped, return false.
	 */
	public static boolean updateSign(){
		if(!enabled) {
            return false;
        }
		Block blk = signLocation.getWorld().getBlockAt(signLocation);
		if(!(blk.getType() == Material.WALL_SIGN) && !(blk.getType() == Material.SIGN_POST)){
			System.out.println("Das TeamBattle-InfoSchild ist kein Schild! /wa setsign");
			return false;
		}
		Sign sgn=(Sign)blk.getState();
		sgn.setLine(0, TeamBattle.instance().getChatTeamName(TeamBattleTeams.Blue)+":");
		sgn.setLine(2, TeamBattle.instance().getChatTeamName(TeamBattleTeams.Red)+":");
		sgn.setLine(1, "§3"+TeamBattle.instance().getTeamPoints(TeamBattleTeams.Blue)+" §6Punkte");
		sgn.setLine(3, "§3"+TeamBattle.instance().getTeamPoints(TeamBattleTeams.Red)+" §6Punkte");
		sgn.update();
		System.out.println("Updated TeamBattleSign!");
		return true;
	}
	//also checks if enabled
	public static void getSignLoc(){
		boolean enabld=TeamBattle.instance().cfg.getBoolean("options.sign.enabled",true);
		TeamBattleSign.enabled=enabld;
		if(!enabld) {
            return;
        }
		int secs = TeamBattle.instance().cfg.getInt("options.sign.updateEveryXSeconds",5);
		int x=TeamBattle.instance().cfg.getInt("options.sign.x",0);
		int y=TeamBattle.instance().cfg.getInt("options.sign.y",0);
		int z=TeamBattle.instance().cfg.getInt("options.sign.z",0);
		String world=TeamBattle.instance().cfg.getString("options.sign.worldName","world");
		TeamBattleSign.signLocation =new Location(Bukkit.getWorld(world),x,y,z);
		updateSign();
		
		//auto-update
		if(secs <= 0) {
            return;
        }
		TeamBattleSign.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MTC.instance(), new Runnable(){
			private boolean continueRunning=true;
			@Override
			public void run() {
				if(!this.continueRunning){
					Bukkit.getScheduler().cancelTask(TeamBattleSign.taskId);
					return;
				}
				this.continueRunning = TeamBattleSign.updateSign();
			}
			
		}, (secs*20)+300, secs*20);//delaying first run because of reload
	}
	public static void setSignLoc(String worldName,int x,int y,int z){
		TeamBattle.instance().cfg.set("options.sign.x", x);
		TeamBattle.instance().cfg.set("options.sign.y", y);
		TeamBattle.instance().cfg.set("options.sign.z", z);
		TeamBattle.instance().cfg.set("options.sign.worldName", worldName);
		TeamBattleSign.signLocation =new Location(Bukkit.getWorld(worldName),x,y,z);
	}
}
