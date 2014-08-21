package io.github.xxyy.mtc.fulltag;

import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.mtc.Const;
import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public final class FullTagHelper {
    public static Map<Integer, Material> parts = new HashMap<>(); //TODO enum

    private FullTagHelper() {

    }


    /**
     * on success, there will be two ints in the list (first one is TRUE, second FALSE)
     * else, there will be a single negative value indicating an error.
     */
    public static List<Integer> getBooleanValCounts(String column) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) {
            System.out.println("§cMTC: Tried to count boolean " + column + " before reload was complete.");
            return null;
        }
        ResultSet rs = sql.executeQuery("SELECT COUNT(*) AS cnt FROM " + sql.dbName + ".mtc_fulls GROUP BY " + column + " ORDER BY " + column + " DESC");
        if (rs == null) {
            System.out.println("rs==null -> DB down? (FullTagHelper.getBooleanValCounts(" + column + "))");
            return null;
        }
        try {
            List<Integer> lst = new ArrayList<>();
            if (!rs.isBeforeFirst()) {
                return null;
            }
            while (rs.next()) {
                lst.add(rs.getInt("cnt"));
            }
            return lst;
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "(FullTagHelper.getBooleanValCounts(" + column + "))");
        }
        return null;
    }

    /**
     * Gets a fully enchanted ItemStack.
     *
     * @param thorns         Add Thorns?
     * @param partId         ID of the requested part.
     * @param ignoreItemType Whether to add enchantments that cannnot normally be added to given item type.
     * @param sender         Who gives it away?
     * @param recName       Who receives it?
     * @param comment        Additional comment
     * @return Will return error message (String) OR ItemStack.
     *
     * @deprecated This method returns an Object, thus violating OOP principles.
     */
    @Deprecated
    public static Object getFull(boolean thorns, byte partId, boolean ignoreItemType, CommandSender sender, String recName, String comment) {
        ItemStack is = FullTagHelper.getStackByPartId(partId);
        if (is == null) {
            return "Dieser Full-Typ (" + partId + ") ist unbekannt!";
        }
        for (Enchantment enchantment : Enchantment.values()) {
            if ((thorns || !enchantment.equals(Enchantment.THORNS))
                    && (ignoreItemType || enchantment.canEnchantItem(is))) {
                is.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
            }
        }
        FullInfo fi = FullTagHelper.registerFull(is, sender, recName, comment, thorns, partId);
        if (fi.id < 0) {
            return "Das Item konnte nicht registriert werden! Fehler: " + fi.id;
        }
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(Const.FULL_LORE_PREFIX + fi.id);
        lore.add(Const.FULL_OWNER_LORE_PREFIX + fi.receiverName);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }

    public static int getFullId(ItemStack is) {
        if (is == null) {
            return -6;
        }
        if (!is.hasItemMeta()) {
            return -5;
        }
        List<String> lore = is.getItemMeta().getLore();
        if (lore == null) {
            return -3;
        }
        for (String str : lore) {
            if (str.startsWith(Const.FULL_LORE_PREFIX)) {
                str = str.replaceFirst(Const.FULL_LORE_PREFIX, "");
                if (!StringUtils.isNumeric(str)) {
                    return -2;
                }
                return Integer.parseInt(str);
            }
        }
        return -1;
    }

    public static List<FullInfo> getFullsWStringInRow(String plrName, String row) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) {
            System.out.println("Tried to get Fulls for player before reload was complete!");
            return null;
        }
        List<FullInfo> lst = new ArrayList<>();
        ResultSet rs = sql.safelyExecuteQuery("SELECT id FROM " + sql.dbName + ".mtc_fulls WHERE " + row + " = ?", plrName);
        if (rs == null) {
            return null;
        }
        try {
            if (!rs.isBeforeFirst()) {
                return lst;
            }
            while (rs.next()) {
                lst.add(FullInfo.getById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "FullTagHelper.getFullsForPlayer()");
            return null;
        }
        return lst;
    }

    public static Map<Integer, Integer> getPartCounts() {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) {
            System.out.println("FullTagHelper: Reload incomplete!");
            return null;
        }
        ResultSet rs = sql.executeQuery("SELECT part,COUNT(*) AS cnt FROM " + sql.dbName + ".mtc_fulls GROUP BY part ORDER BY COUNT(*) ASC");
        Map<Integer, Integer> map = new HashMap<>();
        int sum = 0;
        try {
            if (rs == null || !rs.isBeforeFirst()) {
                return null;
            }
            while (rs.next()) {
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

    public static ItemStack getStackByPartId(int partId) {
        if (FullTagHelper.parts.isEmpty()) {
            FullTagHelper.initParts();
        }
        return (FullTagHelper.parts.containsKey(partId) ? new ItemStack(FullTagHelper.parts.get(partId), 1) : null);
    }

    public static Map<String, Integer> getTop(String column, int perPage, int startIndex) {
        SafeSql sql = MTC.instance().ssql;
        if (sql == null) {
            System.out.println("§cMTC: Tried to get top " + column + " before reload was complete.");
            return null;
        }
        ResultSet rs = sql.executeQuery("SELECT " + column + " AS col,COUNT(*) AS cnt FROM " + sql.dbName + ".mtc_fulls GROUP BY " + column + " ORDER BY COUNT(*) DESC LIMIT " + startIndex + "," + (startIndex + perPage));
        if (rs == null) {
            System.out.println("rs==null -> DB down? (FullTagHelper.getTop(" + column + "))");
            return null;
        }
        try {
            Map<String, Integer> map = new HashMap<>();
            if (!rs.isBeforeFirst()) {
                return null;
            }
            while (rs.next()) {
                map.put(rs.getString("col"), rs.getInt("cnt"));
            }
            return map;
        } catch (SQLException e) {
            sql.formatAndPrintException(e, "(FullTagHelper.getTop(" + column + "))");
        }
        return null;
    }

    public static Map<String, Integer> getTopFullOwners(int startIndex) {
        return FullTagHelper.getTop("lastowner", 15, startIndex);
    }

    public static Map<String, Integer> getTopFullSenders(int startIndex) {
        return FullTagHelper.getTop("sender_name", 15, startIndex);
    }

    public static void initParts() {
        FullTagHelper.parts.put(0, Material.DIAMOND_CHESTPLATE); //REFACTOR enum
        FullTagHelper.parts.put(1, Material.DIAMOND_LEGGINGS);
        FullTagHelper.parts.put(2, Material.DIAMOND_BOOTS);
        FullTagHelper.parts.put(3, Material.DIAMOND_HELMET);
        FullTagHelper.parts.put(4, Material.DIAMOND_SWORD);
        System.out.println(MTCHelper.CSCollection(FullTagHelper.parts.keySet()));
    }

    public static boolean isFull(ItemStack is) {
        List<String> lore = is.getItemMeta().getLore();
        if (lore == null) {
            return false;
        }
        for (String str : lore) {
            if (str.startsWith(Const.FULL_LORE_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param is It is as it is.
     */
    public static FullInfo registerFull(ItemStack is, CommandSender sender, String recName, String comment, boolean thorns, byte partId) {
        FullInfo fi = FullInfo.create(sender.getName(), recName, comment, thorns, -1, -1, -1, "initialized", partId);
        LogHelper.getFullLogger().log(Level.WARNING, "Full registriert: " + fi.toString());
        return fi;
    }

    public static boolean scheduleFullForLater(boolean thorns, byte partId, boolean ignoreItemType, CommandSender sender, String recName, String comment, Inventory inv) {
        if (inv instanceof PlayerInventory) {
            Player receiver = Bukkit.getPlayerExact(recName);
            if (receiver != null) {
                receiver.sendMessage("§cBitte leere dein Inventar, um von " + sender.getName() + " ein full-enchantedes Item (" + FullInfo.getPartNameById(partId) + ") zu bekommen! §7(" +
                        "Nächster Versuch in 10 Sekunden)");
            }
            sender.sendMessage("§cDas Inventar von " + recName + " ist voll! Du wirst banachrichtigt, sobald " + FullInfo.getPartNameById(partId) + " abgeliefert wird.");
            Bukkit.getScheduler().runTaskLater(MTC.instance(), new RunnableGiveFullLater(thorns, partId, ignoreItemType,
                    sender, recName, comment, inv),/**/ 200);
            LogHelper.getFullLogger().log(Level.INFO, recName + "'s Inventory is full...trying again in 200t (" + sender.toString() + "->" + FullInfo.getPartNameById(partId));
        } else {
            sender.sendMessage("§cDieses Inventar ist voll!");
        }
        return false;
    }

    public static boolean tryGiveFull(boolean thorns, byte partId, boolean ignoreItemType, CommandSender sender, String recName, String comment, Inventory inv) {
        if (inv.firstEmpty() == -1) {
            return FullTagHelper.scheduleFullForLater(thorns, partId, ignoreItemType, sender, recName, comment, inv);
        }
        Object obj = FullTagHelper.getFull(thorns, partId, ignoreItemType, sender, recName, comment);
        if (obj == null) {
            obj = "Das Objekt ist NULL!";
        }
        if (obj instanceof String) {
            sender.sendMessage("§cFehler: §e" + obj + "§7 (" + FullInfo.getPartNameById(partId) + ")");
            return false;
        }
        ItemStack is = (ItemStack) obj;
        Map<Integer, ItemStack> map = inv.addItem(is);
        return map.isEmpty() || FullTagHelper.scheduleFullForLater(thorns, partId, ignoreItemType, sender, recName, comment, inv);
    }
}
