package io.github.xxyy.mtc.module.statsd;

import com.flozano.statsd.client.ClientBuilder;
import com.flozano.statsd.metrics.Metrics;
import com.flozano.statsd.metrics.MetricsBuilder;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import io.github.xxyy.mtc.module.statsd.reporter.OnlineCountReporter;
import io.github.xxyy.mtc.module.statsd.reporter.TpsReporter;

import java.time.Clock;

@SuppressWarnings("unused")
public class StatsdModule extends ConfigurableMTCModule {

    public static final String NAME = "statsd";

    private StatsdConfiguration statsdCfg;
    private Metrics metrics;
    private TpsReporter tpsReporter;
    private OnlineCountReporter onlineCountReporter;

    public StatsdModule() {
        super(NAME, "modules/statsd/config.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        enable0();
    }

    private void enable0() {
        statsdCfg = new StatsdConfiguration(configuration);
        statsdCfg.setDefaults();

        metrics = MetricsBuilder.create()
            .withClient(ClientBuilder.create()
                .withHost(statsdCfg.getConnectionHost())
                .withPort(statsdCfg.getConnectionPort())
                .withFlushRate(statsdCfg.getFlushRate())
                .withSampleRate(1.0D) //send 100% of metrics
                .build())
            .withClock(Clock.systemDefaultZone()) //TODO is this the correct clock?
            .withPrefix(statsdCfg.getPrefix())
            .build();
        tpsReporter = new TpsReporter(this, metrics);
        tpsReporter.start();
        onlineCountReporter = new OnlineCountReporter(this, metrics);
    }

    @Override
    public void disable(MTC plugin) {
        disable0();
    }

    private void disable0() {
        tpsReporter.stop();
        tpsReporter = null;
        onlineCountReporter.stop();
        onlineCountReporter = null;
        metrics.close();
        metrics = null;
        statsdCfg = null;
    }

    @Override
    protected void reloadImpl() {
        disable0();
        enable0();
    }
}
