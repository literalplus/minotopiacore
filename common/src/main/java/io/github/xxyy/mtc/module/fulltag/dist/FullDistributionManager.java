/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag.dist;

import com.google.common.base.Preconditions;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.api.misc.Cache;
import io.github.xxyy.mtc.logging.LogManager;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.fulltag.FullTagModule;
import io.github.xxyy.mtc.module.fulltag.model.FullData;
import io.github.xxyy.mtc.module.fulltag.model.FullInfo;
import io.github.xxyy.mtc.yaml.ManagedConfiguration;
import net.md_5.bungee.api.ChatColor;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Manages the distribution of full items to players. Supports storing the items if the receiver does not currently
 * have space in their inventory.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 10/09/15
 */
public class FullDistributionManager implements Cache {
    private static final Logger LOGGER = LogManager.getLogger(FullDistributionManager.class);
    private static final String STORAGE_PATH = "unstored";

    // These are all theoretically available fulls, even if the receiver is not online, etc
    private final Map<Integer, UUID> queuedFulls = new ConcurrentHashMap<>();
    // Immediately queued fulls with data attached already, waiting for their receiver to retrieve them
    private final SetMultimap<UUID, FullInfo> queuedFullInfos = MultimapBuilder.hashKeys().hashSetValues().build();
    private final ManagedConfiguration storage;
    @Nonnull
    private final FullTagModule module;

    public FullDistributionManager(@Nonnull FullTagModule module) {
        this.module = module;
        this.storage = ManagedConfiguration.fromDataFolderPath( //TODO: Migrate to SQL
                "modules/fulltag/dist.stor.yml", ClearCacheBehaviour.NOTHING, module.getPlugin()
        );

        ConfigurationSection section = storage.getConfigurationSection(STORAGE_PATH);
        if (section == null) {
            section = storage.createSection(STORAGE_PATH);
        }
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            try {
                int id = Integer.parseInt(entry.getKey());
                UUID uuid = UUID.fromString(entry.getValue().toString());
                queuedFulls.put(id, uuid);
            } catch (IllegalArgumentException e) { //NumberFormatException is subclass
                LOGGER.warn("Encountered unreadable full queue entry: " + entry.getKey() + " -> " + entry.getValue(), e);
            }
        }

        new FullDistributionListener(this); //registers itself automagically
    }

    /**
     * Requests for the manager to distribute a full item to a player. The exact distribution procedure is not defined
     * and may involve the player accepting the request, at any time.
     *
     * @param info     the full info of the item to store
     * @param receiver the player who is going to receive the item
     */
    public void requestStore(@Nonnull FullInfo info, Player receiver) {
        Preconditions.checkNotNull(info, "info");
        Preconditions.checkNotNull(receiver, "receiver");
        Preconditions.checkArgument(info.getData() != null, "info must have valid data associated!");
        LOGGER.info("Full storage request: {} -> {} (from {})",
                info.getId(), info.getData().getReceiverId(), info.getData().getSenderId());

        queuedFulls.put(info.getId(), info.getData().getReceiverId());
        queuedFullInfos.put(info.getData().getReceiverId(), info);
        saveStorage();
    }

    /**
     * Notifies a player of how many full items are waiting for them, or that none are waiting for them, depending on
     * whether there are retrievable items for that player.
     *
     * @param receiver the player to notify
     */
    public void notifyWaiting(@Nonnull Player receiver) {
        long waitingItemCount = queuedFulls.values().stream()
                .filter(u -> receiver.getUniqueId().equals(u))
                .count();

        if (waitingItemCount > 0) {
            receiver.spigot().sendMessage(
                    new XyComponentBuilder("Auf dich warten ").color(ChatColor.YELLOW)
                            .append(String.valueOf(waitingItemCount), ChatColor.GOLD)
                            .append(" Fullteile! ", ChatColor.YELLOW)
                            .append("/fulls ", ChatColor.GOLD)
                            .append("[Mehr Info... (klick)]", ChatColor.DARK_GREEN, ChatColor.BOLD, ChatColor.UNDERLINE)
                            .command("/canhasfull list")
                            .create()
            );
        } else {
            receiver.sendMessage("§eAuf dich warten keine Fullteile mehr.");
        }
    }

    /**
     * Creates an item stack for a specific full item.
     *
     * @param data     the full data to create a stack for
     * @param receiver the player who is going to receive the stack, may not be null
     * @return an item stack for that data and receiver
     */
    @Nonnull
    public ItemStack createStack(@Nonnull FullData data, @Nonnull Player receiver) {
        Preconditions.checkNotNull(receiver, "receiver");
        Preconditions.checkNotNull(data, "data");

        ItemStackFactory factory = new ItemStackFactory(data.getPart().getMaterial())
                .lore(FullTagModule.FULL_LORE_PREFIX + data.getId())
                .lore(String.format("§6Besitzer: %s", receiver.getName()))
                .lore(String.format("§6UUID: %s", receiver.getUniqueId().toString().substring(0, 9)));

        Arrays.stream(Enchantment.values())
                .filter(e -> e.canEnchantItem(factory.getBase()))
                .filter(e -> data.isThorns() || !e.equals(Enchantment.THORNS))
                .forEach(e -> factory.enchant(e, e.getMaxLevel()));

        return factory.produce();
    }

    /**
     * Requests the currently retrievable fulls (i.e. fulls that belong to that player but have not yet been distributed
     * to them in the current map) for an online player. Note that, if this method is called on the main thread, the
     * callback will always be called in the main thread. This method might complete retrieval immediately if results
     * are cached. If not, data will be retrieved asynchronously.
     *
     * @param receiver the player to request information for
     * @param callback the callback to call with the information
     */
    public void requestRetrievableFulls(@Nonnull Player receiver, @Nonnull Consumer<Set<FullInfo>> callback) {
        Preconditions.checkNotNull(receiver, "receiver");
        Preconditions.checkNotNull(callback, "callback");

        Set<Integer> queuedIds = getQueuedFullIds(receiver.getUniqueId());
        Set<Integer> unfetchedIds = new HashSet<>(queuedIds);
        Set<FullInfo> queuedInfos = queuedFullInfos.get(receiver.getUniqueId());

        queuedInfos.forEach(fi -> unfetchedIds.remove(fi.getId()));

        if (!unfetchedIds.isEmpty()) { //Runs async because database latency shouldn't stop the main thread
            module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(), () -> {
                Set<FullInfo> fullInfos = unfetchedIds.stream()
                        .map(id -> {
                            FullInfo info = getModule().getRegistry().getById(id);
                            if (info == null || info.getData() == null || !info.getData().isValid()) {
                                queuedFulls.remove(id); //If the info is no longer valid, remove from queue too to prevent players from seeing it again, etc
                            }
                            return info;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                fullInfos.addAll(queuedInfos);

                module.getPlugin().getServer().getScheduler().runTask(module.getPlugin(), () ->
                                callback.accept(fullInfos)
                );
            });
        } else {
            callback.accept(queuedInfos);
        }
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        saveStorage();
    }

    /**
     * Attempts to store a full item in a player's inventory.
     *
     * @param info     the item to store
     * @param receiver the player whose inventory to store in
     * @return whether the item has been added to the inventory
     */
    protected boolean attemptStore(@Nonnull FullInfo info, @Nonnull Player receiver) {
        Preconditions.checkArgument(info.getData() != null, "info must have valid associated data");
        if (!receiver.isOnline()) {
            return false;
        }
        ItemStack stack = createStack(info.getData(), receiver);
        Map<Integer, ItemStack> unstored = receiver.getInventory().addItem(stack);
        if (!unstored.isEmpty()) {
            return false;
        }
        LOGGER.info("Stored full item {} at {}/{}", info, receiver.getName(), receiver.getUniqueId());
        queuedFulls.remove(info.getId());
        queuedFullInfos.remove(info.getData().getReceiverId(), info);
        receiver.sendMessage(String.format("%sDu hast ein Fullteil vom Typ %s und mit der ID %d erhalten!",
                module.getPlugin().getChatPrefix(), info.getData().getPart().getAlias(), info.getId()));
        return true;
    }

    protected Set<Integer> getQueuedFullIds(UUID receiverId) {
        return queuedFulls.entrySet().stream()
                .filter(e -> e.getValue().equals(receiverId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Nonnull
    public FullTagModule getModule() {
        return module;
    }

    protected void saveStorage() {
        Map<Integer, String> serialisedMap = new HashMap<>();
        queuedFulls.forEach((i, uuid) -> serialisedMap.put(i, uuid.toString())); //configuration framework can't save UUID objects
        storage.set(STORAGE_PATH, serialisedMap);
        storage.asyncSave(module.getPlugin());
    }
}
