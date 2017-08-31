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

package li.l1t.mtc.module.blocklock;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.removal.HandlerReader;
import li.l1t.mtc.module.blocklock.removal.RemovalHandler;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * Manages access to the block lock configuration file.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
public class BlockLockConfig {
    private static final String TARGET_MATERIALS_PATH = "target-materials";
    private final HandlerReader handlerReader;
    private Set<Material> targetMaterials = new HashSet<>();
    private Map<Material, List<RemovalHandler>> materialRemovalHandlers = new HashMap<>();

    @InjectMe
    public BlockLockConfig(HandlerReader handlerReader) {
        this.handlerReader = handlerReader;
    }

    public void loadFrom(ManagedConfiguration configuration) {
        registerDefaults(configuration);
        targetMaterials.clear();
        configuration.getStringList(TARGET_MATERIALS_PATH).stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .forEach(targetMaterials::add);
        materialRemovalHandlers.clear();
        ConfigurationSection section = getOrCreateSection("removal-handlers", configuration);
        targetMaterials.forEach(material -> {
            readAndRegisterHandlerFor(material, section);
        });
    }

    private ConfigurationSection getOrCreateSection(String path, ConfigurationSection root) {
        ConfigurationSection section = root.getConfigurationSection(path);
        if (section == null) {
            return root.createSection(path);
        } else {
            return section;
        }
    }

    private List<RemovalHandler> readAndRegisterHandlerFor(Material material, ConfigurationSection section) {
        return materialRemovalHandlers.put(material, handlerReader.readHandlers(section, material.name()));
    }

    private void registerDefaults(ManagedConfiguration configuration) {
        configuration.options().copyDefaults(true);
        configuration.addDefault(TARGET_MATERIALS_PATH, Collections.singletonList(Material.BEDROCK.name()));
    }

    public Set<Material> getTargetMaterials() {
        return targetMaterials;
    }

    public boolean isTargetedMaterial(Material material) {
        return targetMaterials.contains(material);
    }

    public List<RemovalHandler> getRemovalHandlersFor(Material material) {
        return materialRemovalHandlers.getOrDefault(material, Collections.emptyList());
    }
}
