/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.api;

import li.l1t.common.exception.DatabaseException;

import java.util.Optional;
import java.util.UUID;

/**
 * Provides access to N.u.b. protection data stored in a data source. Does not provide any means of caching.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public interface ProtectionRepository {
    /**
     * @param playerId the unique id of the player to find protection data for
     *
     * @return an optional containing given player's protection data from the data source, or an empty optional if there
     * is no data for given player
     *
     * @throws DatabaseException if a database error occurs
     */
    Optional<NubProtection> findProtectionFor(UUID playerId) throws DatabaseException;

    /**
     * Removes given protection data from the data source permanently. Does nothing if there is no such protection.
     *
     * @param protection the protection data to permanently remove from the data source
     *
     * @throws DatabaseException if a database error occurs
     */
    void deleteProtection(NubProtection protection) throws DatabaseException;

    /**
     * @param protection the protection to write to the data source, ignoring any remote changes
     *
     * @throws NoSuchProtectionException if there is no such protection
     * @throws DatabaseException         if a database error occurs
     */
    void saveProtection(NubProtection protection) throws DatabaseException, NoSuchProtectionException;

    /**
     * Creates a protection in the data source for given player with given duration. If there is already a protection
     * for that player, updates that protection's duration to given duration.
     *
     * @param playerId        the unique id of the player to create a protection for
     * @param durationMinutes the duration of the protections from now, in minutes
     *
     * @return the created protection
     *
     * @throws DatabaseException if a database error occurs
     */
    NubProtection createProtection(UUID playerId, int durationMinutes) throws DatabaseException;
}
