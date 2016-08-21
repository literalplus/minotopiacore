/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.hook.impl;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import li.l1t.mtc.hook.HookWrapper;
import li.l1t.mtc.hook.Hooks;
import org.bukkit.entity.Player;

/**
 * Unsafe implementation of the TitleManager hook.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class TitleManagerHookImpl implements Hook {
    private boolean hooked = false;

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return Hooks.isPluginLoaded(wrapper, "TitleManager");
    }

    @Override
    public void hook(HookWrapper wrapper) {
        hooked = TitleObject.class.getName() != null; //Make sure the class is loaded
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    public void sendTitle(Player plr, String title, String subtitle) {
        new TitleObject(title, subtitle)
                .send(plr);
    }

    public void sendActionbarMessage(Player plr, String message) {
        new ActionbarTitleObject(message)
                .send(plr);
    }
}
