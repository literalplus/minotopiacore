/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.fulltag.model;

import com.google.common.collect.ImmutableList;
import li.l1t.common.lib.com.mojang.api.profiles.HttpProfileRepository;
import li.l1t.common.lib.com.mojang.api.profiles.NameData;
import li.l1t.common.sql.QueryResult;
import li.l1t.common.sql.SpigotSql;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.fulltag.FullTagModule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Repository class for {@link LegacyFullData}, retrieving data from an underlying legacy MySQL
 * database with a legacy data schema.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/08/15
 */
public class LegacyFullDataRepository {

    public static final String TABLE_NAME = "ni176987_1_DB.mtc_fulls_old";

    private final SpigotSql sql;
    private final FullTagModule module;
    private final HttpProfileRepository profileRepository;

    public LegacyFullDataRepository(FullTagModule module, HttpProfileRepository profileRepository) {
        this.module = module;
        this.sql = module.getPlugin().getSql();
        this.profileRepository = profileRepository;
    }

    /**
     * Deletes a legacy full data item from the underlying legacy database.
     *
     * @param legacyFullData the data to delete
     */
    public void delete(LegacyFullData legacyFullData) {
        sql.asyncUpdate("DELETE FROM " + TABLE_NAME + " WHERE id=?", legacyFullData.getId());
    }

    /**
     * Locates legacy data for a given <b>current</b> unique id. This method will query Mojang for
     * that UUID's name history and attempt to reconstruct previously owner fulls using creation
     * dates.
     *
     * @param receiverName the current name of the receiver
     * @param receiverId   the unique id of the receiver
     * @return a list of legacy full data items which belong to that user
     */
    public List<LegacyFullData> findByCurrentUniqueId(String receiverName, UUID receiverId) {
        NameData[] nameHistory = profileRepository.findNameHistory(receiverId);

        if (nameHistory.length == 0) {
            XLoginHook.Profile nameProfile = module.getPlugin().getXLoginHook().getBestProfile(receiverName);
            XLoginHook.Profile uuidProfile = module.getPlugin().getXLoginHook().getProfile(receiverId);
            //Only allow cracked users if they are the only profile with that name and they are actually cracked
            if (receiverId.equals(nameProfile.getUniqueId()) && !uuidProfile.isPremium()) {
                return findByReceiver(receiverName, receiverId);
            }
            return null;
        } else if (nameHistory.length == 1) {
            return findByReceiver(receiverName, receiverId);
        } else {
            List<LegacyFullData> result = new ArrayList<>();
            NameData current = nameHistory[0];
            NameData next;
            int i = 1;
            do {
                if (i < nameHistory.length) {
                    next = nameHistory[i];
                } else {
                    next = null;
                }
                result.addAll(findByReceiver(current.getName(),
                        current.getChangedToAt(),
                        (next == null ? new Date().getTime() : next.getChangedToAt()),
                        receiverId));
                i++;
            } while ((current = next) != null);
            return result;
        }
    }

    /**
     * Attempts to find a list of {@link LegacyFullData} instances by a receiver player's unique
     * id.
     *
     * @param receiverName the name to look for
     * @return an immutable list containing the found data, if any
     * @throws IllegalStateException if a database error occurs
     */
    public List<LegacyFullData> findByReceiver(String receiverName, UUID currentId) {
        return findByReceiver(receiverName, 0, new Date().getTime(), currentId);
    }

    /**
     * Attempts to find a list of {@link LegacyFullData} instances by a receiver player's unique
     * id.
     *
     * @param receiverName the name to look for
     * @param after        only data created after this unix timestamp (in ms) is returned
     * @param before       only data created before this unix timestamp (in ms) is returned
     * @return an immutable list containing the found data, if any
     * @throws IllegalStateException if a database error occurs
     */
    public List<LegacyFullData> findByReceiver(String receiverName, long before, long after, UUID currentId)
            throws IllegalStateException {
        before = before / 1000; //Java why
        after = after / 1000;
        return findByWhere("receiver_name=? AND `timestamp` > ? AND `timestamp` < ?",
                String.format("by receiver %s at %d < t < %d", receiverName, before, after),
                currentId,
                receiverName, before, after);
    }

    private List<LegacyFullData> findByWhere(String whereClause, String desc, UUID currentId, Object... args)
            throws IllegalStateException {
        try (QueryResult qr = sql.executeQueryWithResult("SELECT id,sender_name,receiver_name,comment,`timestamp`," +
                "thorns,part FROM " + TABLE_NAME + " WHERE " + whereClause, args)) {
            ResultSet rs = qr.rs();
            if (!rs.next()) {
                return ImmutableList.of();
            }

            List<LegacyFullData> result = new ArrayList<>();
            do {
                result.add(new LegacyFullData(this, rs.getInt("id"), rs.getString("sender_name"),
                        rs.getString("receiver_name"), currentId, rs.getString("comment"), rs.getLong("timestamp"),
                        rs.getBoolean("thorns"), rs.getInt("part")));
            } while (rs.next());

            return Collections.unmodifiableList(result);
        } catch (SQLException e) {
            throw new IllegalStateException(String.format(
                    "Could not retrieve LegacyFullData %s because of a database error: %d: %s",
                    desc, e.getErrorCode(), e.getMessage()));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(String.format(
                    "Invalid full part id in database for FullData %s",
                    desc));
        }
    }

    public FullTagModule getModule() {
        return module;
    }

    public HttpProfileRepository getProfileRepository() {
        return profileRepository;
    }
}
