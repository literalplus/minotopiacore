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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wraps a reward in a compound reward and stores a double weight with it.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-12
 */
@SerializableAs("mtc.vote.rsub")
public class WeightedRewardWrapper extends AbstractCompoundReward {
    private static final String WEIGHT_PATH = "weight";
    private static final String REWARDS_PATH = "reward";
    private final List<Reward> rewards;
    private final double weight;

    public WeightedRewardWrapper(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        this.weight = config.findTyped(WEIGHT_PATH, double.class).orElse(1D);
        this.rewards = config.getCollection(REWARDS_PATH, Reward.class, Collectors.toList());
    }

    public WeightedRewardWrapper(double weight, Reward reward) {
        this.weight = weight;
        this.rewards = Collections.singletonList(reward);
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public Stream<Reward> findRewardsFor(Player player, Vote vote) {
        return rewards.stream();
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(WEIGHT_PATH, weight)
                .put(REWARDS_PATH, rewards)
                .build();
    }
}
