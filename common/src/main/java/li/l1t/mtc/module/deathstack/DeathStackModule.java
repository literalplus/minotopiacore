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

package li.l1t.mtc.module.deathstack;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.stack.InventoryCompactor;
import li.l1t.mtc.module.stack.StackModule;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * DeathStack is a module that simplifies picking up player drops by pre-stacking them, so that players don't have to
 * repeatedly call /stack.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public class DeathStackModule extends ConfigurableMTCModule {
    @InjectMe(required = false)
    private StackModule stackModule;
    private InventoryCompactor compactor;

    public DeathStackModule() {
        super("DeathStack", "modules/deathstack.cfg.yml", ClearCacheBehaviour.RELOAD_ON_FORCED, true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        Predicate<ItemStack> stackablePredicate = any -> true;
        if(stackModule != null) {
            stackablePredicate = stackModule::isStackingPermitted;
            stackablePredicate = stackablePredicate.and(stackModule::isCoveredByAllowedSpecs);
        }
        this.compactor = new InventoryCompactor(64, stackablePredicate);
        registerListener(new DeathStackListener(this));
    }

    @Override
    protected void reloadImpl() {

    }

    public List<ItemStack> compactStacks(List<ItemStack> input) {
        ItemStack[] inputArray = input.toArray(new ItemStack[input.size()]);
        return new ArrayList<>(Arrays.asList(compactor.compact(inputArray)));
    }
}
