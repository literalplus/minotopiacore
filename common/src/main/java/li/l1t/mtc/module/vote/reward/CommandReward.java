/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.reward;

import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * A reward that executes a console command when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
@SerializableAs("mtc.vote.reward.command")
public class CommandReward implements ConfigurationSerializable, Reward {
    private final String rawCommand;

    public CommandReward(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    @Override
    public void apply(Player player, Vote vote) {
        Server server = player.getServer();
        String command = MacroReplacementService.INSTANCE.replaceMacros(player, vote, rawCommand);
        server.dispatchCommand(server.getConsoleSender(), command);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("command", rawCommand);
        return result;
    }
}
