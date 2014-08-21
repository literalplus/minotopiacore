package io.github.xxyy.mtc.listener;

import io.github.xxyy.mtc.MTC;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;

import java.util.List;


public class InfiniteDispenseListener implements Listener {
    @EventHandler
    public void onDispense(BlockDispenseEvent evt) {
        Block blk = evt.getBlock();
        List<MetadataValue> metaData = blk.getMetadata("mtc.infinite");
        metaData.stream()
                .filter(val -> val.getOwningPlugin().getName().equals(MTC.instance().getName()))
                .forEach(val -> ((InventoryHolder) blk.getState()).getInventory().addItem(evt.getItem()));
    }

    @EventHandler
    public void onHopper(InventoryMoveItemEvent evt) {
        InventoryHolder hldr = evt.getInitiator().getHolder();
        if (!(hldr instanceof BlockState)) {
            return;
        }
        BlockState blkState = (BlockState) hldr;
        List<MetadataValue> metaData = blkState.getMetadata("mtc.infinite");
        metaData.stream()
                .filter(val -> val.getOwningPlugin().getName().equals(MTC.instance().getName()))
                .forEach(val -> evt.getInitiator().addItem(evt.getItem()));
    }
}
