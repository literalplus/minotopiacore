/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.module.inject;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

/**
 * Handles a dependency on a plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11.6.16
 */
public class PluginDependency<T extends JavaPlugin> extends SimpleInjectionTarget<T> {
    /**
     * Creates a new injection target.
     *
     * @param clazz the target class, e.g. what will be injected
     */
    public PluginDependency(Class<T> clazz) {
        super(clazz);
        Preconditions.checkState(JavaPlugin.class.isAssignableFrom(getClazz()),
                "clazz %s must extend JavaPlugin", getClazz());
    }

    @Override
    public T createInstance() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        T plugin = JavaPlugin.getPlugin(getClazz());
        setInstance(plugin);
        return plugin;
    }
}
