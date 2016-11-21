/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.metrics;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import me.minotopia.statsd_bukkit.NonBlockingStatsDClient;
import me.minotopia.statsd_bukkit.StatsDClientErrorHandler;
import org.apache.logging.log4j.Logger;

/**
 * Provides a Statsd API to other modules.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-21-11
 */
public class StatsdModule extends ConfigurableMTCModule implements StatsDClientErrorHandler {
    public static final String NAME = "StatsdApi";
    private static final Logger LOGGER = LogManager.getLogger(StatsdModule.class);
    private static final String ADDRESS_PATH = "statsd.host";
    private static final String PORT_PATH = "statsd.port";
    private static final String PREFIX_PATH = "statsd.prefix";
    private NonBlockingStatsDClient statsd;
    private String address;
    private String prefix;
    private int port;

    protected StatsdModule() {
        super(NAME, "modules/statsd.cfg.yml", ClearCacheBehaviour.RELOAD, true);
    }

    @Override
    protected void reloadImpl() {
        configuration.options().copyDefaults(true).header("Common Statsd config for all MTC modules");
        configuration.addDefault(PREFIX_PATH, "mtc.unknownserver");
        configuration.addDefault(ADDRESS_PATH, "localhost");
        configuration.addDefault(PORT_PATH, 8125);
        address = configuration.getString(ADDRESS_PATH);
        port = configuration.getInt(PORT_PATH);
        prefix = configuration.getString(PREFIX_PATH);
        configuration.asyncSave(getPlugin());
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(new StatsdTestCommand(this), "sdtest")
                .behaviour(CommandBehaviours.permissionChecking("mtc.statsdtest"));
    }

    public NonBlockingStatsDClient statsd() {
        if(statsd == null) {
            statsd = createNewClient();
        }
        return statsd;
    }

    private NonBlockingStatsDClient createNewClient() {
        LOGGER.info("(Re-)connecting to Statsd server at {}:{} (prefix '{}')", address, port, prefix);
        return new NonBlockingStatsDClient(prefix, address, port, getPlugin(), this);
    }

    @Override
    public void handle(Exception e) {
        LOGGER.warn("Error writing to Statsd", e);
        statsd = createNewClient();
    }
}
