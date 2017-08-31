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

package li.l1t.mtc.module.framework;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
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

    static class ConstructorInjectionModule extends MockMTCModule {
        public final ObjectInjectable wow;
        public final IndependentModule module;

        @InjectMe
        protected ConstructorInjectionModule(ObjectInjectable wow, IndependentModule module) {
            super("ConstructorInjectionModules");
            this.wow = wow;
            this.module = module;
        }
    }
}
