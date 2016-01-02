/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.hook;

import org.bukkit.plugin.Plugin;

/**
 * A simple implementation of the HookWrapper interface without any actual functionality.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class SimpleHookWrapper implements HookWrapper {
    private final Plugin plugin;
    private boolean active;

    public SimpleHookWrapper(Plugin plugin, boolean active) {
        this.plugin = plugin;
        this.active = active;
    }

    public SimpleHookWrapper(Plugin plugin) {
        this(plugin, true);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }
}
