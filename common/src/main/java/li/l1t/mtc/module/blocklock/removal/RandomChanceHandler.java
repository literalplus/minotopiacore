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
import org.bukkit.entity.Player;

/**
 * Basically rolls a dice to find out whether a lock may be removed.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-08
 */
public class RandomChanceHandler implements RemovalHandler {
    private final int percentChance;

    public RandomChanceHandler(int percentChance) {
        if (percentChance < 0 || percentChance > 100) {
            throw new HandlerConfigException("RandomChanceHandler chance must be between 0 and 100");
        }
        this.percentChance = percentChance;
    }

    @Override
    public boolean onRemove(BlockLock lock, Player player) {
        if (RandomUtils.nextInt(0, 100) > percentChance) {
            MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Du hast Glück gehabt!");
            return true;
        } else {
            MessageType.RESULT_LINE.sendTo(player, "Heute habe ich leider keinen Block für dich =( " +
                    "Versuche es bei der nächsten Staffel erneut.");
            return false;
        }
    }
}
