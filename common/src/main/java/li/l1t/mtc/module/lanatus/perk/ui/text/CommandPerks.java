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

package li.l1t.mtc.module.lanatus.perk.ui.text;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.common.inventory.gui.element.LambdaMenuElement;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.ItemIconService;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.lanatus.perk.LocalPerkManager;
import li.l1t.mtc.module.lanatus.perk.PerksConfig;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkRepository;
import li.l1t.mtc.module.lanatus.perk.repository.AvailablePerk;
import li.l1t.mtc.module.lanatus.perk.repository.SqlPerkRepository;
import li.l1t.mtc.module.lanatus.perk.service.PerkEnableService;
import li.l1t.mtc.module.lanatus.perk.ui.inventory.MyPerksMenu;
import li.l1t.mtc.module.lanatus.shop.service.SimpleItemIconService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Executes the /perks command which shows an inventory menu allowing to enable and disable perks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class CommandPerks extends MTCExecutionExecutor {
    private final Plugin plugin;
    private final LanatusClient lanatus;
    private final PerkRepository perkRepository;
    private final ItemIconService iconService;
    private final PerkEnableService enableService;
    private final LocalPerkManager manager;
    private final PerksConfig config;

    @InjectMe
    public CommandPerks(MTCPlugin plugin, MTCLanatusClient lanatus, SqlPerkRepository perkRepository,
                        SimpleItemIconService iconService, PerkEnableService enableService, LocalPerkManager manager,
                        PerksConfig config) {
        this.plugin = plugin;
        this.lanatus = lanatus;
        this.perkRepository = perkRepository;
        this.iconService = iconService;
        this.enableService = enableService;
        this.manager = manager;
        this.config = config;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        Player player = exec.player();
        if (hasAnyPerksAvailable(player)) {
            openMenu(player);
        } else {
            exec.respond(MessageType.USER_ERROR, "Du besitzt keine Perks.");
        }
        return true;
    }

    private boolean hasAnyPerksAvailable(Player player) {
        return !perkRepository.findAvailableByPlayerId(player.getUniqueId()).getValidPerks().isEmpty();
    }

    private void openMenu(Player player) {
        MyPerksMenu menu = new MyPerksMenu(
                player, plugin, this::handlePerkClick,
                perk -> createPerkIcon(perk, player)
        );
        menu.addItems(findAvailablePerks(player));
        if (config.isLanatusShopAvailable()) {
            menu.addToTopRow(4, shopButton());
        }
        menu.open();
    }

    private Set<Perk> findAvailablePerks(Player player) {
        return perkRepository.findAvailableByPlayerId(player.getUniqueId()).stream()
                .map(AvailablePerk::getProductId)
                .map(this::getPerkOrLog)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Perk getPerkOrLog(UUID perkId) {
        try {
            return manager.getPerkById(perkId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private LambdaMenuElement<MyPerksMenu> shopButton() {
        return new LambdaMenuElement<>(
                MyPerksMenu.class, this::handleShopButtonClick, shopIcon()
        );
    }

    private void handleShopButtonClick(InventoryClickEvent event, MyPerksMenu menu) {
        ((Player) event.getWhoClicked()).performCommand("lashop");
    }

    private ItemStack shopIcon() {
        return new ItemStackFactory(Material.CHEST)
                .displayName("§ePerks erwerben")
                .lore("§7Hier klicken für:").lore("§e/lashop")
                .produce();
    }

    private ItemStack createPerkIcon(Perk perk, Player player) {
        Preconditions.checkNotNull(perk, "perk");
        Product product = lanatus.products().findById(perk.getProductId());
        boolean perkEnabled = perkRepository.isPerkEnabled(player.getUniqueId(), perk.getProductId());
        ItemStackFactory factory = iconService.createRawIconStack(product, perkEnabled);
        if (perkEnabled) {
            factory.lore(" ").lore("§7Klicken zum §cDeaktivieren").glow();
        } else {
            factory.lore(" ").lore("§7Klicken zum §aAktivieren");
        }
        return factory.produce();
    }

    private void handlePerkClick(Perk perk, InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        enableService.togglePerk(player, perk);
        menuFromEvent(event).redraw();
    }

    @SuppressWarnings("ConstantConditions")
    private MyPerksMenu menuFromEvent(InventoryClickEvent event) {
        InventoryHolder holder = event.getClickedInventory().getHolder();
        Verify.verify(holder instanceof MyPerksMenu, "Invalid inventory holder for MyPerksMenu click: ", holder, holder.getClass());
        return (MyPerksMenu) holder;
    }
}
