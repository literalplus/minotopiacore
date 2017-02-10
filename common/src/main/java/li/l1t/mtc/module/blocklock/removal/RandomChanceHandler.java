/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
                "Du erhältst den Block mit einer %d%%igen Wahrscheinlichkeit zurück.",
                dropRate);
    }
}
