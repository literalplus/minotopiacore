/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
