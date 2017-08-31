/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.shop.ui.text.admin;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.exception.UserException;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An admin action that adds an item to the shop.
 *
 * @author Janmm14, Literallie
 */
class AddAdminAction extends AbstractShopAction {
    private final Pattern UNESCAPED_COLON_PATTERN = Pattern.compile("(?<!\\\\):");
    private final ShopModule module;

    AddAdminAction(ShopModule module) {
        super("shopadmin", "add", 0, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) { //REFACTOR
        // /sa add <options>
        if (args.length > 0) {
            handleLegacyEscapes(args);
        }
        ItemStack itemInHand = plr.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            throw new UserException("Du hast nichts in der Hand.");
        }
        try {
            ShopItem item = module.getItemManager().createItem(itemInHand, args);
            module.getItemManager().registerItem(item);
            module.getItemConfig().asyncSave(module.getPlugin());
            plr.sendMessage("§aDas Item " + item.getDisplayName() + " wurde erfolgreich zum Shop hinzugefügt.");
            ComponentSender.sendTo(
                    new XyComponentBuilder("Nächste Schritte mit deinem Item: ", ChatColor.GOLD)
                            .append("[hier klicken]", ChatColor.DARK_GREEN, ChatColor.UNDERLINE)
                            .hintedCommand("/sa info " + item.getSerializationName()), plr
            );
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new UserException(e.getMessage(), e);
        }
    }

    private void handleLegacyEscapes(String[] args) {
        Matcher matcher = UNESCAPED_COLON_PATTERN.matcher(args[0]);
        if (matcher.find()) {
            throw new UserException("Die Verwendung dieses Befehls hat sich geändert. Jetzt wird das Item in deiner " +
                    "Hand herangezogen und die Argumente geben Spezialoptionen an. Falls du tatsächlich einen Doppelpunkt " +
                    "verwenden musst, gib stattdessen \\: an.");
        }
        parseEscapes(args);
    }

    private void parseEscapes(String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].replaceAll("\\\\:", ":");
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Parameter...>", "Fügt Item in deiner Hand dem Shop hinzu");
        sendHelpLine(plr, "", "Verwendet spezifischen Datenwert");
        sendHelpLine(plr, "wildcard", "Verwendet Item mit Wildcard-Datenwert");
    }
}
