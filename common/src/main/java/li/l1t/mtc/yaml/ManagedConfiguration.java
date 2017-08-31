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

package li.l1t.mtc.yaml;

import li.l1t.common.yaml.XyConfiguration;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.misc.Cache;
import li.l1t.mtc.misc.CacheHelper;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import org.apache.commons.lang.Validate;

import java.io.File;

/**
 * Represents a YAML configuration file managed by MTC with nice capabilities like automatic saving,
 * etc. This keeps a single File object to which changes can easily be saved to and loaded from.
 * Static load methods also provide extended syntax error fallback. (i.e. making backups of invalid
 * files)
 *
 * @author <a href="https://l1t.li">Literallie</a>
 * @since 2015-01-19
 */
public class ManagedConfiguration extends XyConfiguration implements Cache {
    private ClearCacheBehaviour clearCacheBehaviour = ClearCacheBehaviour.NOTHING;

    protected ManagedConfiguration(File file) {
        super(file);
    }

    /**
     * Creates a new {@link ManagedConfiguration}, loading from the given file. <p> Any errors
     * loading the Configuration will be logged and available at {@link #getError()}. If the
     * specified input is not a valid config, a blank config will be returned. </p> The encoding
     * used may follow the system dependent default.
     *
     * @param file      Input file
     * @param behaviour what to do on a cache clear
     * @return Resulting configuration
     * @throws IllegalArgumentException Thrown if file is null or it didn't exist and couldn't be created
     */
    public static ManagedConfiguration fromFile(File file, ClearCacheBehaviour behaviour) {
        Validate.notNull(file, "File cannot be null");
        ensureReadable(file);

        ManagedConfiguration config = new ManagedConfiguration(file);
        config.setClearCacheBehaviour(behaviour);
        config.tryLoad();

        return config;
    }

    /**
     * Creates a new {@link ManagedConfiguration}, loading from the given file path relative to the
     * given plugin's data folder. <p> Any errors loading the Configuration will be logged and
     * available at {@link #getError()}. If the specified input is not a valid config, a blank
     * config will be returned. </p> The encoding used may follow the system dependent default.
     *
     * @param filePath  the input file path, relative to the plugin's data folder
     * @param behaviour what to do on a cache clear
     * @param plugin    the plugin whose data folder to use
     * @return Resulting configuration
     * @throws IllegalArgumentException if file is null or it didn't exist and couldn't be created
     */
    public static ManagedConfiguration fromDataFolderPath(String filePath, ClearCacheBehaviour behaviour, MTCPlugin plugin) {
        File file = new File(plugin.getDataFolder(), filePath);
        return fromFile(file, behaviour);
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        switch (clearCacheBehaviour) {
            case RELOAD:
                tryLoad();
                break;
            case SAVE:
                asyncSave(plugin);
                break;
            case RELOAD_ON_FORCED:
                if (forced) {
                    tryLoad();
                } else {
                    asyncSave(plugin);
                }
                break;
            case NOTHING:
                break;
            default:
                throw new UnsupportedOperationException("ClearCacheBehaviour of " + clearCacheBehaviour.name() + " not supported!");
        }
    }

    public ClearCacheBehaviour getClearCacheBehaviour() {
        return clearCacheBehaviour;
    }

    public void setClearCacheBehaviour(ClearCacheBehaviour clearCacheBehaviour) {
        if (this.clearCacheBehaviour != ClearCacheBehaviour.NOTHING && clearCacheBehaviour == ClearCacheBehaviour.NOTHING) {
            CacheHelper.unregisterCache(this);
        } else {
            CacheHelper.registerCache(this); //Simple set operation, existing keys don't change the collection
        }

        this.clearCacheBehaviour = clearCacheBehaviour;
    }
}
