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

package li.l1t.mtc.module.vote;

import com.google.common.collect.ImmutableList;
import li.l1t.mtc.module.vote.reward.BroadcastReward;
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
                MessageReward.class, BroadcastReward.class
        ).forEach(ConfigurationSerialization::registerClass);
        ConfigurationSerialization.registerClass(ItemStack.class, "mtc.itemstack");
    }
}
