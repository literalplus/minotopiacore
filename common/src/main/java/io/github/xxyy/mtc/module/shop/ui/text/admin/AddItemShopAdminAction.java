package io.github.xxyy.mtc.module.shop.ui.text.admin;

import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AddItemShopAdminAction extends AbstractShopAction {

    private final ShopModule module;

    protected AddItemShopAdminAction(ShopModule module) {
        super("shopadmin", "add", 1, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String[] split = args[0].split(":");

        String itemIdStr = split[0];
        String dataStr = split.length == 1 ? "0" : split[1];
        Material material = Material.matchMaterial(itemIdStr);
        if (material == null) {
            plr.sendMessage("§cDas Material §6" + itemIdStr + "§c ist nicht bekannt.");
            return;
        }
        try {
            int data = Integer.parseInt(dataStr);
            if (data < 0 || data > Byte.MAX_VALUE) {
                plr.sendMessage("§cData muss zwischen 0 und " + Byte.MAX_VALUE + " liegen.");
                return;
            }
            byte dataB = (byte) data;
            ShopItem wildcardItem = module.getItemManager().getItem(material, (byte) -1);
            if (wildcardItem != null) {
                plr.sendMessage("§cEs existiert bereits ein Wildcard-ShopItem mit dem Material."); //TODO do we allow wildcard & specific ShopItems of same material currently?
                return;
            }
            ShopItem specificItem = module.getItemManager().getItem(material, dataB);
            if (specificItem != null) {
                plr.sendMessage("§cEs existiert bereits ein ShopItem mit dem gegebenen Material und dem entsprechenden data-Wert.");
                return;
            }
            ShopItem newItem = new ShopItem(-1, -1, material, dataB, new ArrayList<>());
            module.getItemConfig().storeItem(newItem);
            module.getItemConfig().asyncSave(module.getPlugin());
            plr.sendMessage("§aDas Item " + newItem.getDisplayName() + " wurde erfolgreich zum Shop hinzugefügt.");
            String aliasCmd = "/sa alias add " + newItem.getMaterial() + ':' + newItem.getDataValue() + " <alias>";
            plr.spigot().sendMessage(
                new XyComponentBuilder("Setze jetzt einen Displaynamen: ", ChatColor.GOLD)
                    .append(aliasCmd, ChatColor.RED)
                    .suggest(aliasCmd)
                    .create());
        } catch (NumberFormatException ignored) {
            plr.sendMessage("§cData muss eine Zahl sein und nicht §6" + dataStr);
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<itemid/itemname[:data]>", "Fügt das Item zum Shop hinzu.");
        plr.sendMessage("§6Setze data auf -1 für jeden beliebigen data-Wert (Wildcard). Standard ist 0.");
    }
}
