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

package li.l1t.mtc.module.chat.mute.yaml;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.chat.mute.api.Mute;
import li.l1t.mtc.module.chat.mute.api.MuteManager;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Handles management of mute metadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-08-23
 */
public class YamlMuteManager implements MuteManager {
    private final Map<UUID, Mute> mutes = new HashMap<>();
    private final ManagedConfiguration config;
    private final MTCPlugin plugin;

    public YamlMuteManager(ManagedConfiguration config, MTCPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        loadFromConfiguration();
    }

    private void loadFromConfiguration() {
        config.options().header(" ~~~ DATA STORE ONLY - DO NOT EDIT - ANY CHANGES WILL BE OVERWRITTEN ~~~\n");
        List<Mute> configuredMutes = config.getListChecked("mutes", YamlBackedMute.class);
        configuredMutes.forEach(mute -> mutes.put(mute.getPlayerId(), mute));
    }

    private void saveToConfiguration() {
        config.set("mutes", new ArrayList<>(mutes.values()));
        config.asyncSave(plugin);
    }

    @Override
    public boolean isCurrentlyMuted(XLoginHook.Profile profile) {
        return isCurrentlyMuted(profile.getUniqueId());
    }

    @Override
    public boolean isCurrentlyMuted(Player player) {
        return isCurrentlyMuted(player.getUniqueId());
    }

    public boolean isCurrentlyMuted(UUID playerId) {
        if (!mutes.containsKey(playerId)) {
            return false;
        }
        Mute mute = getMuteFor(playerId);
        return !mute.hasExpired();
    }

    @Override
    public Mute getMuteFor(XLoginHook.Profile profile) {
        return getMuteFor(profile.getUniqueId());
    }

    public Mute getMuteFor(UUID playerId) {
        return mutes.computeIfAbsent(playerId, YamlBackedMute::new);
    }

    @Override
    public void saveMute(Mute mute) {
        mutes.put(mute.getPlayerId(), mute);
        saveToConfiguration();
    }

    @Override
    public boolean removeMute(XLoginHook.Profile profile) {
        return removeMute(profile.getUniqueId());
    }

    private boolean removeMute(UUID playerId) {
        boolean mutedBefore = isCurrentlyMuted(playerId);
        mutes.remove(playerId);
        saveToConfiguration();
        return mutedBefore;
    }
}
