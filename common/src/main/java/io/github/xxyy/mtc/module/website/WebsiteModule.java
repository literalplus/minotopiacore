package io.github.xxyy.mtc.module.website;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.module.MTCModuleAdapter;

/**
 * <p>
 * Central entry point for the website module.
 * </p>
 * This module takes care of allowing users to confirm their accounts and change their passwords directly in-game using
 * a command. It also records the amount of time they spend on the server. Additionally, it stores online players in
 * a database so that online state can be conveniently displayed on the website.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 10/10/14
 */
public final class WebsiteModule extends MTCModuleAdapter implements Listener {
    public static final String ONLINE_TABLE_NAME = "ni176987_1_DB.onlineuser"; //lagacy name, not my choice
    public static final String PLAYTIME_TABLE_NAME = "mt_homepage.play_time";
    public static final String NAME = "Website";
    private WebsiteListener listener;

    public WebsiteModule() {
        super(NAME);
    }

    @Override
    public void enable(MTC plugin) {
        super.enable(plugin);

        listener = new WebsiteListener(plugin);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable(MTC plugin) {
        HandlerList.unregisterAll(listener); //We'll get some reload functionality working for modules eventually
    }
}
