/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.misc;

import li.l1t.common.test.util.MockHelper;
import li.l1t.common.test.util.mokkit.MockServer;
import li.l1t.mtc.api.PlayerGameManager;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MiscTest {

    @Test
    public void testGameManager() throws Exception {
        MockServer server = MockHelper.mockServer();
        Plugin plugin = MockHelper.mockPlugin(server);
        PlayerGameManager manager = new PlayerGameManagerImpl(plugin);
        assertThat(server.getServicesManager().getRegistration(PlayerGameManager.class).getProvider(), is(manager));
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        assertThat(manager.isInGame(id1), is(false));
        manager.setInGame(true, id1, plugin);
        assertThat(manager.isInGame(id1), is(true));
        assertThat(manager.isInGame(id2), is(false));
        assertThat(manager.getProvidingPlugin(id1), is(plugin));
        manager.setInGame(false, id1, plugin);
        assertThat(manager.isInGame(id1), is(false));
        assertThat(manager.getProvidingPlugin(id1), nullValue());
    }
}
