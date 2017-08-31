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

package li.l1t.mtc.module.nub.task;

import li.l1t.common.util.task.ImprovedBukkitRunnable;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.nub.LocalProtectionManager;
import li.l1t.mtc.module.nub.api.NubProtection;
import li.l1t.mtc.module.nub.api.ProtectionService;
import li.l1t.mtc.module.nub.service.SimpleProtectionService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A task that periodically checks active protections for expiry.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public class ProtectionCheckTask extends ImprovedBukkitRunnable {
    private final LocalProtectionManager manager;
    private final ProtectionService service;
    private final Plugin plugin;

    @InjectMe
    public ProtectionCheckTask(LocalProtectionManager manager, SimpleProtectionService service, MTCPlugin plugin) {
        this.manager = manager;
        this.service = service;
        this.plugin = plugin;
    }

    public void start() {
        runTaskTimer(plugin, 3L * 60L * 20L);
    }

    @Override
    public void run() {
        manager.getAllProtections()
                .forEach(this::notifyProtectionStatus);
    }

    private void notifyProtectionStatus(NubProtection protection) {
        Player player = plugin.getServer().getPlayer(protection.getPlayerId());
        if (player == null) {
            plugin.getLogger().warning("[N.u.b.] Protection for unknown player expired: " + protection.getPlayerId());
            manager.removeProtection(protection.getPlayerId());
        } else {
            if (protection.isExpired()) {
                service.expireProtection(player, protection);
            } else {
                service.showOwnProtectionStatusTo(player);
            }
        }
    }
}
