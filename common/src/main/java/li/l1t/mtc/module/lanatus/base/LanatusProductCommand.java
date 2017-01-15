/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.base;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.product.Product;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.command.MTCExecutionExecutor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;

import java.util.Collection;
import java.util.UUID;

/**
 * Executes the /laprod command, used to show information about products.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-27
 */
class LanatusProductCommand extends MTCExecutionExecutor {
    private final LanatusClient client;

    LanatusProductCommand(LanatusClient client) {
        this.client = client;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            switch (exec.arg(0).toLowerCase()) {
                case "list":
                    showProductListWithFilter(exec, exec.findArg(1).orElse(""));
                    return true;
                case "info":
                    showProductDetails(exec, exec.uuidArg(1));
                    return true;
                default:
                    break;
            }
        }
        showUsage(exec);
        return true;
    }

    private void showProductListWithFilter(BukkitExecution exec, String filter) {
        Collection<Product> products = client.products().query()
                .inAnyModule()
                .containing(filter)
                .execute();
        if (products.isEmpty()) {
            exec.respond(MessageType.RESULT_LINE, "Keine Produkte gefunden.");
            return;
        }
        exec.respond(MessageType.LIST_HEADER, "%d Produkte gefunden:", products.size());
        products.forEach(product -> showProductListItem(exec, product));
    }

    private void showProductListItem(BukkitExecution exec, Product product) {
        exec.respond(resultLineBuilder()
                .append(product.getModule(), ChatColor.GREEN)
                .command("/laprod list " + product.getModule())
                .tooltip("Klicken, um alle Produkte\n aus diesem Modul anzuzeigen")
                .append(": ", ChatColor.GOLD, ComponentBuilder.FormatRetention.NONE)
                .append(product.getDisplayName(), ChatColor.GREEN)
                .append(" für ", ChatColor.GOLD)
                .append(product.getMelonsCost(), ChatColor.GREEN)
                .append(" Melonen ", ChatColor.GOLD)
                .append("[Details]", ChatColor.DARK_GREEN)
                .hintedCommand("/laprod info " + product.getUniqueId())
        );
    }

    private void showProductDetails(BukkitExecution exec, UUID uuid) {
        Product product = client.products().findById(uuid);
        exec.respond(MessageType.HEADER, "Produktinfo: %s", product.getDisplayName());
        exec.respond(MessageType.RESULT_LINE, "Beschreibung: §s%s", product.getDescription());
        exec.respond(MessageType.RESULT_LINE, "Icon: §s%s", formatMaterialName(product.getIconName()));
        exec.respond(MessageType.RESULT_LINE, "Kaufpreis: §s%d", product.getMelonsCost());
        exec.respond(MessageType.RESULT_LINE, "Modul: §s%s", product.getModule());
    }

    private String formatMaterialName(String iconName) {
        Material material = Material.matchMaterial(iconName);
        if (material == null) {
            return String.format("'%s' §c(unbekanntes Item)", iconName);
        } else {
            return material.name();
        }
    }

    private void showUsage(BukkitExecution exec) {
        exec.respondUsage("list", "[Suchbegriff]", "Listet alle Produkte auf.");
        exec.respondUsage("info", "<UUID>", "Zeigt ein Produkt.");
    }
}
