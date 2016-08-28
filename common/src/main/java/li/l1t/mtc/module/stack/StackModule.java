/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.stack;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.fulltag.FullTagModule;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A module that provides a /stack command that can be controlled in a more fine-grained manner.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-27
 */
public class StackModule extends ConfigurableMTCModule {
    public static final String NAME = "Stack";
    private static final String SPECS_PATH = "allowed-items-limited";
    private static final String OVERRIDE_SIZES_PATH = "override-sizes-to-check";
    @InjectMe(required = false)
    private FullTagModule fullTagModule;
    private SortedSet<Integer> overrideSizes = new TreeSet<>(Arrays.asList(8, 32, 64));
    private List<ItemSpec> specs = new ArrayList<>(Collections.singletonList(new ItemSpec(Material.RECORD_11, (short) -1)));

    protected StackModule() {
        super(NAME, "modules/stack.cfg.yml", ClearCacheBehaviour.RELOAD, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        ConfigurationSerialization.registerClass(ItemSpec.class);
        super.enable(plugin);
        registerCommand(new StackCommand(this), "stack", "xstack")
                .behaviour(CommandBehaviours.permissionChecking("mtc.stack"));
        registerCommand(new StackAdminCommand(this), "stacka", "stackadmin")
                .behaviour(CommandBehaviours.permissionChecking("mtc.admin.stack"));
    }

    @Override
    protected void reloadImpl() {
        configuration.addDefault(OVERRIDE_SIZES_PATH, new ArrayList<>(overrideSizes));
        configuration.addDefault(SPECS_PATH, specs);
        overrideSizes = new TreeSet<>(Collections.reverseOrder());
        overrideSizes.addAll(
                configuration.getStringList(OVERRIDE_SIZES_PATH).stream()
                        .map(str -> {
                            try {
                                return Integer.parseInt(str);
                            } catch (NumberFormatException e) {
                                return -1;
                            }
                        })
                        .filter(i -> i > 1)
                        .collect(Collectors.toSet())
        );
        specs = configuration.getListChecked(SPECS_PATH, ItemSpec.class);
    }

    public boolean isStackingPermitted(ItemStack stack) {
        return fullTagModule == null ||
                !fullTagModule.isFullItem(stack);
    }

    public boolean isCoveredByAllowedSpecs(ItemStack stack) {
        return specs.stream().anyMatch(spec -> spec.matches(stack));
    }

    public SortedSet<Integer> getOverrideSizesOrderedInReverse() {
        return overrideSizes;
    }

    public boolean addAllowedSpecIfNotPresent(ItemSpec spec) {
        if (specs.contains(spec)) {
            return false;
        } else {
            specs.add(spec);
            saveSpecs();
            return true;
        }
    }

    public boolean removeAllowedSpecIfPresent(ItemSpec spec) {
        if (!specs.contains(spec)) {
            return false;
        }
        specs.remove(spec);
        saveSpecs();
        return true;
    }

    private void saveSpecs() {
        configuration.set(SPECS_PATH, specs);
        save();
    }
}
