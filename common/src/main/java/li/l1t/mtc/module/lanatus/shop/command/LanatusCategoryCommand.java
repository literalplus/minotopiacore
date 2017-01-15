/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.command;

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.module.lanatus.shop.LanatusShopModule;
import li.l1t.mtc.module.lanatus.shop.category.SqlCategory;
import li.l1t.mtc.module.lanatus.shop.gui.ProductSelectionMenu;
import net.md_5.bungee.api.ChatColor;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Executes the /lacat command, which allows to manage Lanatus categories.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-22-11
 */
public class LanatusCategoryCommand extends MTCExecutionExecutor {
    private final LanatusShopModule module;

    public LanatusCategoryCommand(LanatusShopModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasNoArgs() || exec.arg(0).equalsIgnoreCase("help")) {
            respondUsage(exec);
            return true;
        }
        switch (exec.arg(0)) {
            case "info":
                return handleInfo(exec, category(exec.uuidArg(1)));
            case "display":
                return handleDisplayName(exec, category(exec.uuidArg(1)), exec.joinedArgsColored(2));
            case "icon":
                return handleIcon(exec, category(exec.uuidArg(1)), exec.arg(2));
            case "desc":
                return handleDescriptionSet(exec, category(exec.uuidArg(1)), exec.joinedArgsColored(2));
            case "descadd":
                return handleDescriptionAdd(exec, category(exec.uuidArg(1)), exec.joinedArgsColored(2));
            case "list":
                return handleList(exec, exec.findArg(1).orElse(""));
            case "prodadd":
                return handleProductAdd(exec, category(exec.uuidArg(1)));
            case "prodrem":
                return handleProductRemove(exec, category(exec.uuidArg(1)));
            case "new":
                Category newCategory = handleNew(exec, exec.joinedArgsColored(1));
                return handleInfo(exec, newCategory);
            case "refresh":
                return handleRefresh(exec);
            default:
                exec.respond(MessageType.WARNING, "Unbekannte Aktion.");
                respondUsage(exec);
        }
        return true;
    }

    private Category category(UUID categoryId) {
        return module.categories().findSingle(categoryId)
                .orElseThrow(() -> new UserException("Keine Kategorie mit der UUID %s gefunden.", categoryId));
    }

    private boolean handleInfo(BukkitExecution exec, Category category) {
        exec.respond(MessageType.HEADER, "Kategorieinfo");
        exec.respond(MessageType.RESULT_LINE, "Anzeigename: §s%s", category.getDisplayName());
        exec.respond(MessageType.RESULT_LINE, "Icon: §s%s", category.getIconName());
        exec.respond(infoDescription(category));
        exec.respond(infoEditActions(category));
        exec.respond(infoProductActions(category));
        exec.respond(infoRefresh(category));
        return true;
    }

    private XyComponentBuilder infoDescription(Category category) {
        String[] descriptionLines = category.getDescription().split("\r?\n");
        XyComponentBuilder builder = resultLineBuilder().append("Beschreibung: ")
                .append(descriptionLines[0]);
        if (descriptionLines.length > 1) {
            builder.tooltip(descriptionLines).append(" [...]", ChatColor.DARK_GREEN);
        }
        builder.append(" [+]", ChatColor.DARK_GREEN).tooltip("Neue Zeile")
                .suggest("/lacat descadd " + category.getUniqueId() + " ");
        return builder;
    }

    private XyComponentBuilder infoEditActions(Category category) {
        return resultLineBuilder().append("Ändern: ")
                .append("[Anzeigenamen] ", ChatColor.DARK_PURPLE).suggest("/lacat display " + category.getUniqueId() + " ")
                .append("[Icon] ", ChatColor.DARK_GREEN).suggest("/lacat icon " + category.getUniqueId() + " ")
                .append("[Beschreibung komplett]", ChatColor.DARK_AQUA).suggest("/lacat desc " + category.getUniqueId() + " ");
    }

    private XyComponentBuilder infoProductActions(Category category) {
        return resultLineBuilder().append("Produkte:")
                .append(" [Hinzufügen]", ChatColor.DARK_GREEN).command("/lacat prodadd " + category.getUniqueId())
                .append(" [Entfernen]", ChatColor.DARK_RED).command("/lacat prodrem " + category.getUniqueId());
    }

    private XyComponentBuilder infoRefresh(Category category) {
        return resultLineBuilder().append("Aktualisieren: ")
                .append("[lokal] ", ChatColor.DARK_GREEN).command("/lacat info " + category.getUniqueId())
                .append("[aus Datenbank]", ChatColor.DARK_PURPLE).command("/lacat refresh").tooltip("§e§lAchtung: §eLeert Cache für alle Kategorien.");
    }

    private boolean handleIcon(BukkitExecution exec, Category category, String newIcon) {
        category.setIconName(newIcon);
        module.categories().save(category);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Icon erfolgreich gesetzt.");
        return true;
    }

    private boolean handleDisplayName(BukkitExecution exec, Category category, String newDisplayName) {
        category.setDisplayName(newDisplayName);
        module.categories().save(category);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Anzeigename erfolgreich gesetzt auf %s§p.", category.getDisplayName());
        return true;
    }

    private boolean handleDescriptionSet(BukkitExecution exec, Category category, String description) {
        category.setDescription(description);
        module.categories().save(category);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Beschreibung erfolgreich überschrieben.");
        return true;
    }

    private boolean handleDescriptionAdd(BukkitExecution exec, Category category, String newLine) {
        String previousDescription = category.getDescription();
        String lineDelimiter = previousDescription == null || previousDescription.isEmpty() ? "" : "\n";
        category.setDescription(previousDescription + lineDelimiter + newLine);
        module.categories().save(category);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Beschreibung erfolgreich bearbeitet.");
        return true;
    }

    private boolean handleList(BukkitExecution exec, String filter) {
        Collection<Category> categories = module.categories().findAll().stream()
                .filter(categoryMatches(filter.toLowerCase()))
                .collect(Collectors.toList());
        exec.respond(MessageType.HEADER, "%d Kategorien gefunden:", categories.size());
        categories.forEach(cat -> showCategoryListItem(exec, cat));
        return true;
    }

    private void showCategoryListItem(BukkitExecution exec, Category category) {
        exec.respond(
                resultLineBuilder()
                        .append(ChatColor.stripColor(category.getDisplayName()), ChatColor.YELLOW)
                        .append(" [Info]", ChatColor.DARK_BLUE)
                        .command("/lacat info " + category.getUniqueId())
        );
    }

    private Predicate<Category> categoryMatches(String filter) {
        return cat -> cat.getDisplayName().toLowerCase().contains(filter) ||
                cat.getDescription().toLowerCase().contains(filter) ||
                cat.getIconName().toLowerCase().contains(filter) ||
                cat.getUniqueId().toString().startsWith(filter);
    }

    private boolean handleProductRemove(BukkitExecution exec, Category category) {
        ProductSelectionMenu menu = ProductSelectionMenu.withoutParent(
                category, productRemoveClickHandler(exec, category), exec.player(), module
        );
        menu.addItems(module.categories().findProductsOf(category));
        menu.open();
        return true;
    }

    private BiConsumer<Product, ProductSelectionMenu> productRemoveClickHandler(BukkitExecution exec, Category category) {
        return (product, newMenu) -> {
            module.categories().dissociate(category, product);
            exec.respond(MessageType.RESULT_LINE_SUCCESS, "Produkt %s §paus der Kategorie %s §pentfernt.", product.getDisplayName(), category.getDisplayName());
        };
    }

    private boolean handleProductAdd(BukkitExecution exec, Category category) {
        ProductSelectionMenu menu = ProductSelectionMenu.withoutParent(
                category, productAddClickHandler(exec, category), exec.player(), module
        );
        menu.addItems(findProductsNotInCategory(category));
        menu.open();
        return true;
    }

    private Collection<Product> findProductsNotInCategory(Category category) {
        Collection<Product> allProducts = module.client().products().query().inAnyModule().andActive().execute();
        Collection<Product> categoryProducts = module.categories().findProductsOf(category);
        return allProducts.stream()
                .filter(prod -> !categoryProducts.contains(prod))
                .collect(Collectors.toList());
    }

    private BiConsumer<Product, ProductSelectionMenu> productAddClickHandler(BukkitExecution exec, Category category) {
        return (product, newMenu) -> {
            module.categories().associate(category, product);
            exec.respond(MessageType.RESULT_LINE_SUCCESS, "Produkt %s §pzur Kategorie %s §phinzugefügt.", product.getDisplayName(), category.getDisplayName());
        };
    }

    private Category handleNew(BukkitExecution exec, String displayName) {
        SqlCategory category = new SqlCategory(UUID.randomUUID(), "dirt", displayName, "");
        module.categories().save(category);
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Kategorie erstellt!");
        module.categories().clearCache();
        return category;
    }

    private boolean handleRefresh(BukkitExecution exec) {
        module.categories().clearCache();
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Globaler Kategoriencache geleert!");
        return true;
    }

    private void respondUsage(BukkitExecution exec) {
        exec.respondUsage("display", "<UUID> <Anzeigename>", "Setzt Anzeigenamen");
        exec.respondUsage("icon", "<UUID> <Iconname>", "Setzt Icon ('stained_clay:15')");
        exec.respondUsage("desc", "<UUID> <neue Beschreibung>", "Setzt die Beschreibung");
        exec.respondUsage("descadd", "<UUID> <neue Zeile>", "Fügt der  Beschreibung eine Zeile hinzu");
        exec.respondUsage("info", "<UUID>", "Zeigt Infos zu einer Kategorie");
        exec.respondUsage("list", "[Suchbegriff]", "Listet alle Kategorien auf");
        exec.respondUsage("prodrem", "<UUID>", "Wählt Produkte zum Entfernen aus einer Kategorie");
        exec.respondUsage("prodadd", "<UUID>", "Fügt einer Kategorie Produkte hinzu");
        exec.respondUsage("new", "<Anzeigename>", "Fügt eine Kategorie hinzu");
    }
}
