package io.github.xxyy.mtc.games.teambattle.event;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.games.teambattle.TeamBattle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public final class LeaveListener implements Listener {
	@EventHandler(ignoreCancelled=true)
	public void onPlayerKicked(PlayerKickEvent e){
		if(TeamBattle.instance().isPlayerInQueue(e.getPlayer())){
			LeaveListener.doKickFromLobby(e.getPlayer());
			return;
		}
		if(!TeamBattle.instance().isPlayerInGame(e.getPlayer())) {
            return;
        }
		System.out.println("Player was kicked while in TeamBattle. Leaving..");
		LeaveListener.doKick(e.getPlayer());
	}
	@EventHandler(ignoreCancelled=true)
	public void onPlayerLeave(PlayerQuitEvent e){
		if(TeamBattle.instance() == null) {
            return;
        }
		if(TeamBattle.instance().isPlayerInQueue(e.getPlayer())){
			LeaveListener.doKickFromLobby(e.getPlayer());
			return;
		}
		if(!TeamBattle.instance().isPlayerInGame(e.getPlayer())) {
            return;
        }
		System.out.println("Player did not leave TeamBattle. Leaving..");
		LeaveListener.doKick(e.getPlayer());
	}
	private static void doKick(Player plr){
		TeamBattle.instance().removePlayerFromGame(plr);
		//TeamBattle.instance().tpPlayerToPrevLocation(plr);
		CommandHelper.clearInv(plr);
		TeamBattle.leaveMan.writePlayerNode(TeamBattle.instance().getPrevLocation(plr),plr);
		TeamBattle.leaveMan.removePlayerFromLoginUsed(plr.getName());
	}
	private static void doKickFromLobby(Player plr){
		TeamBattle.instance().removePlayerFromLobby(plr);
		//TeamBattle.instance().tpPlayerToPrevLocation(plr);
		TeamBattle.leaveMan.writePlayerNode(TeamBattle.instance().getPrevLocation(plr),plr);
		TeamBattle.leaveMan.removePlayerFromLoginUsed(plr.getName());
	}
}
