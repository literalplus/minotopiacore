package io.github.xxyy.mtc.module.shop.ui.text.admin;

import com.google.common.base.Joiner;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

/**
 * Admin action for managing item aliases.
 *
 * @author Janmm14
 */
public class AliasAdminAction extends AbstractShopAction {
    private static final Joiner ALIASES_JOINER = Joiner.on("§7, §e");
    private static final Pattern ITEM_NAME_ALIAS_DELIMITER = Pattern.compile("|", Pattern.LITERAL);
    private final ShopModule module;

    protected AliasAdminAction(ShopModule module) {
        super("shopadmin", "alias", 2, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) { //REFACTOR
        String combined = StringHelper.varArgsString(args, 1, false);
        String[] split = ITEM_NAME_ALIAS_DELIMITER.split(combined);
        String itemName = split[0];
        ShopItem item = module.getItemManager().getItem(itemName);
        if (item == null) {
            plr.sendMessage("§cItem §6" + itemName + "§c nicht gefunden.");
            return;
        }
        String arg0 = args[0].toLowerCase();

        if (!arg0.equals("list") && split.length != 2) {
            plr.sendMessage("§cInvalide Syntax. Es muss genau ein§6 |§c verwendent werden.");
            return;
        }
        switch (arg0) {
            case "add": {
                item.getAliases().add(split[1]);
                saveAndMessage(plr, item, split[1], true);
                break;
            }
            case "addfirst": {
                item.getAliases().add(0, split[1]);
                saveAndMessage(plr, item, split[1], true);
                break;
            }
            case "remove": {
                item.getAliases().remove(split[1]);
                saveAndMessage(plr, item, split[1], false);
                break;
            }
            case "list": {
                plr.sendMessage("§6Aliases of §a" + item.getDisplayName() + "§6: §e" + ALIASES_JOINER.join(item.getAliases()));
                break;
            }
        }
    }

    private void saveAndMessage(Player plr, ShopItem item, String alias, boolean added) {
        module.getItemConfig().asyncSave(module.getPlugin());
        plr.sendMessage("§aItem §6" + item.getDisplayName() + "$a wurde Alias §6" + alias + "§a " + (added ? "hinzugefügt" : "entfernt") + ".");
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<add> <itemname> | <alias>", "Fügt einen Alias hinzu.");
        sendHelpLine(plr, "<addfirst> <itemname> | <alias>", "Fügt einen Alias am Anfang hinzu, dieser wird vom Shop zur Ausgabe verwendet.");
        sendHelpLine(plr, "<remove> <itemname> | <alias>", "Entfernt einen Alias.");
        sendHelpLine(plr, "<list> <itemname>", "Listet alle Aliases eines Items auf.");
    }
}
