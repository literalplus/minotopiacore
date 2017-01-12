/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.reward.compound;

import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.CompoundReward;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * Abstract base class for rewards that apply other rewards when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
abstract class AbstractCompoundReward implements CompoundReward, ConfigurationSerializable {
    public abstract Stream<Reward> findRewardsFor(Player player, Vote vote);

    @Override
    public void apply(Player player, Vote vote) {
        findRewardsFor(player, vote).forEach(reward -> reward.apply(player, vote));
    }
}
