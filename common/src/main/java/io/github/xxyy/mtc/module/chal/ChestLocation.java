/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.chal;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.util.inventory.InventoryHelper;
import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.helper.MTCHelper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stores the location of a chest for Chal, including metadata.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30/11/14
 */
public class ChestLocation extends XyLocation {
    private ChalDate date;

    public ChestLocation(Location toClone, ChalDate date) {
        super(toClone);
        this.date = date;
    }

    public ChalDate getDate() {
        return date;
    }

    public void setDate(ChalDate date) {
        this.date = date;
    }

    public boolean openableBy(Player plr) {
        return plr.hasPermission(ChalModule.DATE_BYPASS_PERMISSION) || date.is(LocalDate.now()) ||
                (date.before(LocalDate.now()) && plr.hasPermission(ChalModule.DATE_BYPASS_PAST_PERMISSION));
    }

    public boolean openFor(Player plr) {
        if (!openableBy(plr)) {
            return !MTCHelper.sendLocArgs("XU-chopendenied", plr, false, date.toReadable());
        }

        if (getBlock() == null || getBlock().getType() != Material.CHEST) {
            plr.sendMessage(String.format("§cInterner Fehler. Melde dies bitte. (Tür #%d)", date.getDay()));
            return false;
        }

        Chest chest = (Chest) getBlock().getState();
        ItemStack[] contents = InventoryHelper.cloneAll(chest.getBlockInventory().getContents());
        List<String> lore = Arrays.asList("§eAdventskalender Tür #" + date.getDay(), "§6geöffnet von "+plr.getName());
        contents = (ItemStack[]) Arrays.asList(contents).stream()
                .map(ItemStackFactory::new)
                .map(isf -> isf.appendLore(lore))
                .map(ItemStackFactory::produce)
                .collect(Collectors.toList()).toArray();
        plr.getInventory().addItem(contents);
        MTCHelper.sendLocArgs("XU-chopened", plr, false, date.getDay());

        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("date", date.serialize());
        return result;
    }

    public static ChestLocation deserialize(Map<String, Object> input) {
        Validate.isTrue(input.containsKey("date"), "Missing date!");
        ChalDate date = ChalDate.deserialize(input.get("date").toString());
        return new ChestLocation(XyLocation.deserialize(input), date);
    }
}
