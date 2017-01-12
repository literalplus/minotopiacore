/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.reward.compound;

import com.google.common.collect.ImmutableMap;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A compound reward that applies all of its child rewards when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
@SerializableAs("mtc.vote.all-of")
public class AllOfCompoundReward extends AbstractCompoundReward {
    public static final String REWARDS_PATH = "rewards";
    private final List<Reward> rewards;

    public AllOfCompoundReward(List<Reward> rewards) {
        this.rewards = rewards;
    }

    public AllOfCompoundReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        rewards = config.getCollection(REWARDS_PATH, Reward.class, Collectors.toList());
    }

    @Override
    public Stream<Reward> findRewardsFor(Player player, Vote vote) {
        return rewards.stream();
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(REWARDS_PATH, rewards)
                .build();
    }
}
