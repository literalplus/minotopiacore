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

package li.l1t.mtc.module.blocklock.removal;

import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.blocklock.api.BlockLock;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Basically rolls a dice to find out whether a lock may be removed.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-08
 */
public class RandomChanceHandler implements RemovalHandler {
    private final int dropRate;

    public RandomChanceHandler(int dropRate) {
        if (dropRate < 0 || dropRate > 100) {
            throw new HandlerConfigException("RandomChanceHandler droprate must be between 0 and 100");
        }
        this.dropRate = dropRate;
    }

    @Override
    public boolean onRemove(BlockLock lock, Player player) {
        if (RandomUtils.nextInt(0, 100) < dropRate) {
            MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Du hast Glück gehabt!");
            return true;
        } else {
            MessageType.RESULT_LINE.sendTo(player, "Heute habe ich leider keinen Block für dich =( " +
                    "Versuche es bei der nächsten Staffel erneut.");
            return false;
        }
    }

    @Override
    public void describeTo(CommandSender sender) {
        MessageType.RESULT_LINE.sendTo(sender,
                "Du erhältst den Block mit %d%%iger Wahrscheinlichkeit zurück.",
                dropRate);
    }
}
