package io.github.xxyy.mtc.misc;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import io.github.xxyy.common.util.XyValidate;
import io.github.xxyy.mtc.api.PlayerGameManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MTC's implementation of PlayerGameManager.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 21.8.14
 */
public class PlayerGameManagerImpl implements PlayerGameManager {
    private Map<UUID, Plugin> playersInGames = new HashMap<>();

    public PlayerGameManagerImpl(Plugin plugin) {
        plugin.getServer().getServicesManager().register(PlayerGameManager.class, this, plugin, ServicePriority.Low);
    }

    @Override
    public void setInGame(boolean inGame, UUID uuid, Plugin plugin) throws IllegalStateException {
        Validate.notNull(uuid, "UUID cannot be null!");
        Validate.notNull(plugin, "plugin cannot be null!");
        if(inGame) {
            XyValidate.validateState(!isInGame(uuid), "Player can only be in one game at the same time!");
            playersInGames.put(uuid, plugin);
        } else {
            XyValidate.validateState(plugin.equals(getProvidingPlugin(uuid)), "Can't remove other plugins' entries!");
            playersInGames.remove(uuid);
        }
    }

    @Override
    public Plugin getProvidingPlugin(UUID uuid) {
        return playersInGames.get(uuid);
    }
}
