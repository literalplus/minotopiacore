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

package li.l1t.mtc.module.lanatus.pex;

import li.l1t.lanatus.api.LanatusClient;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

/**
 * Listens for players joining and migrates their PEx group data to Lanatus, if they don't already
 * have a group set and their group is marked for auto-migration.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-01
 */
public class GroupMigrationJoinListener implements Listener {
    private final LanatusAccountMigrator migrator;
    private final PermissionManager pex;

    public GroupMigrationJoinListener(PermissionManager pex, LanatusClient lanatus) {
        migrator = new LanatusAccountMigrator(lanatus);
        migrator.registerMigrationProduct();
        this.pex = pex;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleJoinForGroupMigration(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        PermissionUser user = pex.getUser(player);
        migrator.migrateIfNecessary(user, player.getUniqueId());
    }
}
