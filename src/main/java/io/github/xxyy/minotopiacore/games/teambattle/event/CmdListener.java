package io.github.xxyy.minotopiacore.games.teambattle.event;

import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public final class CmdListener implements Listener {
	@EventHandler(ignoreCancelled=true,priority=EventPriority.LOWEST)
	public void onPlayerPreCmd(PlayerCommandPreprocessEvent e){
		if(TeamBattle.instance() == null || !TeamBattle.instance().isPlayerInGame(e.getPlayer())) {
            return;
        }
		if(e.getMessage().startsWith("/login")) { afterLogin(e.getPlayer()); return;}
		if(e.getMessage().startsWith("/war ") || e.getMessage().startsWith("/wa ")){return;}
		if(checkPermAndMsg(e.getPlayer())){
			e.getPlayer().sendMessage(TeamBattle.chatPrefix+" Du kannst aufgrund deiner Rechte Befehle im TeamBattle benutzen. §8Bitte missbrauche das nicht!");
			return;
		}
		e.setCancelled(true);
		e.getPlayer().sendMessage(TeamBattle.chatPrefix+" Du darfst nur §3/war §7verwenden!");
	}
	
	public boolean checkPermAndMsg(Player plr){
        return plr.hasPermission("mtc.teambattle.admin.execute.other");
    }
	
	public void afterLogin(Player plr){
		if(TeamBattle.instance().isPlayerInGame(plr) || TeamBattle.leaveMan.hasPlayerUsedLogin(plr.getName())){
			plr.sendMessage(TeamBattle.chatPrefix+" 404 Bug nicht gefunden. Vielen Dank an alle Buguser, ihr seid einfach super..");
			return;
		}
		if(TeamBattle.leaveMan.doesLocExist(plr.getName())){
			plr.teleport(Bukkit.getWorld(TeamBattle.leaveMan.getWorldName(plr.getName())).getSpawnLocation());
			plr.sendMessage(TeamBattle.chatPrefix+" Du wurdest zum Spawn teleportiert, da du das Spiel");
			plr.sendMessage(TeamBattle.chatPrefix+" in der Arena verlassen hast.");
			plr.sendMessage(TeamBattle.chatPrefix+" Deine vorherige Position wurde gespeichert.");
			plr.sendMessage(TeamBattle.chatPrefix+" Zur§ck mit §3/war prev§7, l§schen mit §3/war prev clear");
			TeamBattle.leaveMan.addPlayerToLoginUsed(plr.getName());
		}
		Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableTpSpawn(plr),10);
		
	}
}
