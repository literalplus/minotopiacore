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
        if(!event.getProduct().getModule().equals(LanatusThirdModule.MODULE_NAME)) {
            return;
        }
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
