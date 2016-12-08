/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.api;

import li.l1t.common.exception.UserException;

import java.util.UUID;

/**
 * Thrown if there is no protection for a player, but a protection was expected.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class NoSuchProtectionException extends UserException {
    private final UUID playerId;

    public NoSuchProtectionException(UUID playerId) {
        super("Dieser Spieler hat keinen N.u.b.-Schutz: %s", playerId.toString());
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
