package io.github.xxyy.mtc.module.statsd.reporter;

import com.flozano.statsd.metrics.Metrics;
import io.github.xxyy.mtc.module.statsd.StatsdModule;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

public class TpsReporter implements StatsReporter {

    private static final double MILLIS_TO_TPS_FACTOR = 20 / 50; //tps / time

    @Getter
    private final StatsdModule module;
    private final Metrics metrics;

    private BukkitTask task;
    private TpsProvider tpsProvider;

    public TpsReporter(StatsdModule module, Metrics metrics) {
        this.module = module;
        this.metrics = metrics;
        tpsProvider = new TaskTpsProvider(this::reportTps);
    }

    public void reportTps(double tps) {
        metrics.gauge("tps").value(Math.round(tps * 10)); // send with xx.x precision as it only accepts long values

    }

    @Override
    public void start() {
        tpsProvider.start();
        task = module.getPlugin().getServer().getScheduler().runTaskTimer(module.getPlugin(), tpsProvider::reportNow, 1L, 1L);
    }

    @Override
    public void stop() {
        tpsProvider.stop();
        task.cancel();
        task = null;
    }

    public static double millisToTps(long millisDiff) {
        return ((double) millisDiff) * MILLIS_TO_TPS_FACTOR;
    }
}
