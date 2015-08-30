/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag.dist;

import io.github.xxyy.common.util.task.ImprovedBukkitRunnable;
import io.github.xxyy.mtc.module.fulltag.model.FullInfo;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Maintains a queue of full items to distribute and periodically attempts to empty the queue.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 10/09/15
 */
class FullDistributionQueueExecutor extends ImprovedBukkitRunnable {
    private static final long EXECUTION_DELAY_TICKS = 5 * 20L;
    private final Queue<FullQueueItem> queue = new ConcurrentLinkedDeque<>();
    private final FullDistributionManager manager;

    FullDistributionQueueExecutor(FullDistributionManager manager) {
        this.manager = manager;
        this.runTaskTimer(manager.getModule().getPlugin(), EXECUTION_DELAY_TICKS, EXECUTION_DELAY_TICKS);
    }

    public void enqueue(FullInfo info, Player receiver, CompletableFuture<FullInfo> future) {
        queue.add(new FullQueueItem(info, receiver, future));
    }

    @Override
    public void run() {
        boolean modified = false;
        for (FullQueueItem queueItem : queue) { //iterator guarantees traversal of elements as they existed at creation time
            try {
                manager.attemptStore(queueItem.getFullInfo(), queueItem.getReceiver());
                queue.remove(queueItem);
                queueItem.getFuture().complete(queueItem.getFullInfo());
            } catch (FullDistributionException e) {
                if (!queueItem.getReceiver().isOnline()) {
                    queue.remove(queueItem);
                    queueItem.getFuture().completeExceptionally(new FullDistributionException(
                            queueItem.getFullInfo().getData(),
                            "The target player is no longer online!"
                    ));
                    modified = true;
                } else {
                    queueItem.getReceiver().sendMessage(String.format(
                            "%sBitte leere dein Inventar, um ein voll verzaubertes Item vom Typ %s zu erhalten!",
                            manager.getModule().getPlugin().getChatPrefix(),
                            queueItem.getFullInfo().getData().getPart().getAlias()
                    ));
                }
            }
        }

        if (modified) {
            manager.saveStorage();
        }
    }

    public class FullQueueItem {
        private final FullInfo fullInfo;
        private final Player receiver;
        private final CompletableFuture<FullInfo> future;

        private FullQueueItem(FullInfo fullInfo, Player receiver, CompletableFuture<FullInfo> future) {
            this.fullInfo = fullInfo;
            this.receiver = receiver;
            this.future = future;
        }

        public FullInfo getFullInfo() {
            return fullInfo;
        }

        public Player getReceiver() {
            return receiver;
        }

        public CompletableFuture<FullInfo> getFuture() {
            return future;
        }
    }
}
