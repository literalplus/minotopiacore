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

package li.l1t.mtc.module.blocklock.sql;

import li.l1t.common.exception.InternalException;
import li.l1t.common.misc.XyLocation;
import li.l1t.common.sql.sane.util.AbstractJdbcEntityCreator;
import li.l1t.mtc.api.module.inject.InjectMe;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Creates block lock instances from JDBC result sets.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
class JdbcBlockLockCreator extends AbstractJdbcEntityCreator<SqlBlockLock> {
    private final Server server;

    @InjectMe
    JdbcBlockLockCreator(Server server) {
        this.server = server;
    }

    @Override
    public SqlBlockLock createFromCurrentRow(ResultSet rs) throws SQLException {
        return new SqlBlockLock(
                location(rs), rs.getTimestamp("creationdate").toInstant(),
                material(rs.getString("type")), uuid(rs, "creatoruuid"),
                instantOrNull(rs, "removaldate"), uuid(rs, "removeruuid")
        );
    }

    private Instant instantOrNull(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        } else {
            return timestamp.toInstant();
        }
    }

    private XyLocation location(ResultSet rs) throws SQLException {
        String worldName = rs.getString("world");
        World world = server.getWorld(worldName);
        if (world == null) {
            throw new InternalException("Unknown world: " + worldName);
        }
        return new XyLocation(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
    }

    private Material material(String materialName) {
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            throw new InternalException("Unknown material: " + materialName);
        } else {
            return material;
        }
    }
}
