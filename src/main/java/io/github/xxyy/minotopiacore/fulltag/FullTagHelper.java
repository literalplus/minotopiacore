package io.github.xxyy.minotopiacore.fulltag;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.Const;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;


public class FullTagHelper {
    public static HashMap<Integer, Material> parts = new HashMap<>();
    /**
	 * on success, there will be two ints in the list (first one is TRUE, second FALSE)
	 * else, there will be a single negative value indicating an error.
	 */
	public static List<Integer> getBooleanValCounts(String column){
	    SafeSql sql = MTC.instance().ssql;
        if(sql == null){
            System.out.println("§cMTC: Tried to count boolean "+column+" before reload was complete.");
            return null;
        }
        ResultSet rs = sql.executeQuery("SELECT COUNT(*) AS cnt FROM "+sql.dbName+".mtc_fulls GROUP BY "+column+" ORDER BY "+column+" DESC");
        if(rs == null){
            System.out.println("rs==null -> DB down? (FullTagHelper.getBooleanValCounts("+column+"))");
            return null;
        }
        try {
            List<Integer> lst = new ArrayList<>();
            if(!rs.isBeforeFirst()) return null;
            while(rs.next()){
                lst.add(rs.getInt("cnt"));
            }
            return lst;
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "(FullTagHelper.getBooleanValCounts("+column+"))");
        }
        return null;
	}
    /**
     * Gets a fully enchanted ItemStack.
     * @param thorns Add Thorns?
     * @param partId 
     * @param ignoreItemType 
     * @param sender Who gives it away?
     * @param receiver Who receives it?
     * @param comment Additional comment
     * @return Will return error message (String) OR ItemStack.
     * @author xxyy98<xxyy98@gmail.com
     */
    public static Object getFull(boolean thorns,byte partId,boolean ignoreItemType, CommandSender sender, String recName,String comment){
		ItemStack is = FullTagHelper.getStackByPartId(partId);
		if(is == null) return "Dieser Full-Typ ("+partId+") ist unbekannt!";
		for(Enchantment enchantment : Enchantment.values()){
			if((thorns || !enchantment.equals(Enchantment.THORNS))
					&& (ignoreItemType || enchantment.canEnchantItem(is))){
				is.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
			}
		}
		FullInfo fi = FullTagHelper.registerFull(is, sender, recName, comment, thorns, partId);
		if(fi.id < 0) return "Das Item konnte nicht registriert werden! Fehler: "+fi.id;
		ItemMeta meta = is.getItemMeta();
		List<String> lore = meta.getLore();
        if(lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(Const.fullLorePrefix+fi.id);
        lore.add(Const.fullOwnerLorePrefix+fi.receiverName);
        meta.setLore(lore);
        is.setItemMeta(meta);
		return is;
	}
	public static int getFullId(ItemStack is){
	    if(is == null) return -6;
	    if(!is.hasItemMeta()) return -5;
	    List<String> lore = is.getItemMeta().getLore();
        if(lore == null) return -3;
        for(String str : lore){
            if(str.startsWith(Const.fullLorePrefix)){
                str = str.replaceFirst(Const.fullLorePrefix, "");
                if(!StringUtils.isNumeric(str)) return -2;
                return Integer.parseInt(str);
            }
        }
        return -1;
	}
	
	public static List<FullInfo> getFullsWStringInRow(String plrName,String row){
	    SafeSql sql = MTC.instance().ssql;
	    if(sql == null){
	        System.out.println("Tried to get Fulls for player before reload was complete!");
	        return null;
	    }
	    List<FullInfo> lst = new ArrayList<>();
	    ResultSet rs = sql.safelyExecuteQuery("SELECT id FROM "+sql.dbName+".mtc_fulls WHERE "+row+" = ?", plrName);
	    if(rs == null) return null;
	    try {
            if(!rs.isBeforeFirst()) return lst;
            while(rs.next()) {
                lst.add(FullInfo.getById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "FullTagHelper.getFullsForPlayer()"); return null;
        }
	    return lst;
	}
	
	/**
	 * 
	 * @return HashMap<PARTID, COUNT>
	 * @author xxyy98<xxyy98@gmail.com
	 */
	public static HashMap<Integer, Integer> getPartCounts(){
	    SafeSql sql = MTC.instance().ssql;
	    if(sql == null){
	        System.out.println("FullTagHelper: Reload incomplete!");
	        return null;
	    }
	    ResultSet rs = sql.executeQuery("SELECT part,COUNT(*) AS cnt FROM "+sql.dbName+".mtc_fulls GROUP BY part ORDER BY COUNT(*) ASC");
	    HashMap<Integer, Integer> map = new HashMap<>();
        int sum = 0;
	    try {
            if(rs == null || !rs.isBeforeFirst()) return null;
            while(rs.next()){
                int sth = rs.getInt("cnt");
                sum += sth;
                map.put(rs.getInt("part"), sth);
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "FullTagHelper.getPartCounts()");
            return null;
        }
	    map.put(-1, sum);
	    return map;
	}
	
	public static ItemStack getStackByPartId(int partId){
	    if(FullTagHelper.parts.isEmpty()) {
            FullTagHelper.initParts();
        }
	    return (FullTagHelper.parts.containsKey(partId) ? new ItemStack(FullTagHelper.parts.get(partId),1) : null);
	}
	
	public static HashMap<String,Integer> getTop(String column, int perPage, int startIndex){
	    SafeSql sql = MTC.instance().ssql;
        if(sql == null){
            System.out.println("§cMTC: Tried to get top "+column+" before reload was complete.");
            return null;
        }
        ResultSet rs = sql.executeQuery("SELECT "+column+" AS col,COUNT(*) AS cnt FROM "+sql.dbName+".mtc_fulls GROUP BY "+column+" ORDER BY COUNT(*) DESC LIMIT "+startIndex+","+(startIndex + perPage));
        if(rs == null){
            System.out.println("rs==null -> DB down? (FullTagHelper.getTop("+column+"))");
            return null;
        }
        try {
            HashMap<String, Integer> map = new HashMap<>();
            if(!rs.isBeforeFirst()) return null;
            while(rs.next()){
                map.put(rs.getString("col"), rs.getInt("cnt"));
            }
            return map;
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "(FullTagHelper.getTop("+column+"))");
        }
        return null;
	}
	
	public static HashMap<String, Integer> getTopFullOwners(int startIndex){
	    return FullTagHelper.getTop("lastowner",15,startIndex);
	}
	
	public static HashMap<String, Integer> getTopFullSenders(int startIndex){
        return FullTagHelper.getTop("sender_name",15,startIndex);
    }
	
	public static void initParts(){
        FullTagHelper.parts.put(0, Material.DIAMOND_CHESTPLATE); //REFACTOR enum
        FullTagHelper.parts.put(1, Material.DIAMOND_LEGGINGS);
        FullTagHelper.parts.put(2, Material.DIAMOND_BOOTS);
        FullTagHelper.parts.put(3, Material.DIAMOND_HELMET);
        FullTagHelper.parts.put(4, Material.DIAMOND_SWORD);
        System.out.println(MTCHelper.CSCollection(FullTagHelper.parts.keySet()));
    }
	
	public static boolean isFull(ItemStack is){
		List<String> lore = is.getItemMeta().getLore();
		if(lore == null) return false;
		for(String str : lore){
			if(str.startsWith(Const.fullLorePrefix)) return true;
		}
		return false;
	}
	/**
     * @param is  It is as it is.
     */
	public static FullInfo registerFull(ItemStack is, CommandSender sender, String recName,String comment, boolean thorns, byte partId){
		FullInfo fi = FullInfo.create(sender.getName(), recName, comment, thorns, -1, -1, -1, "initialized", partId);
		LogHelper.getFullLogger().log(Level.WARNING, "Full registriert: "+fi.toString());
		return fi;
	}
	public static boolean scheduleFullForLater(boolean thorns,byte partId,boolean ignoreItemType, CommandSender sender, String recName,String comment, Inventory inv){
	     if(inv instanceof PlayerInventory){
	         Player receiver = Bukkit.getPlayerExact(recName);
	         if(receiver != null) {
                receiver.sendMessage("§cBitte leere dein Inventar, um von "+sender.getName()+" ein full-enchantedes Item ("+FullInfo.getPartNameById(partId)+") zu bekommen! §7(" +
                         "Nächster Versuch in 10 Sekunden)");
            }
             sender.sendMessage("§cDas Inventar von "+recName+" ist voll! Du wirst banachrichtigt, sobald "+FullInfo.getPartNameById(partId)+" abgeliefert wird.");
             Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableGiveFullLater(thorns, partId, ignoreItemType,
                    sender, recName, comment, inv),/**/ 200);
             LogHelper.getFullLogger().log(Level.INFO, recName+"'s Inventory is full...trying again in 200t ("+sender.toString()+"->"+FullInfo.getPartNameById(partId));
        }else{
           sender.sendMessage("§cDieses Inventar ist voll!");
        }
        return false;
	}
	public static boolean tryGiveFull(boolean thorns,byte partId,boolean ignoreItemType, CommandSender sender, String recName,String comment, Inventory inv){
        if(inv.firstEmpty() == -1) return FullTagHelper.scheduleFullForLater(thorns, partId, ignoreItemType, sender, recName, comment, inv);
        Object obj = FullTagHelper.getFull(thorns, partId, ignoreItemType, sender, recName, comment);
        if(obj == null){
            obj = "Das Objekt ist NULL!";
        }
        if(obj instanceof String){
            sender.sendMessage("§cFehler: §e"+obj+"§7 ("+FullInfo.getPartNameById(partId)+")");
            return false;
        }
        ItemStack is = (ItemStack)obj;
        HashMap<Integer, ItemStack> map = inv.addItem(is);
        if(!map.isEmpty()) return FullTagHelper.scheduleFullForLater(thorns, partId, ignoreItemType, sender, recName, comment, inv);
        return true;
    }
}
