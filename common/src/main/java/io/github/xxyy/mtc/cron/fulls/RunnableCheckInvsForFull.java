/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.cron.fulls;

import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.MTC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public final class RunnableCheckInvsForFull implements Runnable, FullCheckHelper.FullCheckExecutor {
    public static final int CHECKS_PER_JOB = 35;
    private final MTC plugin;
    private final StopWatch watch = new StopWatch();
    private final List<Integer> checkedFullIds = new ArrayList<>();
    private final Iterator<? extends Player> playerIterator;
    private boolean watchSuspended = false; //TODO: create facade and track there, in xyc

    public RunnableCheckInvsForFull(MTC plugin, Iterator<? extends Player> playerIterator) {
        this.plugin = plugin;
        this.playerIterator = playerIterator;
    }

    @Override
    public void run() {
        CommandHelper.broadcast(MTC.chatPrefix + "Durchsuche alle Spieler nach bösen Fulls...", "mtc.saveallmsg");
        LogHelper.getFullLogger().warning("******CRONJOB******");

        if (watchSuspended) {
            this.watch.resume();
        } else {
            this.watch.start();
        }
        watchSuspended = false;

        int i = 0;
        while (playerIterator.hasNext() && i <= CHECKS_PER_JOB) {
            Player plr = playerIterator.next();
            for (ItemStack stack : plr.getInventory().getContents()) { //comparing contains(Material) is *NOT* more efficient since it also just loops through
                if (FullCheckHelper.handleStack(stack, plr, this).doRemove()) {
                    plr.getInventory().remove(stack);
                }
            }
            LogHelper.getFullLogger().fine("[CJ]Finished player: " + plr.getName());
            i++;
        }

        if (playerIterator.hasNext()) {
            this.watch.suspend();
            watchSuspended = true;
            Bukkit.getScheduler().runTaskLater(plugin, this, 20);
            return;
        }

        this.watch.stop();
        watchSuspended = false;
        LogHelper.getFullLogger().warning("***CRONJOB COMPLETED (" + this.watch.getTime() + "ms)***");
        CommandHelper.broadcast(plugin.getChatPrefix() + "Inventur fertig! (" + this.watch.getTime() + "ms)", "mtc.saveallmsg");
    }

    @Override
    public List<Integer> getCheckedFullIds() {
        return checkedFullIds;
    }
}
