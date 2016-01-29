package io.github.xxyy.mtc.module.shop.ui.text.admin;

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
public class AddAdminAction extends AbstractShopAction {

    private final ShopModule module;

    protected AddAdminAction(ShopModule module) {
        super("shopadmin", "add", 1, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) { //REFACTOR
        String[] split = args[0].split(":");

        String itemIdStr = split[0];
        String dataStr = split.length == 1 ? "0" : split[1];
        Material material = Material.matchMaterial(itemIdStr);
        if (material == null) {
            plr.sendMessage("§cDas Material §6" + itemIdStr + "§c ist nicht bekannt.");
            return;
        }
        byte dataValue;
        try {
            dataValue = Byte.parseByte(dataStr);
        } catch (NumberFormatException ignore) {
            plr.sendMessage(String.format("§cData muss eine Zahl zwischen -1 und 127 sein - nicht §e%s§c.", dataStr));
            return;
        }
        if (dataValue < -1) {
            plr.sendMessage("§cData muss zwischen -1 und 127 liegen.");
            return;
        }

//        ShopItem wildcardItem = module.getItemManager().getItem(material, (byte) -1); //allow for default price - FIXME: data values with wildcards are probably not carried
//        if (wildcardItem != null) {
//            plr.sendMessage("§cEs existiert bereits ein Wildcard-ShopItem mit dem Material."); //TO DO do we allow wildcard & specific ShopItems of same material currently?
//            return;
//        }
        ShopItem specificItem = module.getItemManager().getItem(material, dataValue);
        if (specificItem != null) {
            plr.sendMessage(String.format("§cFür dieses Material und diesen Datenwert gibt es bereits das Item §e%s§c.",
                    specificItem.getDisplayName()));
            return;
        }

        ShopItem newItem = new ShopItem(module.getItemManager(), -1, -1, material, dataValue, new ArrayList<>(), 0);
        module.getItemConfig().storeItem(newItem);
        module.getItemConfig().asyncSave(module.getPlugin());
        plr.sendMessage("§aDas Item " + newItem.getDisplayName() + " wurde erfolgreich zum Shop hinzugefügt.");
        String aliasCmd = "/sa alias add " + newItem.getMaterial() + ':' + newItem.getDataValue() + " <alias>";
        plr.spigot().sendMessage(
                new XyComponentBuilder("Setze jetzt einen Anzeigenamen: ", ChatColor.GOLD)
                        .append(aliasCmd, ChatColor.RED)
                        .suggest(aliasCmd)
                        .create());
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "[<ID>|<Material[:data]>]", "Fügt ein Item zum Shop hinzu.");
        plr.sendMessage("§6Data = -1 bedeutet jeder Wert (Wildcard). Standard ist null.");
    }
}
