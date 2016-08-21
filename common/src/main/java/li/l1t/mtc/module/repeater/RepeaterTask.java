/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.repeater;

import li.l1t.common.util.CommandHelper;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A task for repeating messages.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
class RepeaterTask implements Runnable {
    private int currentTick = 0; //ticks, in this context every five seconds
    private final RepeaterModule module;

    public RepeaterTask(RepeaterModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        currentTick++;

        List<RepeatingMessage> messagesForThisTick = module.getMessages().stream()
                .filter(m -> (currentTick % m.getTickInterval()) == 0)
                .collect(Collectors.toList());
        RepeatingMessage toBroadcast = null;
        if (!messagesForThisTick.isEmpty()) {
            toBroadcast = messagesForThisTick.get(RandomUtils.nextInt(messagesForThisTick.size()));
        }

        if (toBroadcast != null) {
            String globalMessage = toBroadcast.getMessage()
                    .replace("{p}", module.getPlugin().getChatPrefix());

            Bukkit.getOnlinePlayers()
                    .forEach(p -> CommandHelper.msg(globalMessage.replace("{player}", p.getName()).replace("\\n", "\n"), p));
        }
    }
}
