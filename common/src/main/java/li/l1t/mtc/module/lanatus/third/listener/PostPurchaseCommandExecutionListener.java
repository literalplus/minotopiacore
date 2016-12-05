/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.third.listener;

import li.l1t.lanatus.shop.api.event.PostPurchaseEvent;
import li.l1t.mtc.module.lanatus.third.LanatusThirdModule;
import li.l1t.mtc.module.lanatus.third.product.ThirdProduct;
import li.l1t.mtc.module.lanatus.third.product.ThirdProductRepository;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listens for post purchase events and executes the commands associated with the respective third products.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-05
 */
public class PostPurchaseCommandExecutionListener implements Listener {
    private final ThirdProductRepository thirdProductRepository;
    private final Server server;

    public PostPurchaseCommandExecutionListener(LanatusThirdModule module) {
        this.thirdProductRepository = module.thirdProducts();
        this.server = module.getPlugin().getServer();
    }

    @EventHandler
    public void onPostPurchase(PostPurchaseEvent event) {
        thirdProductRepository.getByProduct(event.getProduct())
                .ifPresent(thirdProduct -> handlePurchase(event, thirdProduct));
    }

    private void handlePurchase(PostPurchaseEvent event, ThirdProduct product) {
        product.getCommands()
                .forEach(command -> executeCommand(command, event.getPlayer(), product));
    }

    private void executeCommand(String command, Player player, ThirdProduct product) {
        String rawCommand = stripCommandSlashIfPresent(command);
        server.dispatchCommand(server.getConsoleSender(), substituteTemplates(rawCommand, player, product));
    }

    private String substituteTemplates(String rawCommand, Player player, ThirdProduct product) {
        return rawCommand
                .replaceAll("%p", player.getName())
                .replaceAll("%u", player.getUniqueId().toString());
    }

    private String stripCommandSlashIfPresent(String command) {
        return command.replaceFirst("^/", "");
    }
}
