package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.clan.ClanHelper;
import io.github.xxyy.minotopiacore.clan.ClanMemberInfo;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import io.github.xxyy.minotopiacore.misc.PeaceInfo;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;


public class MainDamageListener implements Listener
{
    private static final DecimalFormat df = new DecimalFormat("#.##");
    static
    {
        MainDamageListener.df.setRoundingMode(RoundingMode.HALF_UP);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    // event should be cancelled before by i.e. WorldGuard
    public void onHit(EntityDamageByEntityEvent e)
    {
        if (e.isCancelled()) return;
        Player plr;
        Player plrDamager;
        boolean message = true;
        plr = (Player) e.getEntity();
        plr.removePotionEffect(PotionEffectType.INVISIBILITY);
        switch (e.getDamager().getType()) {
        case PLAYER:
            plrDamager = (Player) e.getDamager();
            break;
        case ARROW:
        case SNOWBALL:
        case SPLASH_POTION:
            ProjectileSource source = ((Projectile) e.getDamager()).getShooter();
            if (source == null || !(source instanceof Player)) return;
            plrDamager = (Player) source;
            break;
        case WOLF:
            Wolf wolf = (Wolf) e.getDamager();
            AnimalTamer tmr = wolf.getOwner();
            if (!(tmr instanceof Player)) return;
            plrDamager = (Player) tmr;
            message = false;
            break;
        default:
            return;
        }
        // ANTILOGOUT
        Calendar cal = Calendar.getInstance();
        // INVISIBILITY
        plrDamager.removePotionEffect(PotionEffectType.INVISIBILITY);
        // CLAN
        ClanMemberInfo cmiVictim = ClanHelper.getMemberInfoByPlayerName(plr.getName());
        ClanMemberInfo cmiDamager = ClanHelper.getMemberInfoByPlayerName(plrDamager.getName());
        if (cmiVictim.clanId < 0 || cmiDamager.clanId < 0 || (cmiVictim.clanId != cmiDamager.clanId))
        {
            String clnPrefix = ClanHelper.getPrefix(plr.getName()) + ((cmiVictim.clanId < 0) ? "" : ClanHelper.getStarsByRank(cmiVictim.getRank()));
            if (PeaceInfo.isInPeaceWith(plrDamager.getName(), plr.getName()))
            {// this happens if the players are in peace
                if (message)
                {
                    MTCHelper.sendLocArgs("XU-peacehit", plrDamager, true, clnPrefix,
                            plr.getName(), MainDamageListener.df.format(plr.getHealth() / 2.0F), "❤");
                }
                MainDamageListener.cancelAndStopWolves(e); // e.setCancelled(true);
                return;
            }
            //this happens if the players can hit each other
            AntiLogoutListener.setFighting(plr, plrDamager, cal); //ANTILOGOUT
            MTCHelper.sendLocArgs("XU-hit", plrDamager, true, clnPrefix,
                    plr.getName(), MainDamageListener.df.format(plr.getHealth() / 2.0F), "❤");
            return;
        }
        //this happens if the players are in the same clan.
        MainDamageListener.cancelAndStopWolves(e); // e.setCancelled(true);
        if (message)
        {
            MTCHelper.sendLoc("XC-clanhit", plrDamager, true);
        }
    }
    
    private static void cancelAndStopWolves(EntityDamageByEntityEvent e)
    {
        e.setCancelled(true);
        if (e.getDamager().getType() == EntityType.WOLF)
        {
            Wolf wolf = (Wolf) e.getDamager();
            wolf.setCollarColor(DyeColor.ORANGE);
            AnimalTamer prevOwner = wolf.getOwner();
            wolf.setOwner((Player) e.getEntity());
            wolf.setOwner(prevOwner);
        }
    }
}
