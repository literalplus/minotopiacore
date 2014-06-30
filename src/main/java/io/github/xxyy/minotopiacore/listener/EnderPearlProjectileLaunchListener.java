package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.minotopiacore.MTC;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public final class EnderPearlProjectileLaunchListener implements Listener {

    @EventHandler
    public void onProjectileLaunchEP(ProjectileLaunchEvent e) {
//		System.out.println("CAUGHT PEARL!");
        if (e.getEntity().getType() != EntityType.ENDER_PEARL) {
            return;
        }
        ProjectileSource projectileSource = e.getEntity().getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }

        Player shooter = (Player) projectileSource;

        if (!shooter.hasPermission("mtc.enderpearl.use")) {
            e.setCancelled(true);
            shooter.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epcancelled", shooter.getName(), MTC.instance().getName()));
            EnderPearlProjectileLaunchListener.returnPearl(shooter);
            return;
        }
        @SuppressWarnings("deprecation")
        List<Block> lineOfSight = shooter.getLineOfSight(null, 100); //Bukkit is ruining all the fun :(
        for (Block lineOfSightItem : lineOfSight) {
            if (lineOfSightItem.getType() == Material.BEDROCK) {
                e.setCancelled(true);
                shooter.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epbedrock", shooter.getName(), MTC.instance().getName()));
                EnderPearlProjectileLaunchListener.returnPearl(shooter);
                return;
            }
        }
    }

    private static void returnPearl(Player plr) {
        if (plr.getItemInHand().getType() != Material.ENDER_PEARL) {
            return;
        }
        ItemStack is = plr.getItemInHand();
        is.setAmount(plr.getItemInHand().getAmount() + 1);
        plr.setItemInHand(is);
    }
}
