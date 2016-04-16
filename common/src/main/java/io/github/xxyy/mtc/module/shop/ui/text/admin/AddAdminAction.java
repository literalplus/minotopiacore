package io.github.xxyy.mtc.module.shop.ui.text.admin;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * An admin action that adds an item to the shop.
 *
 * @author Janmm14, xxyy
 */
class AddAdminAction extends AbstractShopAction {
    private final ShopModule module;

    AddAdminAction(ShopModule module) {
        super("shopadmin", "add", 1, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) { //REFACTOR
        // /sa add material_name:data
        String[] itemSpec = args[0].split(":");

        String materialDefinition = itemSpec[0];
        String dataValueInput = itemSpec.length == 1 ? "0" : itemSpec[1];
        Material material = Material.matchMaterial(materialDefinition);
        if (material == null) {
            plr.sendMessage("§cUnbekanntes Material: §6" + materialDefinition);
            return;
        }

        byte dataValue;
        try {
            dataValue = Byte.parseByte(dataValueInput);
        } catch (NumberFormatException ignore) {
            dataValue = -2;
        }

        if (dataValue < -1) {
            plr.sendMessage(String.format("§c§lFehler: §cDer Datenwert muss eine Zahl zwischen -1 und 127 sein. (%s)",
                    dataValueInput));
            return;
        }

        ShopItem specificItem = module.getItemManager().getItem(material, dataValue);
        if (specificItem != null) {
            plr.sendMessage(String.format("§c§lFehler: §cDieses Item (%s, %s) existiert bereits mit dem Namen %s.",
                    material, dataValueInput, specificItem.getDisplayName()));
            return;
        }

        ShopItem wildcardItem = module.getItemManager().getWildcardItem(material);
        if (wildcardItem != null) {
            plr.sendMessage(String.format("§e§lAchtung: §eEs existiert bereits ein Wildcard-Item für dieses Material. " +
                    "Dein Item mit dem Datenwert %s wird trotzdem funktionieren.", dataValueInput));
        }

        ShopItem createdItem = new ShopItem(
                module.getItemManager(),
                ShopItem.NOT_BUYABLE, ShopItem.NOT_SELLABLE,
                material, dataValue,
                new ArrayList<>(),
                ShopItem.NOT_DISCOUNTABLE
        );

        module.getItemConfig().storeItem(createdItem);
        module.getItemConfig().asyncSave(module.getPlugin());

        plr.sendMessage("§aDas Item " + createdItem.getDisplayName() + " wurde erfolgreich zum Shop hinzugefügt.");

        String aliasCmd = "/sa alias add " + createdItem.getMaterial() + ':' + createdItem.getDataValue() + " <alias>";

        ComponentSender.sendTo(
                new XyComponentBuilder("Setze als nächstes einen Anzeigenamen: ", ChatColor.GOLD)
                        .append("[hier klicken]", ChatColor.DARK_GREEN, ChatColor.UNDERLINE)
                        .suggest(aliasCmd), plr
        );
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Material[:Datenwert]>", "Fügt ein Item zum Shop hinzu.");
        plr.sendMessage("§7Datenwert -1 ('Wildcard') gilt für alle, Datenwert egal.");
        plr.sendMessage("§7Eine Wildcard überschreibt nicht existierende Datenwerte.");
        plr.sendMessage("§7Wenn nicht angegeben, wird 0 verwendet.");
    }
}
