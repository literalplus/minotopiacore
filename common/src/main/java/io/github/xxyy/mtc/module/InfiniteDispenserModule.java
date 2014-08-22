package io.github.xxyy.mtc.module;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.misc.cmd.MTCCommandExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class InfiniteDispenserModule extends ConfigurableMTCModule implements Listener {
    public static final String NAME = "InfiniteDispensers";
    public static final String INFINITY_TAG = "mtc.infinite";
    private static final String DATA_PATH = "dispensers";
    private List<XyLocation> dispenserLocations;

    public InfiniteDispenserModule() {
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
        dispenserLocations = (List<XyLocation>) configuration.getList(DATA_PATH, new ArrayList<XyLocation>());

        Iterator<XyLocation> it = dispenserLocations.iterator();

        while (it.hasNext()) {
            XyLocation location = it.next();
            BlockState blockState = location.getBlock().getState();
            if (blockState.getType() != Material.HOPPER && blockState.getType() != Material.DISPENSER && blockState.getType() != Material.DROPPER) {
                plugin.getLogger().info("Removing infinite dispenser at " + location.pretyPrint() + " because the block changed to " + blockState.getType() + "!");
                it.remove();
            } else {
                initDispenser(blockState);
            }
        }

        save();
    }

    @Override
    public void disable(MTC plugin) {

    }

    protected void addDispenser(Location input) {
        XyLocation loc;
        if (input instanceof XyLocation) {
            loc = (XyLocation) input;
        } else {
            loc = new XyLocation(input);
        }

        dispenserLocations.add(loc);
        save();
    }

    protected void removeDispenser(Location input) { //REFACTOR: un-spaghetti
        XyLocation loc;
        if (input instanceof XyLocation) {
            loc = (XyLocation) input;
        } else {
            loc = new XyLocation(input);
        }

        dispenserLocations.remove(loc);
        save();
    }

    protected void initDispenser(BlockState dispenser) {
        dispenser.setMetadata(INFINITY_TAG, new MetadataValueAdapter(plugin) {
            @Override
            public void invalidate() {
            }

            @Override
            public Object value() {
                return true;
            }
        });
    }

    ////////////// EVENT HANDLERS //////////////////////////////////////////////////////////////////////////////////////

    @EventHandler(ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent evt) {
        BlockState state = evt.getBlock().getState();
        List<MetadataValue> metaData = state.getMetadata(INFINITY_TAG);
        metaData.stream()
                .filter(val -> plugin.equals(val.getOwningPlugin()))
                .forEach(val -> ((InventoryHolder) state).getInventory().addItem(evt.getItem().clone()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onHopper(InventoryMoveItemEvent evt) {
        InventoryHolder hldr = evt.getInitiator().getHolder();
        if (!(hldr instanceof BlockState)) {
            return;
        }
        BlockState blkState = (BlockState) hldr;
        List<MetadataValue> metaData = blkState.getMetadata(INFINITY_TAG);
        metaData.stream()
                .filter(val -> val.getOwningPlugin().getName().equals(MTC.instance().getName()))
                .forEach(val -> evt.getInitiator().addItem(evt.getItem().clone()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvOpen(InventoryOpenEvent evt) {
        InventoryHolder holder = evt.getInventory().getHolder();
        if (holder instanceof BlockState) {
            BlockState state = (BlockState) holder;
            if (state.getMetadata(INFINITY_TAG).stream()
                    .anyMatch(val -> plugin.equals(val.getOwningPlugin()))) {
                evt.setCancelled(true);
                MTCHelper.sendLoc("XU-infdispclk", (Player) evt.getPlayer(), true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvPickup(InventoryPickupItemEvent evt) {
        InventoryHolder holder = evt.getInventory().getHolder();
        if (holder instanceof BlockState) {
            BlockState state = (BlockState) holder;
            if (state.getMetadata(INFINITY_TAG).stream()
                    .anyMatch(val -> plugin.equals(val.getOwningPlugin()))) {
                evt.setCancelled(true);
                evt.getItem().remove();
            }
        }
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
                        addDispenser(disp.getLocation());
                        initDispenser(disp);
                        return MTCHelper.sendLoc("XU-infdispon", sender, true);
                    case "off":
                        BlockState disp2 = checkTargetBlock(plr);
                        if (disp2 == null) {
                            return true;
                        }
                        disp2.removeMetadata(INFINITY_TAG, plugin);
                        removeDispenser(disp2.getLocation());
                        return MTCHelper.sendLoc("XU-infdispoff", sender, true);
                    default:
                        break;
                }
            }
            return MTCHelper.sendLocArgs("XU-infdisphelp", sender, false, label);
        }

        private BlockState checkTargetBlock(Player plr) {
            @SuppressWarnings("deprecation")
            Block blk = plr.getTargetBlock(null, 100); //BUKKIT! HAVEN'T I TOLD YOU NOT TO DEPRECATED USEFUL STUFF?!
            if (blk == null || !(blk.getState() instanceof InventoryHolder)) {
                MTCHelper.sendLoc("XU-nodisp", plr, true);
                return null;
            }
            return blk.getState();
        }
    }
}
