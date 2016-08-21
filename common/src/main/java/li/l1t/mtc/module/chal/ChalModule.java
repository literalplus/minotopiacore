/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chal;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.helper.MTCHelper;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages advent calendar module. This allows staff to place chests and players to open them
 * exactly once at the day they're scheduled for.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
public class ChalModule extends ConfigurableMTCModule {
    public static final String NAME = "Chal";
    public static final String ADMIN_PERMISSION = "mtc.chal.admin";
    public static final String DATE_BYPASS_PERMISSION = "mtc.chal.bypass";
    public static final String DATE_BYPASS_PAST_PERMISSION = "mtc.chal.bypass-past";
    public static final String METADATA_KEY = "chal-metadata";
    private static final String LOCATIONS_PATH = "locations";
    private static final String USED_PATH = "used";
    private List<ChestLocation> locations;
    private ListMultimap<String, UUID> chestsUsed = MultimapBuilder.hashKeys().arrayListValues().build();
    private Map<UUID, ChalDate> chestSelectors = new HashMap<>();

    public ChalModule() {
        super(NAME, "modules/chal/data.yml", ClearCacheBehaviour.SAVE, false);
        ConfigurationSerialization.registerClass(ChestLocation.class);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        registerCommand(new CommandChal(this), "chal", "adventskalender")
                .behaviour(CommandBehaviours.permissionChecking(ChalModule.ADMIN_PERMISSION));
        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::save, 60 * 20L, 5 * 60 * 20L); //Don't wanna save every time somebody opens a chest
    }

    @Override
    public void disable(MTCPlugin plugin) {
        super.disable(plugin);

        locations.stream()
                .filter(Objects::nonNull)
                .map(Location::getBlock)
                .filter(Objects::nonNull)
                .forEach(b -> b.removeMetadata(METADATA_KEY, plugin));
        plugin.getServer().getOnlinePlayers()
                .forEach(p -> p.removeMetadata(METADATA_KEY, plugin));

        save();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        locations = (List<ChestLocation>) configuration.getList(LOCATIONS_PATH, new ArrayList<>());
        chestsUsed.clear();
        if (configuration.isConfigurationSection(USED_PATH)) {
            for (Map.Entry<String, Object> entry : configuration.getConfigurationSection(USED_PATH).getValues(false).entrySet()) {
                chestsUsed.putAll(entry.getKey(),
                        ((List<String>) entry.getValue()).stream()
                                .map(UUID::fromString)
                                .collect(Collectors.toList()));
            }
        }

        Iterator<ChestLocation> it = locations.iterator();
        while (it.hasNext()) {
            ChestLocation location = it.next();
            Block block = location.getBlock();
            if (block == null || block.getType() != Material.CHEST) {
                plugin.getLogger().info("Removing invalid Chal chest at " + location.prettyPrint());
                it.remove();
                continue;
            }
            block.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, location));
        }
    }

    @Override
    public void save() {
        configuration.set(LOCATIONS_PATH, locations);
        Map<String, List<String>> serializableChestsUsed = new HashMap<>();

        chestsUsed.asMap().forEach((s, us) ->
                serializableChestsUsed.put(s, us.stream().map(Object::toString).collect(Collectors.toList())));

        configuration.set(USED_PATH, serializableChestsUsed);
        super.save();
    }

    public boolean hasOpened(Player plr, ChestLocation location) {
        return chestsUsed.containsKey(location.getDate().toString()) &&
                chestsUsed.containsEntry(location.getDate().toString(), plr.getUniqueId());
    }

    public void resetOpened(ChestLocation location) {
        chestsUsed.removeAll(location.getDate().toString());
    }

    public void resetOpened(UUID uuid) {
        chestsUsed.values()
                .removeIf(u -> u.equals(uuid));
        save();
    }

    public void setOpened(Player plr, ChestLocation location) {
        chestsUsed.put(location.getDate().toString(), plr.getUniqueId());
    }

    public ChestLocation createChest(Location loc, ChalDate date) {
        ChestLocation result = new ChestLocation(loc, date);
        loc.getBlock().setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, result));
        locations.add(result);
        save();
        return result;
    }

    public void removeChest(ChestLocation location) {
        resetOpened(location);
        locations.remove(location);
        location.getBlock().removeMetadata(METADATA_KEY, plugin);
        location.getBlock().setType(Material.MELON_BLOCK);
        save();
    }

    public List<ChestLocation> getLocations() {
        return locations;
    }

    public void startSelection(Player plr, ChalDate date) {
        chestSelectors.put(plr.getUniqueId(), date);
        plr.sendMessage(String.format("§eKlicke jetzt die Kiste deiner Wahl für den %s an!", date.toReadable()));
    }

    private class EventListener implements Listener {
        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onInteract(PlayerInteractEvent evt) {
            if ((evt.getAction() != Action.LEFT_CLICK_BLOCK && evt.getAction() != Action.RIGHT_CLICK_BLOCK) || evt.getClickedBlock() == null) {
                return;
            }

            Player plr = evt.getPlayer();
            if (chestSelectors.containsKey(plr.getUniqueId())) {
                ChalDate date = chestSelectors.remove(plr.getUniqueId());
                if (evt.getClickedBlock().getType() == Material.CHEST) {
                    createChest(evt.getClickedBlock().getLocation(), date);
                    plr.sendMessage("§aKiste für " + date + " gesetzt!");
                } else {
                    plr.sendMessage("§eAuswahl abgebrochen!");
                }
                evt.setCancelled(true);
                return;
            } else if (plr.hasMetadata(METADATA_KEY)) {
                return; //Allow to open chests
            }

            if (evt.getClickedBlock().getType() != Material.CHEST) {
                return;
            }

            Block block = evt.getClickedBlock();
            if (block.hasMetadata(METADATA_KEY)) {
                evt.setCancelled(true);
                ChestLocation location = (ChestLocation) block.getMetadata(METADATA_KEY).get(0).value();

                if (hasOpened(plr, location)) {
                    MTCHelper.sendLocArgs("XU-chaopened", plr, false, location.getDate().getDay());
                    return;
                }

                if (location.openFor(plr)) {
                    setOpened(plr, location);
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent evt) {
            if (evt.getBlock() == null || evt.getBlock().getType() != Material.CHEST) {
                return;
            }

            Block block = evt.getBlock();
            if (block.hasMetadata(METADATA_KEY)) {
                ChestLocation location = (ChestLocation) block.getMetadata(METADATA_KEY).get(0).value();
                removeChest(location);
                evt.getPlayer().sendMessage("§aChal-Kiste entfernt.");
            }
        }
    }
}
