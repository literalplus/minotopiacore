/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.reward;

import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.CompoundReward;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * Provides static utility methods for dealing with rewards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-11
 */
public class Rewards {
    private Rewards() {

    }

    public static Stream<Reward> stream(Player player, Vote vote, Reward... rewards) {
        Stream.Builder<Reward> builder = Stream.builder();
        for (Reward reward : rewards) {
            if(reward instanceof CompoundReward) {
                ((CompoundReward) reward).findRewardsFor(player, vote)
                        .forEach(builder::add);
            } else if(reward != null) {
               builder.add(reward);
            }
        }
        return builder.build();
    }
}
