/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.base;

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.api.position.Position;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.api.purchase.Purchase;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.command.BukkitExecutionExecutor;
import li.l1t.mtc.hook.XLoginHook;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static li.l1t.mtc.api.chat.MessageType.HEADER;
import static li.l1t.mtc.api.chat.MessageType.LIST_HEADER;
import static li.l1t.mtc.api.chat.MessageType.RESULT_LINE;

/**
 * Executes the /lainfo command, providing information about the Lanatus accounts of a provided
 * player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
class LanatusInfoCommand extends BukkitExecutionExecutor {
    private final LanatusClient client;
    private final XLoginHook xLogin;

    public LanatusInfoCommand(LanatusClient client, XLoginHook xLogin) {
        this.client = client;
        this.xLogin = xLogin;
    }

    @Override
    public boolean execute(CommandExecution exec) {
        if (exec.hasNoArgs()) {
            exec.requireIsPlayer();
            showAccountInfo(exec, profile(exec.senderId()));
            return true;
        }
        switch (exec.arg(0).toLowerCase()) {
            case "plist":
                showPurchaseList(exec, argumentProfile(exec.arg(1), exec.sender()));
                return true;
            case "ilist":
                showPositionList(exec, argumentProfile(exec.arg(1), exec.sender()));
                return true;
            case "purchase":
                showPurchaseDetails(exec, uuid(exec.arg(1)));
                return true;
            case "help":
                break;
            default:
                showAccountInfo(exec, argumentProfile(exec.arg(0), exec.sender()));
                return true;
        }
        showUsage(exec);
        return true;
    }

    private XLoginHook.Profile profile(UUID playerId) {
        XLoginHook.Profile profile = xLogin.getProfile(playerId);
        if (profile == null) {
            throw new UserException("Kein Spieler mit der UUID %s bekannt", playerId);
        }
        return profile;
    }

    private XLoginHook.Profile argumentProfile(String input, CommandSender sender) {
        return xLogin.findSingleMatchingProfileOrFail(
                input, sender, profile -> String.format("/lainfo %s", profile.getUniqueId())
        );
    }

    private void showAccountInfo(CommandExecution exec, XLoginHook.Profile profile) {
        AccountSnapshot account = client.accounts().find(profile.getUniqueId());
        exec.respond(HEADER, "Lanatus-Info: §a%s", profile.getName());
        exec.respond(RESULT_LINE, "UUID: §s%s", account.getPlayerId());
        exec.respond(RESULT_LINE, "Melonen: §s%s  §pRang: §s%s", account.getMelonsCount(), account.getLastRank());
        exec.respond(RESULT_LINE, "§pStand: §s%s", account.getSnapshotInstant().atZone(ZoneId.systemDefault()));
        respondAccountActions(exec, profile);
    }

    private void respondAccountActions(CommandExecution exec, XLoginHook.Profile profile) {
        exec.respond(resultLineBuilder()
                .append("[Käufe]", ChatColor.DARK_GREEN)
                .hintedCommand("/lainfo plist " + profile.getUniqueId())
                .append("  ")
                .append("[Items]", ChatColor.DARK_PURPLE)
                .hintedCommand("/lainfo ilist " + profile.getUniqueId())
                .append("  ", ChatColor.YELLOW)
                .appendIf(exec.sender().hasPermission(LanatusBaseModule.GIVE_PERMISSION), "[Melonen geben]")
                .suggest("/lagive " + profile.getUniqueId() + " ").tooltip("Klicken, um \nMelonen zu geben")
                .append("  ", ChatColor.RED)
                .appendIf(exec.sender().hasPermission(LanatusBaseModule.RANK_PERMISSION), "[Rang setzen]")
                .suggest("/larank " + profile.getUniqueId() + " ").tooltip("Klicken, um \nRang zu setzen")
        );
    }

    private void showPurchaseList(CommandExecution exec, XLoginHook.Profile profile) {
        Collection<Purchase> purchases = client.purchases().findByPlayer(profile.getUniqueId());
        exec.respond(LIST_HEADER, "%d Käufe von §a%s:", purchases.size(), profile.getName());
        purchases.forEach(purchase -> showPurchaseListItem(exec, purchase));
    }

    private void showPurchaseListItem(CommandExecution exec, Purchase purchase) {
        exec.respond(resultLineBuilder()
                .appendIf(client.positions().findByPurchase(purchase.getUniqueId()).isPresent(), "(aktiv) ", ChatColor.YELLOW)
                .append(purchase.getProduct().getDisplayName(), ChatColor.GREEN).bold(false).underlined(true)
                .append(" am ", ChatColor.GOLD).underlined(false)
                .append(purchase.getCreationInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), ChatColor.GREEN)
                .append(" ")
                .append("[Details]", ChatColor.DARK_GREEN).underlined(true)
                .hintedCommand("/lainfo purchase " + purchase.getUniqueId())
        );
    }

    private void showPurchaseDetails(CommandExecution exec, UUID purchaseId) {
        Purchase purchase = client.purchases().findById(purchaseId);
        String playerName = xLogin.getDisplayString(purchase.getPlayerId());
        exec.respond(HEADER, "Kauf %s von %s", purchaseId, playerName);
        exec.respond(RESULT_LINE, "Kaufdatum: §s%s", purchase.getCreationInstant().atZone(ZoneId.systemDefault()));
        exec.respond(RESULT_LINE, "§pKaufpreis: §s%s", purchase.getMelonsCost());
        exec.respond(RESULT_LINE, "Daten: §s%s", purchase.getData());
        exec.respond(RESULT_LINE, "Anmerkung: §s%s", purchase.getComment());
        exec.respond(appendProductOverview(resultLineBuilder(), purchase.getProduct()));
        Optional<Position> position = client.positions().findByPurchase(purchaseId);
        if (position.isPresent()) {
            exec.respond(RESULT_LINE, "Itemdaten: '%s'", position.get().getData());
        } else {
            exec.respond(RESULT_LINE, "Zu diesem Kauf gibt es aktuell kein Item.");
        }
    }

    private XyComponentBuilder appendProductOverview(XyComponentBuilder builder, Product product) {
        builder.append("Produkt: ", ChatColor.GOLD)
                .append(product.getDisplayName(), ChatColor.GREEN)
                .append(" ")
                .append("[Details]").underlined(true)
                .hintedCommand("/laprod info " + product.getUniqueId())
                .append("", ChatColor.GOLD).underlined(false);
        return builder;
    }

    private void showPositionList(CommandExecution exec, XLoginHook.Profile profile) {
        Collection<Position> positions = client.positions().findAllByPlayer(profile.getUniqueId());
        exec.respond(LIST_HEADER, "§a%s §pbesitzt %d Items:", profile.getName(), positions.size());
        positions.forEach(position -> showPositionListItem(exec, position));
    }

    private void showPositionListItem(CommandExecution exec, Position position) {
        XyComponentBuilder builder = resultLineBuilder();
        appendProductOverview(builder, position.getProduct());
        exec.respond(builder
                .append(" mit Daten '", ChatColor.GOLD)
                .append(position.getData(), ChatColor.GREEN)
                .append("' ")
                .append("[Kauf]", ChatColor.DARK_GREEN)
                .hintedCommand("/lainfo purchase " + position.getPurchaseId())
        );
    }

    private void showUsage(CommandExecution exec) {
        exec.respondUsage("plist", "<Spieler|UUID>", "Zeigt Käufe.");
        exec.respondUsage("purchase", "<UUID>", "Zeigt einen Kauf.");
        exec.respondUsage("ilist", "<Spieler|UUID>", "Zeigt aktuelle Positionen.");
        exec.respondUsage("", "[Spieler|UUID]", "Zeigt Infos zu (d)einem Account.");
    }
}
