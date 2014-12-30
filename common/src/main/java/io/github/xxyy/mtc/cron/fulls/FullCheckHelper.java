/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.cron.fulls;

import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.fulltag.FullInfo;
import io.github.xxyy.mtc.fulltag.FullTagHelper;
import io.github.xxyy.mtc.helper.MTCHelper;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;

/**
 * Helps checking Fulls (armor or swords enchanted at the highest level of every enchant)
 * for validity, duplicates and other nasty things.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29.6.14
 */
public final class FullCheckHelper {
    private FullCheckHelper() {

    }

    public static FullCheckResult handleStack(ItemStack stack, Player plrHolder, FullCheckExecutor executor) {
        if (stack == null || stack.getType().equals(Material.AIR)) { //REFACTOR a typeID enum should do this
            return FullCheckResult.IGNORE;
        }

        int id = FullTagHelper.getFullId(stack);
        if (id < 0) {
            return FullCheckResult.IGNORE;
        }

        if (stack.getAmount() > 1) {
            return registerViolation(FullCheckResult.STACKED, null, plrHolder);
        }
        FullInfo fullInfo = FullInfo.getById(id);
        if (fullInfo.id == -10) {
            return registerViolation(FullCheckResult.UNKNOWN, fullInfo, plrHolder);
        }
        if (fullInfo.id < 0) {
            System.out.println("Error while checking Full in Cronjob: " + fullInfo.id);
            return FullCheckResult.IGNORE;
        }
        if (executor.getCheckedFullIds().contains(fullInfo.id)) {
            return registerViolation(FullCheckResult.DUPED, fullInfo, plrHolder);
        }

        updateLastSeen(fullInfo, "cron-check", plrHolder, plrHolder.getLocation());

        executor.getCheckedFullIds().add(fullInfo.id);
        LogHelper.getFullLogger().fine(MessageFormat.format("[CJ]Caught Full: {0} at player: {1}(UUID={2})", fullInfo.toLogString(), plrHolder.getName(), plrHolder.getUniqueId()));

        return FullCheckResult.IGNORE;
    }

    private static FullCheckResult registerViolation(FullCheckResult result, FullInfo fullInfo, Player plrHolder) {
        Validate.isTrue(result != FullCheckResult.IGNORE);
        Validate.notNull(result);

        Location lastLocation = plrHolder.getLocation();

        MTCHelper.addViolation("FULL-" + result.name(), plrHolder.getName(), "CJ|" + MTCHelper.locToShortString(lastLocation));
        LogHelper.getFullLogger().warning(MessageFormat.format("[CJ]Caught VIOLATION: FULL-{0} @ {1}(UUID={2})", result.name(), plrHolder.getName(), plrHolder.getUniqueId()));
        plrHolder.sendMessage(result.getRemovalMessage());

        updateLastSeen(fullInfo, "cronjob_"+result.name(), plrHolder, lastLocation);

        return result;
    }

    private static void updateLastSeen(FullInfo fullInfo, String checkDescription, Player plrHolder, Location lastLocation) {
        if(fullInfo == null) {
            return;
        }

        Validate.notNull(plrHolder);
        Validate.notNull(lastLocation);

        fullInfo.lastseen = (Calendar.getInstance().getTimeInMillis() / 1000);
        fullInfo.lastCode = checkDescription;
        fullInfo.inEnderchest = false;
        fullInfo.x = lastLocation.getBlockX();
        fullInfo.y = lastLocation.getBlockY();
        fullInfo.z = lastLocation.getBlockZ();
        fullInfo.lastOwnerName = plrHolder.getName();
        Bukkit.getScheduler().runTaskAsynchronously(MTC.instance(), fullInfo::flush);
    }

    public interface FullCheckExecutor {
        /**
         * @return IDs of all already checked Fulls.
         */
        Collection<Integer> getCheckedFullIds();
    }

    public enum FullCheckResult {
        STACKED("gestackt"),
        DUPED("doppelt"),
        UNKNOWN("unbekannt"),
        IGNORE(null);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        private final String verb;

        private FullCheckResult(String verb) {
            this.verb = verb;
        }

        public boolean doRemove() {
            return verb != null;
        }

        public String getRemovalMessage() {
            return MTC.chatPrefix + "§cEin Fullteil in deinem Inventar ist " + verb + " und wurde daher entfernt." +
                    " §eFür Beschwerden notiere dir bitte unbedingt das aktuelle Datum und melde dich frühstmöglich!";
        }
    }
}
