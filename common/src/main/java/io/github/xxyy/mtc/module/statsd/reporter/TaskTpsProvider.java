package io.github.xxyy.mtc.module.statsd.reporter;

import java.util.function.Consumer;

public class TaskTpsProvider extends TpsProvider {

    private boolean running = false;
    private long last;

    protected TaskTpsProvider(Consumer<Double> tpsConsumer) {
        super(tpsConsumer);
    }

    @Override
    public void reportNow() {
        if (!running) {
            return;
        }
        long curr = System.currentTimeMillis();
        long diff = curr - last;
        double tps = TpsReporter.millisToTps(diff);
        getTpsConsumer().accept(tps);
        last = curr;
    }

    @Override
    public void start() {
        last = System.currentTimeMillis();
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }
}
