/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.reward;

import com.google.common.collect.ImmutableMap;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * A reward that executes a console command when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
@SerializableAs("mtc.vote.command")
public class CommandReward implements ConfigurationSerializable, Reward {
    public static final String COMMAND_PATH = "command";
    private final String rawCommand;

    public CommandReward(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    public CommandReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        rawCommand = config.findString(COMMAND_PATH).orElse("/pleaseaddacommand");
    }

    @Override
    public void apply(Player player, Vote vote) {
        Server server = player.getServer();
        String command = MacroReplacementService.INSTANCE.replaceMacros(player, vote, rawCommand);
        server.dispatchCommand(server.getConsoleSender(), command);
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(
                        COMMAND_PATH, rawCommand)
                .build();
    }
}
