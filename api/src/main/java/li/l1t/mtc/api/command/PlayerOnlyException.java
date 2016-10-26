/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.command;

import com.google.common.base.Preconditions;
import li.l1t.common.exception.UserException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Thrown if a command may only be executed by players but the sender is not a player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
public class PlayerOnlyException extends UserException {
    public PlayerOnlyException(String messagePattern, Object... params) {
        super(messagePattern, params);
    }

    public static void checkIsPlayer(CommandSender target, String details, Object... params) {
        Preconditions.checkNotNull(target, "target");
        if (!(target instanceof Player)) {
            throw new PlayerOnlyException("Dieser Befehl kann nur von Spielern verwendet werden. " + details, params);
        }
    }
}
