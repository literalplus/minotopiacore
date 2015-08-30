/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag;

import io.github.xxyy.common.util.UUIDHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.logging.LogManager;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import io.github.xxyy.mtc.module.fulltag.dist.FullDistributionManager;
import io.github.xxyy.mtc.module.fulltag.model.FullDataRepository;
import io.github.xxyy.mtc.module.fulltag.model.FullInfo;
import io.github.xxyy.mtc.module.fulltag.model.FullRegistry;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Module that manages items with all possible enchantments on the highest legal level
 * ("Full items"), tracks their location and keeps logs of what users do with those items.
 * <p/>
 * Additionally provides some stats and easy creation for administrators.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 29/08/15
 */
public class FullTagModule extends ConfigurableMTCModule {
    public static final String NAME = "FullTag";
    public static final String FULL_LORE_PREFIX = "§3§9full2: ";
    public static final Logger LOGGER = LogManager.getLogger(FullTagModule.class);
    private static final String ALLOWED_PLAYERS_PATH = "allowed-players";
    private FullDistributionManager distributionManager;
    private FullDataRepository repository;
    private FullRegistry registry;
    private Set<UUID> allowedPlayerIds = new HashSet<>();

    public FullTagModule() {
        super(NAME, "modules/fulltag/fulltag.conf.yml", ClearCacheBehaviour.RELOAD_ON_FORCED);
    }

    @Override
    public void enable(@Nonnull MTC plugin) throws Exception {
        super.enable(plugin);

        repository = new FullDataRepository(this);
        registry = new FullRegistry(this);
        distributionManager = new FullDistributionManager(this);

        plugin.getCommand("full").setExecutor(new CommandFullTag(this));
        plugin.getServer().getPluginManager().registerEvents(new FullTagListener(this), getPlugin());
    }

    @Override
    public void disable(MTC plugin) {
        repository.clearCache(false, plugin);
        registry.clearCache(false, plugin);

        super.disable(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        allowedPlayerIds = configuration.getStringList(ALLOWED_PLAYERS_PATH).stream()
                .map(input -> {
                    if (UUIDHelper.isValidUUID(input)) {
                        return UUID.fromString(input);
                    } else {
                        UUID id = getPlugin().getXLoginHook().getBestUniqueId(input);
                        LOGGER.info("Converting allowed player {} to UUID {}", input, id);
                        if (id == null) {
                            LOGGER.warn("Unable to find player for name " + input + " - Discarding from allowed players!");
                        }
                        return id;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        save();
    }

    @Override
    public void save() {
        configuration.set(ALLOWED_PLAYERS_PATH,
                allowedPlayerIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.toList())
        ); //Sets are serialised differently
        super.save();
    }

    /**
     * Checks whether the player specified by given unique id is allowed to use the FullTag management command.
     *
     * @param uuid the unique id to check
     * @return whether that player may use the FullTag management command
     */
    public boolean isAllowedPlayer(UUID uuid) {
        return allowedPlayerIds.contains(uuid);
    }

    /**
     * Attempts to extract a full id tag from a given item stack's lore lines.
     *
     * @param stack the stack to examine
     * @return the positive integer full id if an id could be extracted, or a negative integer otherwise
     */
    public int getFullId(@Nullable ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return -1;
        }
        List<String> lore = stack.getItemMeta().getLore();
        if (lore == null) {
            return -2;
        }
        for (String str : lore) {
            if (str.startsWith(FULL_LORE_PREFIX)) {
                str = str.substring(FULL_LORE_PREFIX.length());
                if (!StringUtils.isNumeric(str)) {
                    return -3;
                }
                return Integer.parseInt(str);
            }
        }
        return -4;
    }

    /**
     * Attempts to extract a full info from a given item stack's lore lines.
     *
     * @param stack the stack to examine
     * @return the information, or null, if nothing could be extracted
     * @see #getFullId(ItemStack)
     */
    @Nullable
    public FullInfo getFullInfo(ItemStack stack) {
        int fullId = getFullId(stack);

        if (fullId < 0) {
            return null;
        } else {
            return getRegistry().getById(fullId);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FullDataRepository getRepository() {
        return repository;
    }

    public FullRegistry getRegistry() {
        return registry;
    }

    public FullDistributionManager getDistributionManager() {
        return distributionManager;
    }
}
