package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.minotopiacore.MTC;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class EnderPearlProjectileLaunchListener implements Listener {

    @EventHandler
    public void onProjectileLaunchEP(ProjectileLaunchEvent e) {
//		System.out.println("CAUGHT PEARL!");
        if (e.getEntity().getType() != EntityType.ENDER_PEARL) {
            return;
        }
        LivingEntity le = ((EnderPearl) e.getEntity()).getShooter();
        if (le.getType() != EntityType.PLAYER) {
            return;
        }
        Player plr = (Player) le;
        if (!plr.hasPermission("mtc.enderpearl.use")) {
            e.setCancelled(true);
            plr.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epcancelled", plr.getName(), MTC.instance().getName()));
            EnderPearlProjectileLaunchListener.returnPearl(plr);
            return;
        }
        @SuppressWarnings("deprecation")
        List<Block> LoS = plr.getLineOfSight(null, 100); //Bukkit is ruining all the fun :(
        for (short i = 0; i < LoS.size(); i++) {
            if (LoS.get(i).getType() == Material.BEDROCK) {
                e.setCancelled(true);
                plr.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epbedrock", plr.getName(), MTC.instance().getName()));
                EnderPearlProjectileLaunchListener.returnPearl(plr);
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
