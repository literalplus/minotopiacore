/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop.ui.text.admin;

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.checklist.Checklist;
import li.l1t.common.checklist.ChecklistEvaluator;
import li.l1t.common.checklist.renderer.CheckmarkBasedRenderer;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.shop.ShopItem;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.ShopPriceCalculator;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import li.l1t.mtc.module.shop.ui.text.ShopTextOutput;
import li.l1t.mtc.util.checklist.ClickableChecklistItem;
import li.l1t.mtc.util.checklist.LinkChecklistRenderer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles the price action for the shop command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
class InfoAdminAction extends AbstractShopAction {
    private final ShopModule module;
    private final ShopTextOutput output;
    private final ShopPriceCalculator calculator;

    InfoAdminAction(ShopModule module) {
        super("shopadmin", "info", 1, null);
        this.module = module;
        output = module.getTextOutput();
        calculator = new ShopPriceCalculator(module.getItemManager());
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        switch (args[0].toLowerCase()) {
            case "hand":
                infoHand(plr);
                break;
            default:
                priceNamedItem(args, plr);
                break;
        }
    }

    private void priceNamedItem(String[] args, Player plr) {
        String name = StringHelper.varArgsString(args, 0, false);
        ShopItem item = module.getItemManager().getItem(name);
        sendInfo(plr, item, name);
    }

    private void infoHand(Player plr) {
        ItemStack itemInHand = plr.getItemInHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            output.sendPrefixed(plr, "§cDu hast nichts in der Hand!");
            return;
        }

        ShopItem item = module.getItemManager().getItem(itemInHand);
        sendInfo(plr, item, itemInHand.getType() + ":" + itemInHand.getDurability());
    }

    private void sendInfo(Player plr, ShopItem item, String spec) {
        if (item == null) {
            short data = 0;
            String[] specSplit = spec.split(":");
            if (specSplit.length == 2) {
                if (StringUtils.isNumeric(specSplit[1])) {
                    data = Short.parseShort(specSplit[1]);
                } else {
                    plr.sendMessage(String.format(
                            "§cDas nach dem Doppelpunkt muss eine Zahl sein, ist aber §6%s§c.",
                            specSplit[1])
                    );
                    return;
                }
            }
            Material material = Material.matchMaterial(specSplit[0]);
            sendNonExistant(plr, material, data, spec);
            return;
        }

        plr.sendMessage("§e »»»  Shopitem: " + item.getDisplayName() + "  «««");
        Checklist checklist = new Checklist();
        checklist.append("im Shop", () -> true);
        checklist.append(item(
                "Anzeigename", () -> !item.getAliases().isEmpty(),
                "/sa alias add " + item.getSerializationName() + " "
        ));
        checklist.append(item(
                "kaufbar", item::canBeBought, "/sa cost <> " + item.getSerializationName()
        ));
        checklist.append(item(
                "verkaufbar", item::canBeSold, "/sa worth <> " + item.getSerializationName()
        ));
        checklist.append(item(
                "reduzierbar", item::isDiscountable, "/sa discount " + item.getSerializationName() + " "
        ));

        new LinkChecklistRenderer(
                new CheckmarkBasedRenderer.Builder().build()
        ).renderTo(plr, checklist);
        plr.sendMessage("§7Datenwert: -1 ... Wildcard, 0 ... Standard");
        plr.sendMessage("§7Preise: 0 ... nicht (ver)käuflich/reduzierbar");
    }

    private ClickableChecklistItem item(String label, ChecklistEvaluator eval, String command) {
        return new ClickableChecklistItem(label, eval)
                .withButtonCaption("setzen")
                .withAction(ClickEvent.Action.SUGGEST_COMMAND)
                .withArgument(command);
    }

    private void sendNonExistant(Player plr, Material material, short data, String spec) {
        if (material == null) {
            plr.spigot().sendMessage(
                    new XyComponentBuilder("Das Item ", ChatColor.RED)
                            .append(spec, ChatColor.DARK_RED)
                            .append(" ist nicht im Shop und auch Minecraft nicht bekannt. ", ChatColor.RED)
                            .append("[Neues Item]", ChatColor.GREEN, ChatColor.UNDERLINE)
                            .suggest("/sa add")
                            .create()
            );
        } else {
            plr.spigot().sendMessage(
                    new XyComponentBuilder("Das Item ", ChatColor.YELLOW)
                            .append(spec, ChatColor.GOLD)
                            .append(" ist nicht im Shop, kann aber einfach hinzugefügt werden: ", ChatColor.YELLOW)
                            .append("[Neues Item]", ChatColor.GREEN, ChatColor.UNDERLINE)
                            .suggest(String.format("/sa add %s:%s", material, data))
                            .tooltip("Hier klicken zum Hinzufügen")
                            .create()
            );
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Item>", "Zeigt Checkliste zum Item");
        sendHelpLine(plr, "hand", "Zeigt Checkliste zum Item in deiner Hand");
    }
}
