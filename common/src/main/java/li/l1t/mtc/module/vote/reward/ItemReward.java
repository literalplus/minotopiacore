/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
