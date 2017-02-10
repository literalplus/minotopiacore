/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.removal;

import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.hook.VaultHook;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reads removal handler specifications from a YAML configuration.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-10
 */
public class HandlerReader {
    @InjectMe(required = false)
    private VaultHook vaultHook;

    public List<RemovalHandler> readHandlers(ConfigurationSection section, String key) {
        return section.getList(key, Collections.emptyList()).stream()
                .map(Map.class::cast)
                .map(HashMapConfig::of)
                .map(this::instantiateHandler)
                .collect(Collectors.toList());
    }

    private RemovalHandler instantiateHandler(MapConfig config) {
        String type = config.findString("type").orElseThrow(() -> new HandlerConfigException("missing type"));
        switch (type.toLowerCase()) {
            case "random":
                return instantiateRandomChanceHandler(config);
            case "economy":
                return instantiateEconomyHandler(config);
            default:
                throw new HandlerConfigException("Unknown type: " + type);
        }
    }

    private RandomChanceHandler instantiateRandomChanceHandler(MapConfig config) {
        return new RandomChanceHandler(
                config.findTyped("droprate", Number.class).map(Number::intValue)
                        .orElseThrow(() -> new HandlerConfigException("Random chance handler needs droprate!"))
        );
    }

    private EconomyHandler instantiateEconomyHandler(MapConfig config) {
        if (vaultHook == null) {
            throw new HandlerConfigException("Cannot use economy handler if Vault is not hooked");
        }
        return new EconomyHandler(
                config.findTyped("cost", Number.class).map(Number::doubleValue)
                        .orElseThrow(() -> new HandlerConfigException("Economy handler needs cost!")),
                vaultHook
        );
    }
}
