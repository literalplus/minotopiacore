/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.fulltag;

import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;


public final class FullTagListener implements Listener {
    @EventHandler(priority=EventPriority.LOWEST)
    public void onCombust(EntityCombustEvent e){
        if(!e.getEntityType().equals(EntityType.DROPPED_ITEM)) {
            return;
        }
        Item item = ((Item)e.getEntity());
        int id = FullTagHelper.getFullId(item.getItemStack());
        if(id < 0) {
            return;
        }
        FullInfo fi = FullInfo.getById(id);
        if(fi.id < 0) {
            return;
        }
        fi.nullify();
        LogHelper.getFullLogger().warning("Full died & deleted. loc="+MTCHelper.locToShortString(item.getLocation())+",fi="+fi);
    }
    @EventHandler(priority=EventPriority.LOWEST)
    public void onDespawn(ItemDespawnEvent e){
        int id = FullTagHelper.getFullId(e.getEntity().getItemStack());
        if(id > 0){
            FullInfo fi = FullInfo.getById(id);
            if(fi.id < 0){ e.setCancelled(true); return; } //safety 
            fi.nullify();
            LogHelper.getFullLogger().warning("Full despawned & deleted. loc="+MTCHelper.locToShortString(e.getLocation())+",fi="+fi);
        }
    }
    @EventHandler(priority=EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent e){
//        if(!e.getPlayer().hasPermission("mtc.ignore")) return;
        ItemStack is = e.getItemDrop().getItemStack();
        String plrName = e.getPlayer().getName();
        int id = FullTagHelper.getFullId(is);
        if(id < 0) {
            return;//no full
        }
        FullInfo fi = FullInfo.getById(id);
        if(FullTagListener.doLogic(id, fi, null, plrName, "drop_at_"+MTCHelper.locToShortString(e.getPlayer().getLocation())+"_by_"+plrName,e.getPlayer().getLocation())){
            e.getItemDrop().remove();
            e.getPlayer().sendMessage(MTC.chatPrefix+"§cDie Full, die du gedroppt hast, ist unbekannt und wurde daher entfernt. §eFür Beschwerden notiere die bitte unbedingt die aktuelle Uhrzeit!");
            LogHelper.getFullLogger().info("Dropped Full at "+e.getItemDrop().getLocation().toString()+" REMOVED! Player: "+e.getPlayer());
        }
    }
    @EventHandler(priority=EventPriority.LOWEST)
    public void onInvClick(InventoryClickEvent e){
        HumanEntity who = e.getWhoClicked();
        Inventory inv = e.getInventory();
        switch(e.getAction()){
        case NOTHING: case UNKNOWN:
            return;
        case CLONE_STACK:
            int id = FullTagHelper.getFullId(e.getCurrentItem());
            if(id < 0) {
                return;
            }
            e.setCancelled(true);
            if(who instanceof Player) {
                ((Player)who).sendMessage(MTC.chatPrefix+"§cFulls werden nicht dupliziert!");
            }
            return;
        default:
        }
        switch(e.getSlotType()){
        case CRAFTING:
        case FUEL:
//        case OUTSIDE:
//        case QUICKBAR:
        case RESULT:
            return;
        default:
            if(inv.getType() == InventoryType.CREATIVE || inv.getType() == InventoryType.PLAYER) {
                return;
            }
            if(FullTagListener.doLogic(e,inv,"action_"+e.getAction()+"_"+e.getSlotType().name()+"_open_"
                    +inv.getType()+"_at_"+MTCHelper.locToShortString(who.getLocation())+"_by_"+who.getName())){
                if(e.getCursor() != null) {
                    e.getCursor().setAmount(0);
                }
                if(e.getCurrentItem() != null) {
                    e.getCurrentItem().setAmount(0);
                }
                if(who instanceof Player) {
                    ((Player)who).sendMessage(MTC.chatPrefix+"§cDie Full, die du angeklickt hast, ist unbekannt und wurde daher entfernt. §eFür Beschwerden notiere die bitte unbedingt die aktuelle Uhrzeit!");
                }
                LogHelper.getFullLogger().info("Moved Full at "+who.getLocation().toString()+" REMOVED! Player: "+who);
            }
        }
    }
    @EventHandler(priority=EventPriority.LOWEST)
    public void onPickup(PlayerPickupItemEvent e){
//        if(!e.getPlayer().hasPermission("mtc.ignore")) return;
        ItemStack is = e.getItem().getItemStack();
        String plrName = e.getPlayer().getName();
        int id = FullTagHelper.getFullId(is);
        if(id < 0) {
            return;//no full
        }
        FullInfo fi = FullInfo.getById(id);
        if(FullTagListener.doLogic(id, fi, null, plrName, "pickup_at_"+MTCHelper.locToShortString(e.getPlayer().getLocation())+"_by_"+plrName,e.getPlayer().getLocation())){
            e.getPlayer().sendMessage("§cDieses Fullteil ist unbekannt. Daher wurde es entfernt. §eFür Beschwerden notiere dir unbedingt die aktuelle Uhrzeit.");
            e.getItem().remove();
            LogHelper.getFullLogger().info("Picked up Full at "+e.getPlayer().getLocation()+" REMOVED! plr: "+e.getPlayer());
            e.setCancelled(true);
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static boolean doLogic(int id, FullInfo fi, Inventory inv, String plrName, String action, Location loc){
        if(fi.id == -10){
            LogHelper.getFullLogger().severe("!!! CAUGHT UNKOWN FULL ID="+id+" AT "+plrName+" AT "+loc.toString()+" !!!");
            return ConfigHelper.getRemoveUnknownFulls();
        }
        if(fi.id < 0) {
            return false;
        }
        fi.lastseen = (Calendar.getInstance().getTimeInMillis() / 1000);
        fi.lastCode = action;
        if(inv != null) { fi.inEnderchest = inv.getType() == InventoryType.ENDER_CHEST; }
        if(loc != null){
            fi.x = loc.getBlockX();
            fi.y = loc.getBlockY();
            fi.z = loc.getBlockZ();
        }
        fi.lastOwnerName = plrName;
        fi.flush();
        LogHelper.getFullLogger().fine("Caught Full: "+fi.toLogString()+" at player: "+plrName);
        return false;
    }
    public static boolean doLogic(InventoryClickEvent e, Inventory inv, String action){
        return FullTagListener.doLogic(e.getCursor(), inv, e.getWhoClicked().getName(),action,e.getWhoClicked().getLocation());
    }
    /**
     * @return If the full has no right to exist and should therefore be removed.
     */
    public static boolean doLogic(ItemStack is, Inventory inv, String plrName, String action, Location loc){
        int id = FullTagHelper.getFullId(is);
      if(id < 0) {
          return false;
      }
      if(is.getAmount() > 1){
          is.setAmount(1);
          LogHelper.getFullLogger().info("Set amount to 1 at plr: "+plrName);
      }
      FullInfo fi = FullInfo.getById(id);
      return FullTagListener.doLogic(id, fi, inv, plrName, action, loc);
    }
}
