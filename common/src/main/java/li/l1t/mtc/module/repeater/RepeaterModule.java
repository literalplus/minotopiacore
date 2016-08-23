/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.repeater;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Repeats stuffs
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
public class RepeaterModule extends ConfigurableMTCModule {
    public static final String NAME = "Repeater";
    public static final String ADMIN_PERMISSION = "mtc.repeater.admin";
    public static final int SECONDS_PER_TICK = 5;
    private static final String MESSAGES_PATH = "messages";
    private List<RepeatingMessage> messages = new ArrayList<>();

    public RepeaterModule() {
        super(NAME, "modules/messages.conf.yml", ClearCacheBehaviour.SAVE, true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        ConfigurationSerialization.registerClass(RepeatingMessage.class);
        ConfigurationSerialization.registerClass(RepeatingMessage.class, "io.github.xxyy.mtc.module.repeater.RepeatingMessage"); //TODO: temporary compat
        super.enable(plugin);

        registerCommand(new CommandRepeat(this), "repeat", "rpt")
                .behaviour(CommandBehaviours.permissionChecking(RepeaterModule.ADMIN_PERMISSION));
        plugin.getServer().getScheduler().runTaskTimer(plugin, new RepeaterTask(this), 5 * SECONDS_PER_TICK * 20L, SECONDS_PER_TICK * 20L);
    }

    @Override
    public void disable(MTCPlugin plugin) {
        ConfigurationSerialization.unregisterClass(RepeatingMessage.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        messages = ((List<RepeatingMessage>) configuration.getList(MESSAGES_PATH, messages));
        messages.removeIf(Objects::isNull);
    }

    @Override
    public void save() {
        configuration.set(MESSAGES_PATH, messages);
        super.save();
    }

    public List<RepeatingMessage> getMessages() {
        return messages;
    }
}
