/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.vote;

import com.google.common.collect.ImmutableList;
import li.l1t.mtc.module.vote.reward.CommandReward;
import li.l1t.mtc.module.vote.reward.ItemReward;
import li.l1t.mtc.module.vote.reward.MessageReward;
import li.l1t.mtc.module.vote.reward.compound.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

/**
 * Registers configuration serialisable classes of the vote module with the serialisation provider.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
class ConfigurationRegistration {
    static void registerAll() {
        ImmutableList.of(
                CommandReward.class, ItemReward.class, StreakCompoundReward.class, WeekdayCompoundReward.class,
                RandomCompoundReward.class, AllOfCompoundReward.class, WeightedRewardWrapper.class,
                MessageReward.class
        ).forEach(ConfigurationSerialization::registerClass);
        ConfigurationSerialization.registerClass(ItemStack.class, "mtc.itemstack");
    }
}
