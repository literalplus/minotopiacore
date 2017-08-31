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
