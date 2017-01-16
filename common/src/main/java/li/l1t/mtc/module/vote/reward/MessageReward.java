/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
 * A reward that sends a message to the player who voted when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-16
 */
@SerializableAs("mtc.vote.msg")
public class MessageReward implements Reward, ConfigurationSerializable {
    public static final String MESSAGE_PATH = "message";
    private final String message;

    public MessageReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        message = ChatHelper.convertStandardColors(
                config.stringify(MESSAGE_PATH).orElse("Du hast via $service gevoted, aber es ist keine Nachricht definiert.")
        );
    }

    @Override
    public void apply(Player player, Vote vote) {
        player.sendMessage(
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
