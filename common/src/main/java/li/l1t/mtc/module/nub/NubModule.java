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

package li.l1t.mtc.module.nub;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.PlayerGameManager;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.nub.api.NubProtection;
import li.l1t.mtc.module.nub.listener.NubJoinLeaveListener;
import li.l1t.mtc.module.nub.listener.NubPreventListener;
import li.l1t.mtc.module.nub.listener.NubProtectListener;
import li.l1t.mtc.module.nub.service.SimpleProtectionService;
import li.l1t.mtc.module.nub.task.ProtectionCheckTask;
import li.l1t.mtc.module.nub.ui.text.NubCommand;
import li.l1t.mtc.yaml.ManagedConfiguration;

import java.util.Objects;

/**
 * MTC N.u.b. (German "neu und beschützt", which means "new and protected") provides temporary protection in form of
 * god
 * mode for new players.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class NubModule extends ConfigurableMTCModule {
    public static final String ADMIN_PERMISSION = "mtc.nub.admin";
    private static final String BASE_FOLDER_PATH = "modules/nub";

    @InjectMe
    private NubConfig config;
    @InjectMe
    private ProtectionCheckTask checkTask;
    @InjectMe
    private SimpleProtectionService protectionService;
    @InjectMe
    private NubCommand command;
    @InjectMe
    private LocalProtectionManager manager;
    @InjectMe
    private PlayerGameManager gameManager;

    public NubModule() {
        super("Nub", BASE_FOLDER_PATH + "/nub.cfg.yml", ClearCacheBehaviour.RELOAD, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        checkTask.start();
        registerListener(new NubJoinLeaveListener(protectionService, getPlugin()));
        registerListener(new NubProtectListener(protectionService, gameManager));
        registerListener(new NubPreventListener(protectionService, gameManager));
        registerCommand(command, "nub", "godlogin");

        getPlugin().getServer().getOnlinePlayers().stream()
                .filter(protectionService::hasPausedProtection)
                .forEach(protectionService::resumeProtection);
    }

    @Override
    public void disable(MTCPlugin plugin) {
        manager.getAllProtections().stream()
                .map(NubProtection::getPlayerId)
                .map(getPlugin().getServer()::getPlayer)
                .filter(Objects::nonNull)
                .forEach(protectionService::pauseProtection);
    }

    @Override
    protected void reloadImpl() {
        config.loadFrom(this);
    }

    ManagedConfiguration getConfiguration() {
        return configuration;
    }

    public NubConfig getConfig() {
        return config;
    }
}
