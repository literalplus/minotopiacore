package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.MTC;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;


public class InfiniteDispenseListener implements Listener
{
    @EventHandler
    public void onDispense(BlockDispenseEvent evt){
        Block blk = evt.getBlock();
        List<MetadataValue> metaData = blk.getMetadata("mtc.infinite");
        for(MetadataValue val : metaData){
            if(val.getOwningPlugin().getName().equals(MTC.instance().getName())){
                InventoryHolder hldr = (InventoryHolder)blk.getState();
                hldr.getInventory().addItem(evt.getItem());
            }
        }
    }
    
    @EventHandler
    public void onHopper(InventoryMoveItemEvent evt){
        InventoryHolder hldr = evt.getInitiator().getHolder();
        if(!(hldr instanceof BlockState)) return;
        BlockState blkState = (BlockState)hldr;
        List<MetadataValue> metaData = blkState.getMetadata("mtc.infinite");
        for(MetadataValue val : metaData){
            if(val.getOwningPlugin().getName().equals(MTC.instance().getName())){
                evt.getInitiator().addItem(evt.getItem());
            }
        }
    }
}
