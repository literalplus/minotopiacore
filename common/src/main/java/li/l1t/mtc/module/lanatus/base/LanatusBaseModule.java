/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.base;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.MTCModuleAdapter;

/**
 * Provides access to the Lanatus API through XYC.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
public class LanatusBaseModule extends MTCModuleAdapter {
    public static final String READ_PERMISSION = "lanatus.admin.read";
    public static final String ADMIN_PERMISSION = "lanatus.admin.admin";
    public static final String GIVE_PERMISSION = "lanatus.admin.give";
    public static final String RANK_PERMISSION = "lanatus.admin.rank";
    private final MTCLanatusClient client;
    private final XLoginHook xLoginHook;

    @InjectMe
    public LanatusBaseModule(MTCLanatusClient client, XLoginHook xLoginHook) {
        super("LanatusApiModule", true);
        this.client = client;
        this.xLoginHook = xLoginHook;
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(new LanatusInfoCommand(client, xLoginHook), "lainfo")
                .behaviour(CommandBehaviours.permissionChecking(READ_PERMISSION));
    }
}
