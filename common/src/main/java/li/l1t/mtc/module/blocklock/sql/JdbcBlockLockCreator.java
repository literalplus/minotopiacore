/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
                instantOrNull(rs, "removaldate"), uuid(rs, "removedby")
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
