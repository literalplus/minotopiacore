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

package li.l1t.mtc.module.vote.reward;

import com.google.common.collect.ImmutableMap;
import li.l1t.common.util.ChatHelper;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.mtc.api.chat.ChatConstants;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * A reward that broadcasts a message when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-12
 */
@SerializableAs("mtc.vote.broadcast")
public class BroadcastReward implements Reward, ConfigurationSerializable {
    public static final String MESSAGE_PATH = "message";
    private final String message;

    public BroadcastReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        message = ChatHelper.convertStandardColors(
                config.stringify(MESSAGE_PATH).orElse("$player hat gevoted, aber es ist keine Nachricht definiert.")
        );
    }

    @Override
    public void apply(Player player, Vote vote) {
        player.getServer().broadcastMessage(
                MacroReplacementService.INSTANCE.replaceMacros(
                        player, vote,
                        ChatConstants.convertCustomColorCodes(message)
                ));
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(MESSAGE_PATH, message)
                .build();
    }
}
