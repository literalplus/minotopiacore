/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.blocklock.command.BlockLockCommand;
import li.l1t.mtc.module.blocklock.listener.BlockLockInteractionListener;
import li.l1t.mtc.module.blocklock.listener.BlockLockPlaceListener;
import li.l1t.mtc.module.blocklock.listener.BlockLockToolListener;
import org.bukkit.Material;

/**
 * A module that adds special protection for selected materials, allowing only the player who placed it to destroy it.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
public class BlockLockModule extends ConfigurableMTCModule {
    public static final String INFO_PERMISSION = "mtc.blocklock.info";
    public static final String ADMIN_PERMISSION = "mtc.blocklock.admin";
    public static final String TOOL_DISPLAY_NAME = "§3§lBlockLock-Infotool";
    public static final Material TOOL_TYPE = Material.MELON_BLOCK;
    private BlockLockConfig config;

    @InjectMe
    public BlockLockModule(BlockLockConfig config) {
        super("BlockLock", "modules/blocklock.cfg.yml", ClearCacheBehaviour.RELOAD, false);
        this.config = config;
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(inject(BlockLockCommand.class), "bl");
        registerListener(inject(BlockLockInteractionListener.class));
        registerListener(inject(BlockLockPlaceListener.class));
        registerListener(inject(BlockLockToolListener.class));
    }

    @Override
    protected void reloadImpl() {
        config.loadFrom(configuration);
        configuration.asyncSave(plugin);
    }
}
