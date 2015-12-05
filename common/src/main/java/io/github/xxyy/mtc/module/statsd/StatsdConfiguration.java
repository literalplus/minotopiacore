package io.github.xxyy.mtc.module.statsd;

import io.github.xxyy.mtc.yaml.ManagedConfiguration;

import java.io.IOException;

public class StatsdConfiguration {

    private final ManagedConfiguration cfg;

    public StatsdConfiguration(ManagedConfiguration cfg) {
        this.cfg = cfg;
    }

    public void setDefaults() {
        cfg.addDefault("connection.host", "localhost");
        cfg.addDefault("connection.port", 8125);

        cfg.addDefault("flushrate", 1);
        cfg.addDefault("metrics-prefix", "pvp");
        try {
            cfg.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ManagedConfiguration getConfiguration() {
        return cfg;
    }

    public String getConnectionHost() {
        return cfg.getString("connection.host");
    }

    public int getConnectionPort() {
        return cfg.getInt("connection.port");
    }

    /**
     * @return flush rate
     * @see com.flozano.statsd.client.ClientBuilder#withFlushRate(double)
     */
    public int getFlushRate() {
        return cfg.getInt("flushrate");
    }

    public String getPrefix() {
        return cfg.getString("metrics-prefix");
    }
}
