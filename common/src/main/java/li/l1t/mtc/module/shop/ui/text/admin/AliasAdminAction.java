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
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.shop.ShopModule;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.ui.text.AbstractShopAction;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Admin action for managing item aliases.
 *
 * @author xxyy, Janmm14
 * @since 2016-03-26
 */
class AliasAdminAction extends AbstractShopAction {
    private final ShopModule module;

    AliasAdminAction(ShopModule module) {
        super("shopadmin", "alias", 2, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String itemSpec = args[1];
        String aliasSpec = StringHelper.varArgsString(args, 2, false);
        ShopItem item = module.getItemManager().getItem(plr, itemSpec).orElse(null);

        if (module.getTextOutput().checkNonExistant(plr, item, itemSpec)) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                aliasAdd(plr, aliasSpec, item);
                return;
            case "addfirst":
                aliasSetDisplayName(plr, aliasSpec, item);
                return;
            case "remove":
                aliasRemove(plr, aliasSpec, item);
                return;
            case "list":
                aliasList(plr, item);
                return;
            default:
                plr.sendMessage("§c§lFehler: §cUnbekannte Aktion " + args[0] + ".");
        }
    }

    private void aliasList(Player plr, ShopItem item) {
        ComponentSender.sendTo(
                new XyComponentBuilder(String.format("Aliases von %s (%s): ",
                        item.getDisplayName(), item.getSerializationName()), ChatColor.YELLOW)
                        .append("[+]", ChatColor.DARK_GREEN, ChatColor.UNDERLINE)
                        .suggest("/sa alias add " + item.getSerializationName() + " "),
                plr
        );

        List<String> aliases = item.getAliases(); //creates new immutable collection on every call
        for (int i = 0; i < aliases.size(); i++) {
            String alias = aliases.get(i);
            ComponentSender.sendTo(
                    new XyComponentBuilder(" -> ", ChatColor.DARK_GREEN)
                            .append("#" + i + ": ", ChatColor.YELLOW)
                            .append(alias, ChatColor.GREEN)
                            .append((i == 0 ? "(Anzeigename) " : " "), ChatColor.YELLOW)
                            .append("[-]", ChatColor.DARK_RED, ChatColor.UNDERLINE)
                            .hintedCommand("/sa alias remove " + item.getSerializationName() + " " + alias),
                    plr
            );
        }
    }

    private void aliasRemove(Player plr, String aliasSpec, ShopItem item) {
        String removedAlias = aliasSpec;
        if (StringUtils.isNumeric(aliasSpec)) {
            try {
                removedAlias = item.removeAlias(Integer.parseInt(aliasSpec));
            } catch (IndexOutOfBoundsException ioobe) {
                plr.sendMessage("§c§lFehler: §cKein Alias mit der Id " + aliasSpec + ".");
                return;
            }
        } else {
            item.removeAlias(aliasSpec);
        }
        module.getItemConfig().updateItem(item);
        plr.sendMessage("§aAlias §2" + removedAlias + "§a entfernt.");
    }

    private void aliasSetDisplayName(Player plr, String aliasSpec, ShopItem item) {
        item.setDisplayName(aliasSpec);
        module.getItemConfig().updateItem(item);
        plr.sendMessage(String.format("§aDer Anzeigename von %s ist jetzt $2%s§a.",
                item.getSerializationName(), item.getDisplayName()));
    }

    private void aliasAdd(Player plr, String aliasSpec, ShopItem item) {
        item.addAlias(aliasSpec);
        module.getItemConfig().updateItem(item);
        plr.sendMessage(String.format("§aAlias §2%s§a zu %s hinzugefügt.", aliasSpec, item.getDisplayName()));
    }


    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "add <Item> <Alias>", "Fügt einen Alias hinzu");
        sendHelpLine(plr, "addfirst <Item> <Alias>", "Setzt den Anzeigenamen");
        sendHelpLine(plr, "remove <Item> <Alias>", "Entfernt einen Alias");
        sendHelpLine(plr, "remove <Item> <Id>", "Entfernt einen Alias nach seiner Id");
        sendHelpLine(plr, "list <item>", "Listet alle Aliases auf");
    }
}
