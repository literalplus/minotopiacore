package io.github.xxyy.mtc.module.statsd.reporter;


import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * @see <a href="https://gist.github.com/vemacs/6a345b2f9822b79a9a7f">https://gist.github.com/vemacs/6a345b2f9822b79a9a7f</a>
 * @author vemacs, Janmm14 (modifications)
 */
public class NmsReflectionTpsProvider extends TpsProvider {

    private boolean running = false;

    private Object minecraftServer;
    private Field recentTpsField;

    @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
    protected NmsReflectionTpsProvider(Consumer<Double> tpsConsumer) {
        super(tpsConsumer);
        //initialize reflection
        Server server = Bukkit.getServer();
        Field consoleField = server.getClass().getDeclaredField("console");
        consoleField.setAccessible(true);
        minecraftServer = consoleField.get(server);
        recentTpsField = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
        recentTpsField.setAccessible(true);
    }

    @Override
    public void reportNow() {
        if (!running) {
            return;
        }
        getTpsConsumer().accept(getTps());
    }

    private double getTps() {
        try {
            double[] recentTps = (double[]) recentTpsField.get(minecraftServer);
            if (recentTps != null && recentTps.length != 0) {
                return recentTps[0]; //TODO is this the latest tps?
            }
        } catch (IllegalAccessException ignored) {
        }
        return -1;
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }
}
