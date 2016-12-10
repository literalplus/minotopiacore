/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
