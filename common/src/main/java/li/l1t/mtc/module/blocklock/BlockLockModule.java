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

package li.l1t.mtc.module.blocklock;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.blocklock.command.BlockLockCommand;
import li.l1t.mtc.module.blocklock.listener.BlockLockInteractionListener;
import li.l1t.mtc.module.blocklock.listener.BlockLockPlaceBreakListener;
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
        registerListener(inject(BlockLockPlaceBreakListener.class));
        registerListener(inject(BlockLockToolListener.class));
    }

    @Override
    protected void reloadImpl() {
        config.loadFrom(configuration);
        configuration.asyncSave(plugin);
    }
}
