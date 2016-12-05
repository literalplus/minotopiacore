/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.service;

import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.lanatus.api.account.LanatusAccount;
import li.l1t.lanatus.api.position.PositionRepository;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import li.l1t.lanatus.shop.api.ItemIconService;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

/**
 * A service that creates item icons for various things.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class SimpleItemIconService implements ItemIconService {
    private final PositionRepository positionRepository;

    @InjectMe
    public SimpleItemIconService(MTCLanatusClient lanatus) {
        this.positionRepository = lanatus.positions();
    }

    @Override
    public ItemStack createIconStack(Product product, UUID playerId) {
        boolean hasProduct = positionRepository.playerHasProduct(playerId, product.getUniqueId());
        return createIconStack(product, hasProduct);
    }

    public ItemStack createIconStack(Product product, boolean hasPosition) {
        ItemStackFactory factory = createBaseProductStack(product, hasPosition);
        if (product.isPermanent()) {
            factory.lore("§e§o(permanent)");
        }
        if (hasPosition && product.isPermanent()) {
            factory.lore("§a§o(im Besitz)");
            factory.glow();
        }
        return factory.hideAll().produce();
    }

    private ItemStackFactory createBaseProductStack(Product product, boolean hasPosition) {
        ItemStackFactory factory = new ItemStackFactory(baseStack(product.getIconName()));
        factory.displayName(productColor(product, hasPosition) + nullableString(product.getDisplayName()));
        factory.lore(product.getDescription());
        factory.lore(" ").lore(formatMelonsCost(product));
        return factory;
    }

    public ItemStack baseStack(String spec) {
        if (spec != null && !spec.isEmpty()) {
            String[] parts = spec.split(":", 2);
            Material material = Material.matchMaterial(parts[0]);
            if (material != null)
                if (parts.length < 2 || StringUtils.isNumeric(parts[1])) {
                    return new ItemStack(material, 1, Short.parseShort(parts[1]));
                } else {
                    return new ItemStack(material);
                }
        }
        return new ItemStack(Material.DEAD_BUSH);
    }

    private String productColor(Product product, boolean hasPosition) {
        if (product.isPermanent()) {
            return "§e";
        } else if (hasPosition) {
            return "§a";
        } else {
            return "§c";
        }
    }

    private String nullableString(String str) {
        return (str == null || str.isEmpty()) ? "(???)" : str;
    }

    private String formatMelonsCost(Product product) {
        return String.format("§efür nur §a%s§e!", melonPlural(product.getMelonsCost()));
    }

    private String melonPlural(int melonsCount) {
        return melonsCount + " Melone" + (melonsCount == 1 ? "" : "n");
    }

    @Override
    public ItemStack createNotAffordableStack(Product product, LanatusAccount account) {
        return new ItemStackFactory(Material.BARRIER)
                .displayName("§4" + nullableString(product.getDisplayName()))
                .lore("§4Das kannst du dir").lore("§4nicht leisten!")
                .lore(" ").lore("§ebenötigt: " + melonPlural(product.getMelonsCost()))
                .lore("§cDir fehlen: " + melonPlural(product.getMelonsCost() - account.getMelonsCount()))
                .lore(product.getDescription()).hideAll().produce();
    }

    @Override
    public ItemStack createIconStack(Category category) {
        ItemStackFactory factory = new ItemStackFactory(baseStack(category.getIconName()));
        factory.displayName(nullableString(category.getDisplayName()));
        factory.lore(nullableString(category.getDescription()));
        return factory.hideAll().produce();
    }

    @Override
    public ItemStack createPurchaseHelpStack() {
        return createInfoStack(
                "§eProdukt kaufen",
                "§7Drücke auf den grünen",
                "§7Lehmblock, um dieses Produkt",
                "§7für Melonen zu kaufen.",
                " ",
                "§aBewege deine Maus",
                "§aüber die Items links",
                "§avon diesem, um",
                "§aInformationen zu",
                "§aerhalten."
        );
    }

    @Override
    public ItemStack createInfoStack(String title, String... descriptionLines) {
        return new ItemStackFactory(Material.SIGN)
                .displayName("§e" + title)
                .lore(Arrays.asList(descriptionLines))
                .produce();
    }
}
