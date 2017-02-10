/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.removal.HandlerReader;
import li.l1t.mtc.module.blocklock.removal.RemovalHandler;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.bukkit.Material;

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
        targetMaterials.forEach(material -> readAndRegisterHandlerFor(material, configuration));
    }

    private List<RemovalHandler> readAndRegisterHandlerFor(Material material, ManagedConfiguration configuration) {
        return materialRemovalHandlers.put(material, handlerReader.readHandlers(configuration, material.name()));
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
