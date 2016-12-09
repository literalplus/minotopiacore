/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.nub.listener.NubJoinLeaveListener;
import li.l1t.mtc.module.nub.listener.NubPreventListener;
import li.l1t.mtc.module.nub.listener.NubProtectListener;
import li.l1t.mtc.module.nub.service.SimpleProtectionService;
import li.l1t.mtc.module.nub.task.ProtectionCheckTask;
import li.l1t.mtc.module.nub.ui.text.NubCommand;
import li.l1t.mtc.yaml.ManagedConfiguration;

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

    public NubModule() {
        super("Nub", BASE_FOLDER_PATH + "/nub.cfg.yml", ClearCacheBehaviour.RELOAD, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        checkTask.start();
        registerListener(new NubJoinLeaveListener(protectionService, getPlugin()));
        registerListener(new NubProtectListener(protectionService));
        registerListener(new NubPreventListener(protectionService));
        registerCommand(command, "nub", "godlogin");
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
