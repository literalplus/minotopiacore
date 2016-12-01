/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.simple;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Simple module that changes the hit delay (attack speed) for players at join time.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-01-12
 */
public class HitDelayModule extends ConfigurableMTCModule implements Listener {
    public static final String NAME = "HitDelay";
    private static final String ATTACK_SPEED_PATH = "attack-speed-attribute-value";
    private double attackSpeed = 4.0D;

    private HitDelayModule() {
        super(NAME, "modules/hitdelay.cfg.yml", ClearCacheBehaviour.RELOAD, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerListener(this);
    }

    @Override
    protected void reloadImpl() {
        configuration.options().copyDefaults(true);
        configuration.addDefault(ATTACK_SPEED_PATH, attackSpeed);
        configuration.trySave();
        attackSpeed = configuration.getDouble(ATTACK_SPEED_PATH);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
    }
}
