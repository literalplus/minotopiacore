package io.github.xxyy.minotopiacore.bans.listener;

import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.bans.BanHelper;
import io.github.xxyy.minotopiacore.bans.BanInfo;

import java.util.Calendar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;


public class BanJoinListener implements Listener {
	@EventHandler(priority=EventPriority.LOW)
	public void onJoin(PlayerLoginEvent e){
		BanInfo bi = BanHelper.getBanInfoByPlayerName(e.getPlayer().getName().toLowerCase());
		if(bi.id < 0){ return; }
		if(bi.banExpiryTimestamp > 0 && Calendar.getInstance().getTimeInMillis() >= (bi.banExpiryTimestamp*1000)){
			BanHelper.deleteBan(e.getPlayer().getName().toLowerCase());
			e.getPlayer().sendMessage(MTC.banChatPrefix+"Dein temporärer Ban ist abgelaufen!");
			return;
		}
		e.disallow(Result.KICK_OTHER, BanHelper.getBanReasonForKick(bi, false));
	}
}
