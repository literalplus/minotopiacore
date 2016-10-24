/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module;

import li.l1t.mtc.api.MTCPlugin;

class MockMTCModule extends MTCModuleAdapter {
    protected MockMTCModule(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("RefusedBequest")
    public boolean canBeEnabled(MTCPlugin plugin) {
        return true;
    }
}
