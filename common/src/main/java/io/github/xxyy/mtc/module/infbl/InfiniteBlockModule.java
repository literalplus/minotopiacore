/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.infbl;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.misc.cmd.MTCCommandExecutor;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class InfiniteBlockModule extends ConfigurableMTCModule implements Listener {
    public static final String NAME = "InfiniteDispensers"; //keeping legacy constant for backwards compatibility
    public static final String INFINITY_TAG = "mtc.infinite";
    public static final String INFINITE_PERMISSION = "mtc.infinitedispenser"; //keeping legacy constant for backwards compatibility
    private static final String DATA_PATH = "dispensers"; //keeping legacy constant for backwards compatibility
    private List<XyLocation> infiniteBlockLocations;

    public InfiniteBlockModule() {
        super(NAME, "infdisps.stor.yml", ClearCacheBehaviour.SAVE, false); //keeping legacy constant for backwards compatibility
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("infiniteblocks").setExecutor(new CommandHandler());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void reloadImpl() {
        infiniteBlockLocations = (List<XyLocation>) configuration.getList(DATA_PATH, new ArrayList<XyLocation>());

        Iterator<XyLocation> it = infiniteBlockLocations.iterator();

        while (it.hasNext()) { //using iterator instead of Stream API for Iterator#remove() method
            XyLocation location = it.next();
            Block blk = location.getBlock();
            if (canBeMadeInfinite(blk.getType())) {
                addInfiniteMetadata(blk);
            } else {
                plugin.getLogger().info("Removing infinite tag at " + location.prettyPrint() + " because the block changed to " + blk.getType() + "!");
                it.remove();
            }
        }

        save();
    }

    public void addInfiniteToCfg(Location input) {
        infiniteBlockLocations.add(XyLocation.of(input));
        configuration.set(DATA_PATH, infiniteBlockLocations);
        save();
    }

    public void removeInfiniteFromCfg(Location input) {
        infiniteBlockLocations.remove(XyLocation.of(input));
        configuration.set(DATA_PATH, infiniteBlockLocations);
        save();
    }

    private void addInfiniteMetadata(Block blk) {
        blk.setMetadata(INFINITY_TAG, new FixedMetadataValue(plugin, null));
    }

    private void doIfInfinite(InventoryHolder possibleState, Consumer<MetadataValue> consumer) {
        if (possibleState instanceof BlockState) {
            doIfInfinite((BlockState) possibleState, consumer);
        }
    }

    private void doIfInfinite(BlockState state, Consumer<MetadataValue> consumer) {
        state.getMetadata(INFINITY_TAG).stream() //If performance issues arise, make this an isEmpty() call - https://twitter.com/_xxyy/status/556618846401216512
                .filter(val -> plugin.equals(val.getOwningPlugin()))
                .limit(1)
                .forEach(consumer);
    }

    private void doIfInfinite(Block blk, Consumer<MetadataValue> consumer) {
        blk.getMetadata(INFINITY_TAG).stream() //If performance issues arise, make this an isEmpty() call - https://twitter.com/_xxyy/status/556618846401216512
                .filter(val -> plugin.equals(val.getOwningPlugin()))
                .limit(1)
                .forEach(consumer);
    }

    ////////////// EVENT HANDLERS //////////////////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true)
    public void onInfiniteDispenserOrDropper(BlockDispenseEvent evt) {
        BlockState state = evt.getBlock().getState();
        doIfInfinite(state, val -> ((InventoryHolder) state).getInventory().addItem(evt.getItem().clone()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInfiniteHopper(InventoryMoveItemEvent evt) {
        doIfInfinite(evt.getInitiator().getHolder(), val -> evt.getInitiator().addItem(evt.getItem().clone())); //Clone item if initiator is infinite
        doIfInfinite(evt.getDestination().getHolder(), val -> evt.setCancelled(true)); //Cancel if destination is infinite - bugusing
    }

    @EventHandler(ignoreCancelled = true)
    public void onInfiniteInventoryOpen(InventoryOpenEvent evt) {
        if (evt.getInventory().getType() != InventoryType.ANVIL) {
            doIfInfinite(evt.getInventory().getHolder(), val -> {
                evt.setCancelled(true);
                MTCHelper.sendLoc("XU-infdispclk", evt.getPlayer(), true); //keeping legacy constant for backwards compatibility
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryPickup(InventoryPickupItemEvent evt) {
        doIfInfinite(evt.getInventory().getHolder(), val -> {
            evt.setCancelled(true);
            evt.getItem().remove();
        });
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onAnvilClick(PlayerInteractEvent evt) {
        if (evt.hasBlock() && evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block blk = evt.getClickedBlock();
            if (blk.getType() == Material.ANVIL) {   //don't update physics so anvil can't fall down when clicked if placed w/o physics on
                doIfInfinite(blk, val -> blk.setData(getUndamagedDataValueOfAnvil(blk.getData()), false)); //no possibility without using a deprecated method
            }
        }
    }

    private byte getUndamagedDataValueOfAnvil(byte dataValue) { //see http://minecraft.gamepedia.com/Anvil#Data_values section 'Block'
        if (dataValue < 0 || dataValue > 11) {
            throw new IllegalArgumentException("invalid anvil block data value");
        }
        if (dataValue >= 4) {
            if (dataValue < 8) {
                dataValue -= 4;
            } else {
                dataValue -= 8;
            }
        }
        return dataValue;
    }

    /////////////////////////// COMMAND HANDLER ////////////////////////////////////////////////////////////////////////

    private boolean canBeMadeInfinite(Material material) {
        switch (material) {
            case ANVIL:
            case BEACON:
            case BREWING_STAND:
            case CHEST:
            case DISPENSER:
            case DROPPER:
            case FURNACE:
            case BURNING_FURNACE:
            case HOPPER:
                return true;
            default:
                return false;
        }
    }

    public class CommandHandler extends MTCCommandExecutor {
        @Override
        @SuppressWarnings("ConstantConditions")
        public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
            if (!CommandHelper.checkPermAndMsg(sender, INFINITE_PERMISSION, label)) {
                return true;
            }
            if (args.length > 0) {
                if (!(sender instanceof Player) && !args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage("§cNur Spieler können andere Befehle als /" + label + " list verwenden!");
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "on":
                        Block blk = getAndCheckTargetBlock((Player) sender);
                        if (blk == null) {
                            return true;
                        }
                        addInfiniteToCfg(blk.getLocation());
                        addInfiniteMetadata(blk);
                        return MTCHelper.sendLoc("XU-infdispon", sender, true); //legacy constant #backwards-compatibility
                    case "off":
                        Block blk2 = getAndCheckTargetBlock((Player) sender);
                        if (blk2 == null) {
                            return true;
                        }
                        blk2.removeMetadata(INFINITY_TAG, plugin);
                        removeInfiniteFromCfg(blk2.getLocation());
                        return MTCHelper.sendLoc("XU-infdispoff", sender, true); //legacy constant #backwards-compatibility
                    case "list":
                        AtomicInteger i = new AtomicInteger(0);
                        if (sender instanceof ConsoleCommandSender) {
                            infiniteBlockLocations.stream().forEach(loc -> {
                                sender.sendMessage("" + ChatColor.GOLD + loc.getBlock().getType() + " @ " + loc.prettyPrint());
                                i.addAndGet(1);
                            });
                        } else {
                            infiniteBlockLocations.stream().forEach(loc -> {
                                //@formatter:off
                                new FancyMessage(loc.getBlock().getType() + " @ ")
                                            .color(ChatColor.GOLD)
                                        .then(loc.prettyPrint() + " [klick]")
                                            .color(ChatColor.YELLOW)
                                            .tooltip("Hier klicken zum Teleportieren: ", loc.toTpCommand(null))
                                            .command(loc.toTpCommand(null))
                                        .send(sender);
                                i.addAndGet(1);
                                //@formatter:on
                            });
                        }
                        sender.sendMessage("§6" + i.get() + " §eInfiniteBlocks registriert.");
                        return true;
                    default:
                        break;
                }
            }
            return MTCHelper.sendLocArgs("XU-infdisphelp", sender, false, label); //keeping legacy constant for backwards compatibility
        }

        @Nullable
        private Block getAndCheckTargetBlock(@NotNull Player plr) {
            Block blk = plr.getTargetBlock((Set<Material>) null, 15);
            if (blk == null || !canBeMadeInfinite(blk.getType())) {
                MTCHelper.sendLoc("XU-nodisp", plr, true); //keeping legacy constant for backwards compatibility
                return null;
            } else {
                return blk;
            }
        }
    }

}