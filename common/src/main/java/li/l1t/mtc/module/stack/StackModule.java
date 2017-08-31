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

import java.util.*;

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
        configuration.options().copyDefaults(true);
        overrideSizes = new TreeSet<>(Collections.reverseOrder());
        overrideSizes.addAll(
                configuration.getIntegerList(OVERRIDE_SIZES_PATH)
        );
        configuration.set(OVERRIDE_SIZES_PATH, overrideSizes);
        specs = configuration.getListChecked(SPECS_PATH, ItemSpec.class);
        configuration.set(SPECS_PATH, specs);
        configuration.trySave();
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

    public List<ItemSpec> getAllowedSpecs() {
        return specs;
    }
}
