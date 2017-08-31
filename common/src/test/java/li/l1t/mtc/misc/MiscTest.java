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
