/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
