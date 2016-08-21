/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.hook;

import li.l1t.mtc.hook.impl.TitleManagerHookImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Manages the hook for the TitleManager plugin, which allows to show titles and actionbar messages
 * from code.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class TitleManagerHook extends SimpleHookWrapper {
    private TitleManagerHookImpl unsafe;

    public TitleManagerHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    public void sendTitle(Player plr, String title, String subtitle) {
        if (!isActive()) {
            return;
        }

        unsafe.sendTitle(plr, title, subtitle);
    }

    public void sendActionbarMessage(Player plr, String message) {
        if (!isActive()) {
            return;
        }

        unsafe.sendActionbarMessage(plr, message);
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }
}
