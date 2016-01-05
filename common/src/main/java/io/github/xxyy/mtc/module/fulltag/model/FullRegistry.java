/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag.model;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;
import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.sql.QueryResult;
import io.github.xxyy.common.sql.SpigotSql;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.logging.LogManager;
import io.github.xxyy.mtc.misc.CacheHelper;
import io.github.xxyy.mtc.module.fulltag.FullTagModule;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A registry managing fulls currently in the game.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/08/15
 */
public class FullRegistry implements io.github.xxyy.mtc.api.misc.Cache {
    public static final String TABLE_NAME = "mt_main.fullregistry";
    private static final Logger LOGGER = LogManager.getLogger(FullRegistry.class);
    @Nonnull
    private final FullTagModule module;
    private final SpigotSql sql;
    @Nonnull
    private Cache<Integer, FullInfo> fullInfoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .removalListener((RemovalNotification<Integer, FullInfo> notification) -> {
                if (notification.getValue() != null && notification.getValue().isValid()) {
                    save(notification.getValue());
                }
            })
            .build();

    public FullRegistry(@Nonnull FullTagModule module) {
        this.module = module;
        this.sql = module.getPlugin().getSql();
        CacheHelper.registerCache(this);
    }

    /**
     * Attempts to retrieve a {@link FullInfo}, either from the local cache or directly from the underlying database.
     *
     * @param id the id to look up
     * @return a matching {@link FullInfo}, or null, if there is no such item
     * @throws IllegalStateException if a database error occurs
     */
    @Nullable
    public FullInfo getById(int id) {
        FullInfo info = fullInfoCache.getIfPresent(id);
        if (info == null) {
            info = findByWhere("full_id=?", "by id " + id, id).stream().findFirst().orElse(null);
            if (info != null) {
                fullInfoCache.put(id, info);
            } else {
                fullInfoCache.invalidate(id);
            }
        }
        return info;
    }

    /**
     * Finds a list of fulls last held by a specified player from the underlying database.
     *
     * @param lastHolderId the unique id of the player to look for
     * @return a list of {@link FullInfo} containing the results
     * @throws IllegalStateException if a database error occurs
     */
    public List<FullInfo> findByLastHolder(@Nonnull UUID lastHolderId) {
        return findByWhere("lastplayer_id=?", "by last holder " + lastHolderId, lastHolderId.toString());
    }

    private List<FullInfo> findByWhere(String whereClause, String desc, Object... args) throws IllegalStateException {
        try (QueryResult qr = sql.executeQueryWithResult("SELECT * FROM " + TABLE_NAME + " WHERE " + whereClause, args)) {
            ResultSet rs = qr.rs();
            if (!rs.next()) {
                return ImmutableList.of();
            }

            List<FullInfo> result = new ArrayList<>();
            do {

                result.add(new FullInfo(module, rs.getInt(1), rs.getTimestamp(2).toLocalDateTime(),
                        rs.getString(3), XyLocation.fromResultSet(rs), UUID.fromString(rs.getString(4)),
                        rs.getBoolean(5), rs.getBoolean(10)));
            } while (rs.next());

            return Collections.unmodifiableList(result);
        } catch (SQLException e) {
            throw new IllegalStateException(String.format(
                    "Could not retrieve FullInfo %s because of a database error: %d: %s",
                    desc, e.getErrorCode(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(String.format(
                    "Invalid UUID in database for FullInfo %s:%s",
                    desc, e.getMessage()));
        }
    }

    /**
     * Flushes this registry's cache, writing all pending changes to database and removing expired entries.
     */
    public void clearCache(boolean forced, MTC plugin) {
        fullInfoCache.cleanUp();
        fullInfoCache.asMap().values().stream().forEach(this::save);
        LOGGER.debug("saving registry!");
    }

    /**
     * Write the state of given {@link FullInfo} to the underlying database, if modifed.
     *
     * @param info the info to save
     */
    public void save(@Nonnull FullInfo info) {
        if (info.isModified()) {
            LOGGER.debug("saving {}", info);
            info.setModified(false);
            String query = "UPDATE " + TABLE_NAME + " SET lastcode=?,lastplayer_id=?,in_ender=?,x=?,y=?,z=?,world=?,valid=? " +
                    "WHERE full_id=?";
            Object[] args = {
                    info.getLocationCode(), info.getHolderId().toString(), info.isInContainer(), info.getLocation().getBlockX(),
                    info.getLocation().getBlockY(), info.getLocation().getBlockZ(), info.getLocation().getWorld().getName(),
                    info.isValid() ? 1 : 0, info.getId()
            };
            if (module.getPlugin().isEnabled()) {
                sql.executeSimpleUpdateAsync(query, args);
            } else { //While being disabled, we can't register new tasks
                sql.safelyExecuteUpdate(query, args);
            }
            info.updateTimestamp();
        }
    }

    /**
     * Creates a full info from its data template and saves it to the underlying database in the caller thread.
     *
     * @param data     the data template to use
     * @param location the location the item is currently at
     * @return the created full info
     */
    @Nonnull
    public FullInfo create(@Nonnull FullData data, @Nonnull Location location) {
        Preconditions.checkNotNull(data, "data");

        int rowsAffected = sql.safelyExecuteUpdate(
                "INSERT INTO " + TABLE_NAME + " SET full_id=?,lastcode=?,lastplayer_id=?,x=?,y=?,z=?,world=?",
                data.getId(), "initialised", data.getReceiverId().toString(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()
        );

        if (rowsAffected < 1) {
            throw new IllegalStateException("illegal rows affected: " + rowsAffected);
        }

        return new FullInfo(module, data, location);
    }

    /**
     * Creates a full info from its data template and saves it to the underlying database in an async thread.
     *
     * @param data     the data template to use
     * @param location the location the item is currently at
     * @return a future that can be hooked into to execute code when the database operation is completed
     */
    public CompletableFuture<FullInfo> createAsync(@Nonnull FullData data, @Nonnull Location location) {
        Preconditions.checkNotNull(data, "data");

        return sql.executeSimpleUpdateAsync(
                "INSERT INTO " + TABLE_NAME + " SET full_id=?,lastcode=?,lastplayer_id=?,x=?,y=?,z=?,world=?",
                data.getId(), "initialised", data.getReceiverId().toString(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()
        ).thenApply(i -> new FullInfo(module, data, location));
    }
}
