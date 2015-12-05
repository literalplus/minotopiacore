package io.github.xxyy.mtc.module.statsd.reporter;

import com.flozano.statsd.metrics.Metrics;
import io.github.xxyy.mtc.module.statsd.StatsdModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnlineCountReporter implements Listener {

    private final StatsdModule module;
    private final Metrics metrics;

    public OnlineCountReporter(StatsdModule module, Metrics metrics) {
        this.module = module;
        this.metrics = metrics;
    }

    public void start() {
        module.getPlugin().getServer().getPluginManager().registerEvents(this, module.getPlugin());
    }

    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        metrics.gauge("online-counter").value(module.getPlugin().getServer().getOnlinePlayers().size());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        metrics.gauge("online-counter").value(module.getPlugin().getServer().getOnlinePlayers().size());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        metrics.gauge("online-counter").value(module.getPlugin().getServer().getOnlinePlayers().size());
    }
}
