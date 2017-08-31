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

package li.l1t.mtc.resources;

import li.l1t.common.util.config.YamlHelper;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Tries to parse the plugin.yml to find errors fast.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 4.7.14
 */
public class YamlParseTest {

    @Test
    public void checkPluginYml() throws FileNotFoundException, InvalidDescriptionException {
        File file = new File("src/main/resources/plugin.yml");
        Assert.assertTrue("plugin.yml missing", file.isFile());
        new PluginDescriptionFile(new FileInputStream(file));
    }

    @Test
    public void checkLanguage() throws IOException, InvalidConfigurationException {
        File dir = new File("src/main/resources/lang/");
        Assert.assertTrue("lang folder missing or not a directory", dir.isDirectory());

        //noinspection ConstantConditions
        for (File langFile : dir.listFiles()) {
            YamlHelper.load(langFile, true);
            YamlConfiguration.loadConfiguration(langFile);
        }
    }
}
