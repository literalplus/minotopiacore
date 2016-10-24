/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module;

import li.l1t.mtc.MTC;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.MTCModule;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.api.module.inject.InjectionTarget;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Tests the module loading facilities.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 18/06/15
 */
public class ModuleLoaderTest {
    private static SimpleModuleManager moduleManager;
    private static ModuleLoader loader;
    private static MTC mtc;

    @BeforeClass
    public static void initClass() {
        mtc = mock(MTC.class);
        moduleManager = new SimpleModuleManager(mtc, new File("./target/"));
        loader = new ModuleLoader(moduleManager);
    }

    private <T extends MTCModule> InjectionTarget<T> instance(Class<T> moduleClass) {
        return loader.getLoadedModule(moduleClass);
    }

    @Test
    public void testLoadAll() throws Exception {
        loader.loadAll(Arrays.asList(Module3.class, TestModules.IndependentModule.class, Module2.class, Module4.class),
                errorConsumer());

        List<MTCModule> loaded2 = loader.setEnabled(instance(Module2.class), true);
        assertTrue(loaded2.stream().anyMatch(m -> m instanceof TestModules.IndependentModule)); //check that it actually returns enabled modules
        assertTrue(loaded2.stream().anyMatch(m -> m instanceof Module2));

        assertEnabledStates(true, true, false, false);
        assertThat(module(Module2.class).independentModule, is(module(TestModules.IndependentModule.class)));
        assertThat(module(Module2.class).module3, is(nullValue()));

        loader.setEnabled(instance(Module3.class), true);
        assertEnabledStates(true, true, true, false);
        assertThat(module(Module3.class).independentModule, is(module(TestModules.IndependentModule.class)));
        assertThat(module(Module3.class).module2, is(module(Module2.class)));
        assertThat(module(Module3.class).module4, is(nullValue()));
        assertThat(module(Module2.class).module3, is(module(Module3.class)));

        loader.setEnabled(instance(TestModules.IndependentModule.class), false);
        assertEnabledStates(false, false, true, false);
        assertThat(module(Module3.class).independentModule, is(nullValue()));
        assertThat(module(Module3.class).module2, is(nullValue()));
        assertThat(module(TestModules.IndependentModule.class), is(nullValue()));
        assertThat(module(Module2.class), is(nullValue()));
        assertThat(module(Module3.class).module4, is(module(Module4.class)));

        loader.setEnabled(instance(TestModules.IndependentModule.class), true);
        assertEnabledStates(true, false, true, false);
        assertThat(module(Module3.class).independentModule, is(module(TestModules.IndependentModule.class)));
    }

    private <M extends MTCModule> M module(Class<M> clazz) {
        return moduleManager.getModule(clazz);
    }

    private BiConsumer<InjectionTarget<?>, Throwable> errorConsumer() {
        return (meta, thrown) -> {
            thrown.printStackTrace();
            Assert.fail("Exception while loading " + meta + "!");
        };
    }

    @Test
    public void testExceptionSandbox() throws Exception {
        setMockPluginLogger();
        loader.loadAll(Arrays.asList(TestModules.LoadExceptionModule.class, TestModules.EnableExceptionModule.class),
                (meta, thrown) -> assertThat("wrong module failed to load",
                        meta.getClazz(), sameInstance(TestModules.LoadExceptionModule.class))
        );

        assertThat(loader.getLoadedModule(TestModules.LoadExceptionModule.class), is(nullValue()));

        loader.setEnabled(instance(TestModules.EnableExceptionModule.class), true);
    }

    private void setMockPluginLogger() throws NoSuchFieldException, IllegalAccessException {
        Logger logger = mock(PluginLogger.class); // final method in JavaPlugin
        Field field = JavaPlugin.class.getDeclaredField("logger");
        field.setAccessible(true);
        field.set(mtc, logger); // we need this for the logger call in registerEnabled(...)
    }

    @Test
    public void testConstructorInjection() {
        loader.loadAll(
                Arrays.asList(TestModules.IndependentModule.class, TestModules.ConstructorInjectionModule.class),
                errorConsumer()
        );
        List<MTCModule> enabled = loader.setEnabled(instance(TestModules.ConstructorInjectionModule.class), true);
        assertThat(enabled, hasItem(any(TestModules.IndependentModule.class)));
        assertThat(module(TestModules.ConstructorInjectionModule.class).module, is(not(nullValue())));
        assertThat(module(TestModules.ConstructorInjectionModule.class).wow, is(not(nullValue())));
    }

    private void assertEnabledStates(boolean m1, boolean m2, boolean m3, boolean m4) {
        assertModuleState(m1, TestModules.IndependentModule.class);
        assertModuleState(m2, Module2.class);
        assertModuleState(m3, Module3.class);
        assertModuleState(m4, Module4.class);
    }

    private void assertModuleState(boolean expectedState, Class<? extends MTCModule> clazz) {
        assertThat(clazz.getSimpleName() + " has wrong enabled state",
                moduleManager.isEnabled(clazz), is(expectedState));
    }

    private static class Module2 extends MockMTCModule {
        @InjectMe
        TestModules.IndependentModule independentModule;

        @InjectMe(required = false)
        Module3 module3;

        TestModules.IndependentModule ordinaryField;

        protected Module2() {
            super("Module2");
        }
    }

    private static class Module3 extends MockMTCModule {
        @InjectMe(required = false)
        TestModules.IndependentModule independentModule;

        @InjectMe(required = false)
        Module2 module2;

        @InjectMe(required = false)
        Module4 module4;

        @InjectMe
        TestModules.ObjectInjectable anyClass;

        protected Module3() {
            super("Module3");
        }

        @Override
        public void enable(MTCPlugin plugin) throws Exception {
            super.enable(plugin);
            anyClass.wow(); //wow
        }
    }

    private static class Module4 extends MockMTCModule {

        protected Module4() {
            super("Module4");
        }
    }

}
