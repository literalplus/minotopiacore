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

package li.l1t.mtc.misc;

import li.l1t.common.util.XyValidate;
import li.l1t.mtc.api.PlayerGameManager;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MTC's implementation of PlayerGameManager.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 21.8.14
 */
public class PlayerGameManagerImpl implements PlayerGameManager {
    private Map<UUID, Plugin> playersInGames = new HashMap<>();

    public PlayerGameManagerImpl(Plugin plugin) {
        plugin.getServer().getServicesManager().register(PlayerGameManager.class, this, plugin, ServicePriority.Low);
    }

    @Override
    public void setInGame(boolean inGame, UUID uuid, Plugin plugin) throws IllegalStateException {
        Validate.notNull(uuid, "UUID cannot be null!");
        Validate.notNull(plugin, "plugin cannot be null!");
        if (inGame) {
            XyValidate.validateState(!isInGame(uuid), "Player can only be in one game at the same time!");
            playersInGames.put(uuid, plugin);
        } else {
            Validate.isTrue(getProvidingPlugin(uuid) == null || plugin.equals(getProvidingPlugin(uuid)), "Can't remove other plugins' entries!");
            playersInGames.remove(uuid);
        }
    }

    @Override
    public Plugin getProvidingPlugin(UUID uuid) {
        return playersInGames.get(uuid);
    }
}
