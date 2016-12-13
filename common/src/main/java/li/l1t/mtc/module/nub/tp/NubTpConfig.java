/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.tp;

import li.l1t.mtc.yaml.ManagedConfiguration;

/**
 * Stores configuration values for the N.u.b. teleport module.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class NubTpConfig {
    private static final String MIN_COORDINATES_PATH = "valid-coordinate-range-both-x-and-z.min-pos-and-neg";
    private static final String MAX_COORDINATES_PATH = "valid-coordinate-range-both-x-and-z.max-pos-and-neg";
    private static final String VAULT_COST_PATH = "payment.vault-money-cost-per-tp";
    private static final String WORLD_NAME_PATH = "teleport-world-name";
    private int minCoordinate;
    private int maxCoordinate;
    private int vaultTeleportCost;
    private String teleportWorldName;

    void loadFrom(ManagedConfiguration config) {
        config.options().copyDefaults(true);
        config.addDefault(MIN_COORDINATES_PATH, 1_000);
        config.addDefault(MAX_COORDINATES_PATH, 15_000);
        config.addDefault(VAULT_COST_PATH, 100);
        config.addDefault(WORLD_NAME_PATH, "world");
        minCoordinate = config.getInt(MIN_COORDINATES_PATH);
        maxCoordinate = config.getInt(MAX_COORDINATES_PATH);
        vaultTeleportCost = config.getInt(VAULT_COST_PATH);
        teleportWorldName = config.getString(WORLD_NAME_PATH);
        config.trySave();
    }

    public int getMinCoordinate() {
        return minCoordinate;
    }

    public int getMaxCoordinate() {
        return maxCoordinate;
    }

    public int getVaultTeleportCost() {
        return vaultTeleportCost;
    }

    public String getTeleportWorldName() {
        return teleportWorldName;
    }
}