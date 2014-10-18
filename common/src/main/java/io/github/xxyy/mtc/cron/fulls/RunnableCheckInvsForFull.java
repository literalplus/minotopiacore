package io.github.xxyy.mtc.cron.fulls;

import org.apache.commons.lang3.time.StopWatch;
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
    private final StopWatch watch = new StopWatch();
    private final List<Integer> checkedFullIds = new ArrayList<>();
    private final Iterator<? extends Player> playerIterator;

    public RunnableCheckInvsForFull(Iterator<? extends Player> playerIterator) {
        this.playerIterator = playerIterator;
    }

    @Override
    public void run() {
        CommandHelper.broadcast(MTC.chatPrefix + "Durchsuche alle Spieler nach bösen Fulls...", "mtc.saveallmsg");
        LogHelper.getFullLogger().warning("******CRONJOB******");

        if(this.watch.isSuspended()) {
            this.watch.resume();
        } else {
            this.watch.start();
        }

        int i = 0;
        while (playerIterator.hasNext() && i <= CHECKS_PER_JOB) {
            Player plr = playerIterator.next();
            for (ItemStack stack : plr.getInventory().getContents()) { //comparing contains(Material) is *NOT* more efficient since it also just loops through
                if(FullCheckHelper.handleStack(stack, plr, this).doRemove()) {
                    plr.getInventory().remove(stack);
                }
            }
            LogHelper.getFullLogger().fine("[CJ]Finished player: " + plr.getName());
        }

        if (playerIterator.hasNext()) {
            this.watch.suspend();
            Bukkit.getScheduler().runTaskLater(MTC.instance(), this, 20);
            return;
        }

        this.watch.stop();
        LogHelper.getFullLogger().warning("***CRONJOB COMPLETED (" + this.watch.getTime() + "ms)***");
        CommandHelper.broadcast(MTC.chatPrefix + "Inventur fertig! (" + this.watch.getTime() + "ms)", "mtc.saveallmsg");
    }

    @Override
    public List<Integer> getCheckedFullIds() {
        return checkedFullIds;
    }
}
