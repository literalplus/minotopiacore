/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock;

import li.l1t.mtc.yaml.ManagedConfiguration;
import org.bukkit.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Manages access to the block lock configuration file.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-31
 */
public class BlockLockConfig {
    private static final String TARGET_MATERIALS_PATH = "target-materials";
    private Set<Material> targetMaterials = new HashSet<>();

    public void loadFrom(ManagedConfiguration configuration) {
        registerDefaults(configuration);
        targetMaterials.clear();
        configuration.getStringList(TARGET_MATERIALS_PATH).stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .forEach(targetMaterials::add);
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
}
