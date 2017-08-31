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

package li.l1t.mtc.api.module.inject;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
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
    public T createInstance() throws IllegalAccessException,
            InvocationTargetException, InstantiationException {
        T plugin = JavaPlugin.getPlugin(getClazz());
        setInstance(plugin);
        return plugin;
    }

    @Override
    public Constructor<T> getInjectableConstructor() {
        return null;
    }
}
