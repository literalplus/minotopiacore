package io.github.xxyy.mtc.module.shop.ui.text.admin;

import com.google.common.base.Joiner;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ui.text.AbstractShopAction;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class AliasShopAdminAction extends AbstractShopAction {
    private static final Joiner SPACE_JOINER = Joiner.on(' ');
    private static final Pattern ITEM_NAME_ALIAS_DELIMITER = Pattern.compile("|", Pattern.LITERAL);
    private final ShopModule module;

    protected AliasShopAdminAction(ShopModule module) {
        super("shopadmin", "alias", 2, null);
        this.module = module;
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String combined = StringHelper.varArgsString(args, 1, false);
        String[] split = ITEM_NAME_ALIAS_DELIMITER.split(combined);
        String itemName = split[0];
        ShopItem item = module.getItemManager().getItem(itemName);
        if (item == null) {
            plr.sendMessage("§cItem §6" + itemName + "§c nicht gefunden.");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                if (split.length != 2) {
                    plr.sendMessage("§cInvalide Syntax. Genau ein §6|§c ist erlaubt.");
                    return;
                }
                item.getAliases().add(split[1]);
                module.getItemConfig().asyncSave(module.getPlugin());
                plr.sendMessage("§aItem §6" + item.getDisplayName() + "$a wurde Alias §6" + split[1] + "§a hinzugefügt.");
                break;
            }
            case "remove": {
                if (split.length != 2) {
                    plr.sendMessage("§cInvalide Syntax. Genau ein §6|§c ist erlaubt.");
                    return;
                }
                item.getAliases().remove(split[1]);
                module.getItemConfig().asyncSave(module.getPlugin());
                plr.sendMessage("§aItem §6" + item.getDisplayName() + "$a wurde Alias §6" + split[1] + "§a entfernt.");
                break;
            }
            case "list": {
                plr.sendMessage("§6Aliases of §a" + item.getDisplayName() + "§6: §a" + Joiner.on("§7, §a").join(item.getAliases()));
                break;
            }
        }
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<add> <itemname>|<alias>", "Fügt einen Alias hinzu. (Unterstützt Leerzeichen)");
        sendHelpLine(plr, "<remove> <itemname>|<alias>", "Entfernt einen Alias. (Unterstützt Leerzeichen)");
        sendHelpLine(plr, "<list> <itemname>", "Listet alle Aliases eines Items auf.");
    }
}
