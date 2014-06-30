package io.github.xxyy.minotopiacore.games.teambattle.event;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.games.teambattle.TeamBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class DmgListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onDamageByE(final EntityDamageByEntityEvent evt) {
        if (!(evt.getEntity().getType() == EntityType.PLAYER)) {
            return;
        }
        if (!(evt.getDamager().getType() == EntityType.PLAYER)) {
            return;
        }
        if (!TeamBattle.instance().isPlayerInGame((Player) evt.getEntity())) {
            return;
        }
        if (!TeamBattle.instance().isPlayerInGame((Player) evt.getDamager())) {
            return;
        }
        Player plr = (Player) evt.getEntity();
        double nextHealth = plr.getHealth() - evt.getDamage();
        if (nextHealth >= 1) {
            return;
        }

        evt.setCancelled(true);
        plr.setHealth(plr.getMaxHealth());
        plr.setFoodLevel(20);
        plr.setSaturation(20.0F);
        plr.setFireTicks(0);
        CommandHelper.clearInv(plr);
        Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableRespawn(plr), 1);
        TeamBattle.instance().addTeamPoint(TeamBattle.instance().invertTeam(TeamBattle.instance().getPlayerTeam(plr)));
        TeamBattle.instance().notifyPlayersKill(plr, (Player) evt.getDamager());
    }
}
