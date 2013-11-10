package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.warns.WarnHelper;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class MoveNetherRoofListener implements Listener {
	@EventHandler
	public void onNetherMove(PlayerMoveEvent e){
		if(!e.getTo().getWorld().getEnvironment().equals(Environment.NETHER)) return;
		if(e.getTo().getBlockY() > 125){
			if(e.getPlayer().hasPermission("mtc.ignore")) return;
			e.getPlayer().sendMessage("§c[MTC] §eDu darfst nicht über das Netherdach :)");
			e.getPlayer().sendMessage("§c[MTC] §eDu hast einen Warn erhalten. Viel Spass damit :D");
			WarnHelper.addWarn(e.getPlayer().getName(), Bukkit.getConsoleSender(), "Netherdach-Buggen (Autodetetcion)", (byte)0);
			if(MTC.instance().spawn == null){
				e.getPlayer().sendMessage("§cBitte warte, bis MTC fertig geladen ist!");
				e.setTo(e.getFrom());
				return;
			}
			e.getPlayer().teleport(MTC.instance().spawn);
//			e.setTo(MinoTopiaCore.instance().spawn);
			WarnHelper.checkWarnNumberAndDoStuff(e.getPlayer().getName(), Bukkit.getConsoleSender(), (byte)1);
		}
	}
}
