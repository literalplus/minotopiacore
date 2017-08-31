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

package li.l1t.mtc.module.vote.reward;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A reward that adds items to the player's inventory when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-29
 */
@SerializableAs("mtc.vote.item")
public class ItemReward implements ConfigurationSerializable, Reward {
    private static final String ITEMS_PATH = "items";
    private final List<ItemStack> items;

    public ItemReward(List<ItemStack> items) {
        this.items = items;
    }

    public ItemReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        this.items = config.getCollection(ITEMS_PATH, ItemStack.class, Collectors.toList());
    }

    @Override
    public void apply(Player player, Vote vote) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(vote, "vote");
        items.stream()
                .map(reward -> copyAndReplaceMacros(player, vote, reward))
                .map(reward -> player.getInventory().addItem(reward))
                .map(Map::values).flatMap(Collection::stream)
                .map(String::valueOf)
                .forEach(item -> MessageType.WARNING.sendTo(player, "Du konntest %s nicht erhalten, da dein Inventar voll ist!", item));
    }

    private ItemStack copyAndReplaceMacros(Player player, Vote vote, ItemStack stack) {
        ItemStackFactory factory = new ItemStackFactory(stack.clone());
        ItemMeta meta = factory.getBase().getItemMeta();
        if (meta.hasDisplayName()) {
            factory.displayName(MacroReplacementService.INSTANCE.replaceMacros(player, vote, meta.getDisplayName()));
        }
        if (meta.hasLore()) {
            factory.lore(MacroReplacementService.INSTANCE.replaceMacros(player, vote, meta.getLore()));
        }
        return factory.produce();
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(ITEMS_PATH, items)
                .build();
    }
}
