package io.github.xxyy.minotopiacore.games.teambattle.event;

import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class RespawnListener implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (!TeamBattle.instance().isPlayerInGame(e.getPlayer())) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableRespawn(e.getPlayer()), 3);
    }
}
