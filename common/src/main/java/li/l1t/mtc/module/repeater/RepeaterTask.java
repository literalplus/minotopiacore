/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
