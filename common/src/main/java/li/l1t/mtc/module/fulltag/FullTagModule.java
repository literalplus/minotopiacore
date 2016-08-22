/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.fulltag;

import li.l1t.common.lib.com.mojang.api.profiles.HttpProfileRepository;
import li.l1t.common.util.UUIDHelper;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.fulltag.dist.CommandRetrieveFull;
import li.l1t.mtc.module.fulltag.dist.FullDistributionManager;
import li.l1t.mtc.module.fulltag.model.FullDataRepository;
import li.l1t.mtc.module.fulltag.model.FullInfo;
import li.l1t.mtc.module.fulltag.model.FullRegistry;
import li.l1t.mtc.module.fulltag.model.LegacyFullDataRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>Module that manages items with all possible enchantments on the highest legal level ("Full
 * items"), tracks their location and keeps logs of what users do with those items. </p>
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
    private LegacyFullDataRepository legacyRepository;
    private FullRegistry registry;
    private Set<UUID> allowedPlayerIds = new HashSet<>();
    private boolean fullReturnEnabled;

    public FullTagModule() {
        super(NAME, "modules/fulltag/fulltag.conf.yml", ClearCacheBehaviour.RELOAD_ON_FORCED, false);
    }

    @Override
    public void enable(@Nonnull MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        repository = new FullDataRepository(this);
        registry = new FullRegistry(this);
        distributionManager = new FullDistributionManager(this);

        registerCommand(new CommandFullTag(this), "full");
        registerCommand(new CommandRetrieveFull(this), "fulls", "canhasfull", "fullreturn")
                .behaviour(CommandBehaviours.playerOnly());
        plugin.getServer().getPluginManager().registerEvents(new FullTagListener(this), getPlugin());
    }

    @Override
    public void disable(MTCPlugin plugin) {
        repository.clearCache(false, plugin);
        registry.clearCache(false, plugin);

        super.disable(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        configuration.options().copyDefaults(true);
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
        configuration.addDefault("enable.fullreturn", false);
        fullReturnEnabled = configuration.getBoolean("enable.fullreturn");
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
     * Checks whether the player specified by given unique id is allowed to use the FullTag
     * management command.
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
     * @return the positive integer full id if an id could be extracted, or a negative integer
     * otherwise
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
     * Checks whether given item stack is a full item managed by this module.
     *
     * @param stack the stack to check
     * @return whether given item is a full item
     */
    public boolean isFullItem(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return false;
        }
        List<String> lore = stack.getItemMeta().getLore();
        if (lore == null) {
            return false;
        }
        for (String str : lore) {
            if (str.startsWith(FULL_LORE_PREFIX)) {
                return true;
            }
        }
        return false;
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

    public LegacyFullDataRepository getLegacyRepository() {
        if (legacyRepository == null) {
            legacyRepository = new LegacyFullDataRepository(this, new HttpProfileRepository("minecraft"));
        }
        return legacyRepository;
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

    public boolean isFullReturnEnabled() {
        return fullReturnEnabled;
    }
}