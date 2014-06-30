package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.MTC;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class MoveNetherRoofListener implements Listener {
    private final MTC plugin;

    public MoveNetherRoofListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNetherMove(PlayerMoveEvent e) {
        if (!e.getTo().getWorld().getEnvironment().equals(Environment.NETHER)) {
            return;
        }
        if (e.getTo().getBlockY() > 125) {
            if (e.getPlayer().hasPermission("mtc.ignore")) {
                return;
            }
            e.getPlayer().sendMessage("§c[MTC] §eDu darfst nicht über das Netherdach :)");
            e.getPlayer().sendMessage("§c[MTC] §eViel Spaß beim Spawn.");
            e.getPlayer().sendMessage("§c[MTC] §eDeine Koordinaten: §c" + e.getPlayer().getLocation().toString());
            e.getPlayer().sendMessage("§c[MTC] §eWenn du mit dieser automatisierten Entscheidung unzufrieden bist, wende dich " +
                    "bitte mit Screenshot dieser Nachricht an das Team! (Forum!)");

            if (plugin.getXLoginHook().getSpawnLocation() == null) {
                e.setTo(e.getFrom());
            } else {
                e.getPlayer().teleport(plugin.getXLoginHook().getSpawnLocation());
            }
        }
    }
}
