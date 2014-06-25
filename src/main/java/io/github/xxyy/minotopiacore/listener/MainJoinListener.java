package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.clan.InvitationInfo;
import io.github.xxyy.minotopiacore.helper.LaterMessageHelper;
import io.github.xxyy.minotopiacore.helper.StatsHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;

public class MainJoinListener implements Listener {
    //TODO merge in BanJoinListener

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player plr = e.getPlayer();
        String plrName = plr.getName();
        if (ConfigHelper.isEnableTablist()) {
            char colChar = 'f';
            for (byte i = 0; i < ConfigHelper.getTabListAllowedColors().length(); i++) {
                char chr = ConfigHelper.getTabListAllowedColors().charAt(i);
                if (plr.hasPermission("mtc.tablist.color." + chr)) {
                    plr.sendMessage("Your color: §" + chr + "&" + chr);
                    colChar = chr;
                    break;
                }
            }
            if (plrName.length() <= 14) {
                plr.setPlayerListName("§" + colChar + plrName);
            } else {
                plr.setPlayerListName("§" + colChar + plrName.substring(0, 14));
            }
        }
        if (MTC.SpeedOnJoinPotency > 0) {
            plr.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60000, MTC.SpeedOnJoinPotency, false), true);
        }
        if ((ConfigHelper.isEnableItemOnJoin() && ConfigHelper.getItemOnJoin() != null)
                && !plr.getInventory().containsAtLeast(ConfigHelper.getItemOnJoin(), 1)) {
            plr.getInventory().addItem(ConfigHelper.getItemOnJoin());
        }

        //later messages
        if (LaterMessageHelper.hasMessages(plrName)) {
            LaterMessageHelper.sendMessages(plr);
        }

        //clan invitations
        if (ConfigHelper.isClanEnabled()) {
            final String str = InvitationInfo.getInvitationString(plrName, false);
            if (str != null) {
                plr.sendMessage(str);
            }
        }
        //stats
        if (StatsHelper.hasStats(plrName)) {
            StatsHelper.fetchStats(plrName);
        } else {
            StatsHelper.createEntry(plrName);
        }
        if (ConfigHelper.isEnableScB()) {
            plr.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        }
        //playerhide
        if (MTC.instance().getConfig().getBoolean("enable.playerhide", false)) {
            final Iterator<String> iterator = PlayerHideInteractListener.affectedPlayerNames.iterator();
            while (iterator.hasNext()) {
                final String targetName = iterator.next();
                final Player targetPlr = Bukkit.getPlayerExact(targetName);
                if (targetPlr == null) {
                    iterator.remove();
                    continue;
                }
                plr.hidePlayer(targetPlr);
            }
        }
    }
}
