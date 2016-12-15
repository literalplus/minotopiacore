/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
