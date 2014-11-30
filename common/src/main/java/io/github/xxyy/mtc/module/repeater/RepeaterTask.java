/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.repeater;

import org.bukkit.Bukkit;

import io.github.xxyy.common.util.CommandHelper;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A task for repeating messages.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
public class RepeaterTask implements Runnable {
    private final Queue<RepeatingMessage> queuedMessages = new LinkedList<>();
    private int currentTick = 0; //ticks, in this context every five seconds
    private final RepeaterModule module;

    public RepeaterTask(RepeaterModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        currentTick++;

        module.getMessages().stream()
                .filter(m -> (m.getTickInterval() % currentTick) == 0)
                .forEach(queuedMessages::add);

        RepeatingMessage toBroadcast = queuedMessages.poll();

        if(toBroadcast != null) {
            String globalMessage = toBroadcast.getMessage();

            Bukkit.getOnlinePlayers()
                    .forEach(p -> CommandHelper.msg(globalMessage.replace("{player}", p.getName()).replace("\\n","\n"), p));
        }
    }
}
