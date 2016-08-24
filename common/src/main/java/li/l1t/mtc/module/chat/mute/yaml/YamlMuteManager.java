/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.mute.yaml;

import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.chat.mute.api.Mute;
import li.l1t.mtc.module.chat.mute.api.MuteManager;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles management of mute metadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-08-23
 */
public class YamlMuteManager implements MuteManager {
    private final Map<UUID, Mute> mutes = new HashMap<>();
    private final ManagedConfiguration config;
    private final Plugin plugin;

    public YamlMuteManager(ManagedConfiguration config, Plugin plugin) {
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
