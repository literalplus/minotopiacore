package io.github.xxyy.minotopiacore.test.resources;

import io.github.xxyy.minotopiacore.test.TestHelper;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * Tries to parse the plugin.yml to find errors fast.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 4.7.14
 */
public class YamlParseTest {
    private static final Server SERVER = TestHelper.mockServer();

    @Test
    public void checkPluginYml() throws FileNotFoundException, InvalidDescriptionException {
        File file = new File("src/main/resources/plugin.yml");
        Assert.assertTrue("plugin.yml missing", file.isFile());
        new PluginDescriptionFile(new FileInputStream(file));
    }

    @Test
    public void checkLanguage() {
        File dir = new File("src/main/resources/lang/");
        Assert.assertTrue("lang folder missing or not a directory", dir.isDirectory());
        Logger fakeLogger = spy(Logger.getLogger(getClass().getName()));
        when(SERVER.getLogger()).thenReturn(fakeLogger);

        doAnswer(inv -> {
            throw new IllegalStateException("Couldn't load some file: " + inv.getArguments()[1], (Throwable) inv.getArguments()[2]);
        }).when(fakeLogger).log(eq(Level.SEVERE), contains("Cannot load "), any(Throwable.class));

        //noinspection ConstantConditions
        for (File langFile : dir.listFiles()) { //FIXME We need a way to know if this failed....This shitty method only logs and ignores :/
            YamlConfiguration.loadConfiguration(langFile);
        }
    }
}
