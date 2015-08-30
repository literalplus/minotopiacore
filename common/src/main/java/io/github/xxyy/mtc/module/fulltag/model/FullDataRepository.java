/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;
import io.github.xxyy.common.sql.QueryResult;
import io.github.xxyy.common.sql.SpigotSql;
import io.github.xxyy.common.sql.UpdateResult;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.CacheHelper;
import io.github.xxyy.mtc.module.fulltag.FullTagModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Repository class for {@link FullData}, retrieving data from an underlying MySQL database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/08/15
 */
public class FullDataRepository implements CacheHelper.Cache {
    public static final String TABLE_NAME = "mt_main.fulldata";

    private final SpigotSql sql;
    @Nonnull
    private final FullTagModule module;
    @Nonnull
    private Cache<Integer, FullData> dataCache = CacheBuilder.newBuilder()
            .expireAfterAccess(40, TimeUnit.MINUTES)
            .removalListener((RemovalNotification<Integer, FullData> notification) -> {
                if (notification.getValue() != null) {
                    updateComment(notification.getValue());
                    notification.getValue().setValid(false);
                }
            })
            .build();

    public FullDataRepository(@Nonnull FullTagModule module) {
        this.module = module;
        this.sql = module.getPlugin().getSql();
        CacheHelper.registerCache(this);
    }

    /**
     * Attempts to find a {@link FullData} instance from the underlying database by its id, or returns a cached instance,
     * if available.
     *
     * @param id the id to look for
     * @return a {@link FullData} instance or null if there is no such instance
     * @throws IllegalStateException if the data in the database is invalid or could not be retrieved
     */
    @Nullable
    public FullData getById(int id) throws IllegalStateException {
        FullData data = dataCache.getIfPresent(id);
        if (data == null) {
            data = findById(id);
            if (data != null) {
                dataCache.put(id, data);
            }
        }
        return data;
    }

    /**
     * Attempts to find a {@link FullData} instance from the underlying database by its id.
     *
     * @param id the id to look for
     * @return a {@link FullData} instance or null if there is no such instance
     * @throws IllegalStateException if the data in the database is invalid or could not be retrieved
     */
    private FullData findById(int id) throws IllegalStateException {
        return findByWhere("id=?", "by id " + id, id).stream()
                .findFirst().orElse(null);
    }

    /**
     * Attempts to find a list of {@link FullData} instances by a receiver player's unique id.
     *
     * @param receiverId the unique id to look for
     * @return an immutable list containing the found data, if any
     * @throws IllegalStateException if a database error occurs
     */
    public List<FullData> findByReceiver(@Nonnull UUID receiverId) throws IllegalStateException {
        return findByWhere("receiver_id=?", "by receiver " + receiverId, receiverId.toString());
    }

    private List<FullData> findByWhere(String whereClause, String desc, Object... args) throws IllegalStateException {
        try (QueryResult qr = sql.executeQueryWithResult("SELECT * FROM " + TABLE_NAME + " WHERE " + whereClause, args)) {
            ResultSet rs = qr.rs();
            if (!rs.next()) {
                return ImmutableList.of();
            }

            List<FullData> result = new ArrayList<>();
            do {
                result.add(new FullData(rs.getInt(1), rs.getTimestamp(2).toLocalDateTime(),
                        rs.getString(3), UUID.fromString(rs.getString(4)), UUID.fromString(rs.getString(5)),
                        FullPart.values()[rs.getInt(6)], rs.getBoolean(7)));
            } while (rs.next());

            return Collections.unmodifiableList(result);
        } catch (SQLException e) {
            throw new IllegalStateException(String.format(
                    "Could not retrieve FullData %s because of a database error: %d: %s",
                    desc, e.getErrorCode(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(String.format(
                    "Invalid UUID in database for FullData %s:%s",
                    desc, e.getMessage()));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(String.format(
                    "Invalid full part id in database for FullData %s",
                    desc));
        }
    }

    /**
     * Creates a new full data and inserts it into the database.
     *
     * @param comment    the comment string to attach
     * @param senderId   the unique id of the player who created the full item
     * @param receiverId the unique id of the player who owns the full item
     * @param part       the full part the item represents
     * @param thorns     whether the full item has the Thorns enchantment
     * @return the created full data
     * @throws IllegalStateException if a database error occurs
     */
    @Nonnull
    public FullData create(String comment, @Nonnull UUID senderId, @Nonnull UUID receiverId, @Nonnull FullPart part, boolean thorns) {
        try (UpdateResult ur = sql.executeUpdateWithGenKeys(
                "INSERT INTO " + TABLE_NAME + " SET comment=?,sender_id=?,receiver_id=?,part_id=?,thorns=?",
                comment, senderId.toString(), receiverId.toString(), part.ordinal(), thorns
        )) {
            ur.vouchForGeneratedKeys();
            ur.gk().next();
            return new FullData(
                    ur.gk().getInt(1), LocalDateTime.now(),
                    comment, senderId, receiverId, part, thorns
            );
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Updates the comment of a full item's metadata in the database if it has been modified locally.
     *
     * @param data the target data object
     */
    private void updateComment(@Nonnull FullData data) {
        if (data.isModified()) {
            data.resetModified();
            if (module.getPlugin().isEnabled()) {
                sql.executeSimpleUpdateAsync("UPDATE " + TABLE_NAME + " SET comment=? WHERE id=?",
                        data.getComment(), data.getId());
            } else { //When we're disabling, we can't register tasks. Sad, but true.
                sql.safelyExecuteUpdate("UPDATE " + TABLE_NAME + " SET comment=? WHERE id=?",
                        data.getComment(), data.getId());
            }
        }
    }

    /**
     * Flushes this repository's cache, writing all pending changes to database and removing expired entries.
     */
    @Override
    public void clearCache(boolean forced, MTC plugin) {
        dataCache.cleanUp();
        dataCache.asMap().values().forEach(this::updateComment);
    }
}
