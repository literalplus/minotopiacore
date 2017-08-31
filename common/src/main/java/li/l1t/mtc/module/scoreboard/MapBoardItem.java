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

package li.l1t.mtc.module.scoreboard;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A scoreboard item that stores values in a map.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-14
 */
public class MapBoardItem implements BoardItem {
    private final String identifier;
    private final String displayName;
    private final Map<UUID, String> playerValueMap = new HashMap<>();

    public MapBoardItem(String identifier, String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDisplayName(Player player) {
        return displayName;
    }

    public void setValue(Player player, Object value) {
        Preconditions.checkNotNull(player, "player");
        playerValueMap.put(player.getUniqueId(), String.valueOf(value));
    }

    @Override
    public String getValue(Player player) {
        return playerValueMap.getOrDefault(player.getUniqueId(), null);
    }

    @Override
    public boolean isVisibleTo(Player player) {
        return playerValueMap.containsKey(player.getUniqueId());
    }

    @Override
    public void cleanUp(Player player) {
        playerValueMap.remove(player.getUniqueId());
    }
}
