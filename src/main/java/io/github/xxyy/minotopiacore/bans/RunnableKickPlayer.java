package io.github.xxyy.minotopiacore.bans;

import org.bukkit.entity.Player;

@Deprecated
public class RunnableKickPlayer implements Runnable{
	Player plr;
	String kickMsg = "§eDu wurdest von Marcel Davis gekickt.";
	public RunnableKickPlayer(Player plr, String kickMsg){ this.plr=plr; this.kickMsg=kickMsg; }
	@Override
	public void run() {
		plr.kickPlayer(kickMsg);
	}
	
}
