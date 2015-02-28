/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.misc.cmd.MTCCommandExecutor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class InfiniteBlockModule extends ConfigurableMTCModule implements Listener {
    public static final String NAME = "InfiniteDispensers"; // don't change such constants as that would require manual cfg changes
    public static final String INFINITY_TAG = "mtc.infinite";
    private static final String DATA_PATH = "dispensers";
    private List<XyLocation> infiniteBlockLocations;

    public InfiniteBlockModule() {
        super(NAME, "infdisps.stor.yml", ClearCacheBehaviour.SAVE);
    }

    @Override
    public void enable(MTC plugin) {
        super.enable(plugin);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("infdisp").setExecutor(new CommandHandler());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void reloadImpl() {
        infiniteBlockLocations = (List<XyLocation>) configuration.getList(DATA_PATH, new ArrayList<XyLocation>());

        Iterator<XyLocation> it = infiniteBlockLocations.iterator();

        while (it.hasNext()) { // any reason not using enhanced for loop or #forEach?
            XyLocation location = it.next();
            Block blk = location.getBlock();
            BlockState state = blk.getState();
            if (!canBeMadeInfinite(state)) {
                plugin.getLogger().info("Removing infinite tag at " + location.pretyPrint() + " because the block changed to " + state.getType() + "!");
                it.remove();
            } else {
                addInfiniteMetadata(state);
            }
        }

        save();
    }

    @Override
    public void disable(MTC plugin) {

    }

    public void addInfiniteToCfg(Location input) { //REFACTOR: un-spaghetti //will be changed separately if xyc is updated
        XyLocation loc;
        if (input instanceof XyLocation) {
            loc = (XyLocation) input;
        } else {
            loc = new XyLocation(input);
        }

        infiniteBlockLocations.add(loc);
        configuration.set(DATA_PATH, infiniteBlockLocations);
        save();
    }

    public void removeInfiniteFromCfg(Location input) { //REFACTOR: un-spaghetti //will be changed separately if xyc is updated
        XyLocation loc;
        if (input instanceof XyLocation) {
            loc = (XyLocation) input;
        } else {
            loc = new XyLocation(input);
        }

        infiniteBlockLocations.remove(loc);
        configuration.set(DATA_PATH, infiniteBlockLocations);
        save();
    }

    private void addInfiniteMetadata(BlockState dispenser) {
        dispenser.setMetadata(INFINITY_TAG, new FixedMetadataValue(plugin, null));
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
        if (!(evt.getInventory() instanceof AnvilInventory)) {
            doIfInfinite(evt.getInventory().getHolder(), val -> {
                evt.setCancelled(true);
                MTCHelper.sendLoc("XU-infdispclk", (Player) evt.getPlayer(), true);
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
            if (blk.getType() == Material.ANVIL) {   //always the byte casts //don't update physics so anvil can't fall down when clicked if placed w/o physics on
                doIfInfinite(blk, val -> blk.setData(getUndamagedDataValueOfAnvil(blk.getData()), false)); //no possibility without using a deprecated method
            }
        }
    }

    private byte getUndamagedDataValueOfAnvil(byte dataValue) { // see http://minecraft.gamepedia.com/Anvil#Data_values "Block"
        if (dataValue >= 4) {
            if (dataValue < 8) {
                dataValue -= 4;
            } else if (dataValue < 12) {
                return dataValue -= 8;
            } else {
                throw new IllegalArgumentException("invalid anvil block data value");
            }
        }
        return dataValue;
    }

    /////////////////////////// COMMAND HANDLER ////////////////////////////////////////////////////////////////////////

    public class CommandHandler extends MTCCommandExecutor {
        @Override
        public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.infinitedispenser", label)) {
                return true;
            }
            if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                return true;
            }

            Player plr = (Player) sender;
            if (args.length > 0) {
                switch (args[0]) {
                    case "on":
                        BlockState disp = checkTargetBlock(plr);
                        if (disp == null) {
                            return true;
                        }
                        addInfiniteToCfg(disp.getLocation());
                        addInfiniteMetadata(disp);
                        return MTCHelper.sendLoc("XU-infdispon", sender, true);
                    case "off":
                        BlockState disp2 = checkTargetBlock(plr);
                        if (disp2 == null) {
                            return true;
                        }
                        disp2.removeMetadata(INFINITY_TAG, plugin);
                        removeInfiniteFromCfg(disp2.getLocation());
                        return MTCHelper.sendLoc("XU-infdispoff", sender, true);
                    case "list":
                        AtomicInteger i = new AtomicInteger(0);
                        infiniteBlockLocations.stream().forEach(loc -> {
                            //@formatter:off
                            new FancyMessage(loc.getBlock().getType() + " @ ")
                                        .color(ChatColor.GOLD)
                                    .then(loc.pretyPrint() +" [klick]")
                                        .color(ChatColor.YELLOW)
                                        .tooltip("Hier klicken zum Teleportieren: ", loc.toTpCommand(null))
                                        .command(loc.toTpCommand(null))
                                    .send(plr);
                            i.addAndGet(1);
                            //@formatter:on
                        });
                        sender.sendMessage("§6" + i.get() + " §eInfiniteDispenser registriert.");
                        //intended fall-through?
                    default:
                        break;
                }
            }
            return MTCHelper.sendLocArgs("XU-infdisphelp", sender, false, label);
        }

        private BlockState checkTargetBlock(Player plr) {
            @SuppressWarnings("deprecation")
            Block blk = plr.getTargetBlock(null, 100); //BUKKIT! HAVEN'T I TOLD YOU NOT TO DEPRECATED USEFUL STUFF?!
            if (blk == null) {
                MTCHelper.sendLoc("XU-nodisp", plr, true);
                return null;
            } else {
                BlockState state = blk.getState(); //just call Block#getState() once, as it consumes time

                if (canBeMadeInfinite(state)) {
                    return state;
                }
                MTCHelper.sendLoc("XU-nodisp", plr, true);
                return null;
            }
        }
    }
    private boolean canBeMadeInfinite(BlockState state) {
        //maybe use a chain of material checks instead of computing the state of block even if it does not fit to the criteria
        return state.getType() == Material.ANVIL || state instanceof InventoryHolder;
    }

}
