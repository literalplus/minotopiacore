/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.ui.text;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.common.inventory.gui.element.LambdaMenuElement;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.ItemIconService;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.BukkitExecutionExecutor;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.lanatus.perk.LocalPerkManager;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkRepository;
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

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Executes the /perks command which shows an inventory menu allowing to enable and disable perks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class CommandPerks extends BukkitExecutionExecutor {
    private final Plugin plugin;
    private final LanatusClient lanatus;
    private final PerkRepository perkRepository;
    private final ItemIconService iconService;
    private final PerkEnableService enableService;
    private final LocalPerkManager manager;

    @InjectMe
    public CommandPerks(Plugin plugin, MTCLanatusClient lanatus, SqlPerkRepository perkRepository,
                        SimpleItemIconService iconService, PerkEnableService enableService, LocalPerkManager manager) {
        this.plugin = plugin;
        this.lanatus = lanatus;
        this.perkRepository = perkRepository;
        this.iconService = iconService;
        this.enableService = enableService;
        this.manager = manager;
    }

    @Override
    public boolean execute(CommandExecution exec) throws UserException, InternalException {
        Player player = exec.player();
        MyPerksMenu menu = new MyPerksMenu(
                player, plugin, this::handlePerkClick,
                perk -> createPerkIcon(perk, player)
        );
        menu.addItems(findEnabledPerks(player));
        menu.addToTopRow(5, shopButton());

        menu.open();
        return true;
    }

    private Set<Perk> findEnabledPerks(Player player) {
        return perkRepository.findEnabledByPlayerId(player.getUniqueId()).stream()
                .map(manager::getPerk)
                .collect(Collectors.toSet());
    }

    private LambdaMenuElement<MyPerksMenu> shopButton() {
        return new LambdaMenuElement<>(
                MyPerksMenu.class, this::handleShopButtonClick, shopIcon()
        );
    }

    private void handleShopButtonClick(InventoryClickEvent event, MyPerksMenu menu) {
        ((Player) event.getWhoClicked()).performCommand("/lashop");
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
            factory.lore(" ").lore("§a§o(aktiv)").glow();
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
