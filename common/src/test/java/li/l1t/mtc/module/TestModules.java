/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.Injectable;

/**
 * Wrapper class for test modules
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-24
 */
class TestModules {
    static class ObjectInjectable implements Injectable {
        public void wow() {

        }
    }

    static class IndependentModule extends MockMTCModule {
        protected IndependentModule() {
            super("Module1");
        }
    }

    static class LoadExceptionModule extends MockMTCModule {
        protected LoadExceptionModule() {
            super("LoadExceptionModule");
            throw new NoClassDefFoundError("testing errors in load");
        }
    }

    static class EnableExceptionModule extends MockMTCModule {

        protected EnableExceptionModule() {
            super("EnableExceptionModule");
        }

        @Override
        public void enable(MTCPlugin plugin) throws Exception {
            super.enable(plugin);
            throw new Exception("ignore");
        }

        @Override
        public void disable(MTCPlugin plugin) {
            throw new NullPointerException("random exception in disable");
        }
    }
}
