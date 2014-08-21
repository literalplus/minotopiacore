package io.github.xxyy.mtc.misc;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.junit.Test;

import io.github.xxyy.common.test.util.MockHelper;
import io.github.xxyy.mtc.api.PlayerGameManager;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MiscTest {

    @Test
    public void testGameManager() throws Exception {
        Server server = MockHelper.mockServer();
        ServicesManager servicesManager = mock(ServicesManager.class);
        when(server.getServicesManager()).thenReturn(servicesManager);
        Plugin plugin = MockHelper.mockPlugin(server);
        PlayerGameManager manager = new PlayerGameManagerImpl(plugin);
        verify(servicesManager).register(eq(PlayerGameManager.class), eq(manager), eq(plugin), any());
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
