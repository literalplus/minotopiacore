package io.github.xxyy.mtc.module.statsd.reporter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TpsProvider {

    @Getter(AccessLevel.PROTECTED)
    private final java.util.function.Consumer<Double> tpsConsumer;

    public abstract void reportNow();

    public abstract void start();

    public abstract void stop();
}
