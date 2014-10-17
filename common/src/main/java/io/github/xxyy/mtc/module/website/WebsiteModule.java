package io.github.xxyy.mtc.module.website;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
public final class WebsiteModule extends ConfigurableMTCModule implements Listener {
    public static final String ONLINE_TABLE_NAME = "ni176987_1_DB.onlineuser"; //legacy name, not my choice
    public static final String PLAYTIME_TABLE_NAME = "mt_homepage.play_time";
    public static final String WEBSITE_USER_TABLE_NAME = "ni176987_1_DB.hp_user"; //legacy name
    public static final String NAME = "Website";
    private static final String PASSWORD_CHANGING_PATH = "password-changing";

    private final Map<UUID, Instant> playerJoinTimes = new ConcurrentHashMap<>();
    private boolean passwordChangeEnabled = true;
    private WebsiteListener listener;

    public WebsiteModule() {
        super(NAME, "website_config.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTC plugin) {
        super.enable(plugin);

        listener = new WebsiteListener(this);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getCommand("website").setExecutor(new CommandWebsite(this));
    }

    @Override
    protected void reloadImpl() {
        configuration.addDefault(PASSWORD_CHANGING_PATH, true);
        configuration.options().copyDefaults(true);

        passwordChangeEnabled = configuration.getBoolean(PASSWORD_CHANGING_PATH);
    }

    @Override
    public void disable(MTC plugin) {
        HandlerList.unregisterAll(listener); //We'll get some reload functionality working for modules eventually
    }

    public boolean isPasswordChangeEnabled() {
        return passwordChangeEnabled;
    }

    /**
     * Saves the join time for given UUID to the current time. This is used to count the time played.
     * @param uuid the unique id of the player who joined
     */
    void registerJoinTime(UUID uuid) {
        playerJoinTimes.put(uuid, Instant.now());
    }

    long getMinutesPlayed(UUID uuid) {
        long minutesPlayed = ChronoUnit.MINUTES.between(
                playerJoinTimes.getOrDefault(uuid, Instant.now()), //Just making sure
                Instant.now());
        playerJoinTimes.remove(uuid);
        return minutesPlayed;
    }
}
