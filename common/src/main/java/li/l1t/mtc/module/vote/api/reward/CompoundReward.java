/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote.api.reward;

import li.l1t.mtc.module.vote.api.Vote;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * A reward that also applies other rewards when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-11
 */
public interface CompoundReward extends Reward {
    Stream<Reward> findRewardsFor(Player player, Vote vote);
}
