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

package li.l1t.mtc.module.pvpstats.model;

import com.google.common.collect.ImmutableList;
import li.l1t.common.shared.uuid.UUIDRepository;
import li.l1t.common.sql.QueryResult;
import li.l1t.common.sql.SpigotSql;
import li.l1t.common.util.UUIDHelper;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.pvpstats.PlayerStatsModule;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implements the connection between the PvP Stats application model and the database storing raw
 * data.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class PlayerStatsRepositoryImpl implements PlayerStatsRepository {
    public static final int TOP_ROW_LIMIT = 100;
    private final SpigotSql sql;
    private final XLoginHook xLoginHook;
    private String databaseTable = "`mt_pvp`.`pvpstats`";

    public PlayerStatsRepositoryImpl(PlayerStatsModule module) {
        this(module.getPlugin().getSql(), module.getPlugin().getXLoginHook());
    }

    protected PlayerStatsRepositoryImpl(SpigotSql sql, XLoginHook xLoginHook) {
        this.sql = sql;
        this.xLoginHook = xLoginHook;
    }

    @Override
    public PlayerStats findByUniqueId(UUID uuid) throws IllegalStateException {
        return findByUniqueId(uuid, null);
    }

    @Override
    public PlayerStats find(OfflinePlayer plr) throws IllegalStateException {
        return findByUniqueId(plr.getUniqueId(), plr.getName());
    }

    @Override
    public PlayerStats findByName(String name) throws IllegalStateException, UUIDRepository.UnknownKeyException {
        XLoginHook.Profile profile = xLoginHook.getBestProfile(name);
        if (profile == null) {
            throw new UUIDRepository.UnknownKeyException();
        }

        return findByUniqueId(profile.getUniqueId(), profile.getName());
    }

    @Override
    public PlayerStats findByUniqueId(UUID uuid, @Nullable String plrName) throws IllegalStateException {
        if (plrName == null) {
            plrName = xLoginHook.getDisplayString(uuid);
        }

        try (QueryResult qr = sql.executeQueryWithResult(
                "SELECT kills,deaths FROM " + databaseTable + " WHERE uuid=?",
                uuid.toString()
        ).vouchForResultSet()) {
            if (!qr.rs().next()) {
                return new PlayerStats(uuid, plrName, 0, 0);
            }

            return new PlayerStats(uuid, plrName, qr.rs().getInt("kills"), qr.rs().getInt("deaths"));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompletableFuture<List<PlayerStats>> findTopKillers(int limit) {
        return findTop("kills", limit);
    }

    @Override
    public CompletableFuture<List<PlayerStats>> findWhoDiedMost(int limit) {
        return findTop("deaths", limit);
    }

    @Override
    public int getKillsRank(PlayerStats playerStats) {
        return getRank(playerStats, "kills");
    }

    @Override
    public int getDeathsRank(PlayerStats playerStats) {
        return getRank(playerStats, "deaths");
    }

    private int getRank(PlayerStats playerStats, String whatToRank) {
        try (QueryResult qr = sql.executeQueryWithResult(
                "SELECT COUNT(" + whatToRank + ") + 1 FROM " + databaseTable + " WHERE (" + whatToRank + " > ?)",
                playerStats.getKills()
        ).vouchForResultSet()) {
            if (!qr.rs().next()) {
                throw new IllegalStateException("Couldn't rs.next()?!");
            }
            return qr.rs().getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(PlayerStats playerStats) {
        sql.asyncUpdate("INSERT INTO " + databaseTable + " SET uuid=?,kills=?,deaths=? " +
                        "ON DUPLICATE KEY UPDATE kills=?,deaths=?",
                playerStats.getUniqueId(), playerStats.getKills(), playerStats.getDeaths(),
                playerStats.getKills(), playerStats.getDeaths());
        playerStats.setDirty(false);
    }

    private CompletableFuture<List<PlayerStats>> findTop(String column, int limit) {
        if (limit > TOP_ROW_LIMIT) {
            limit = TOP_ROW_LIMIT;
        }

        CompletableFuture<List<PlayerStats>> future = new CompletableFuture<>();

        sql.executeQueryAsync(
                String.format("SELECT uuid, kills, deaths FROM %s ORDER BY `%s` DESC LIMIT %d",
                        databaseTable, column, limit),
                qr1 -> {
                    try (QueryResult qr = qr1.vouchForResultSet()) {
                        future.complete(toPlayerStatsList(qr.rs()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        future.completeExceptionally(e);
                    }
                });

        return future;
    }

    private List<PlayerStats> toPlayerStatsList(ResultSet rs) throws SQLException {
        ImmutableList.Builder<PlayerStats> builder = ImmutableList.builder();

        while (rs.next()) {
            String uuidString = rs.getString("uuid");
            UUID uuid = UUIDHelper.getFromString(uuidString);
            if (uuid == null) {
                throw new SQLException("Invalid UUID in database: " + uuidString);
            }

            builder.add(new PlayerStats(
                    uuid,
                    xLoginHook.getDisplayString(uuid),
                    rs.getInt("kills"),
                    rs.getInt("deaths")
            ));
        }

        return builder.build();
    }

    @Override
    public void setDatabaseTable(String database, String table) {
        databaseTable = "`" + database + "`.`" + table + "`";
    }

    @Override
    public String getDatabaseTable() {
        return databaseTable;
    }

    @Override
    public void cleanup() {

    }

    public XLoginHook getXLoginHook() {
        return xLoginHook;
    }

    @Override
    public SpigotSql getSql() {
        return sql;
    }
}
