/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */
package io.github.xxyy.mtc.module.repeater;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Repeats stuffs
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
public class RepeaterModule extends ConfigurableMTCModule {
    public static final String NAME = "Repeater";
    private static final String MESSAGES_PATH = "messages";
    public static final String ADMIN_PERMISSION = "mtc.repeater.admin";
    public static final int SECONDS_PER_TICK = 5;
    private List<RepeatingMessage> messages = new ArrayList<>();

    public RepeaterModule() {
        super(NAME, "modules/messages.conf.yml", ClearCacheBehaviour.SAVE);
        ConfigurationSerialization.registerClass(RepeatingMessage.class);
    }

    @Override
    public void enable(MTC plugin) {
        super.enable(plugin);

        plugin.getCommand("repeat").setExecutor(new CommandRepeat(this));
        plugin.getServer().getScheduler().runTaskTimer(plugin, new RepeaterTask(this), 5 * SECONDS_PER_TICK * 20L, SECONDS_PER_TICK * 20L);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        messages = (List<RepeatingMessage>) configuration.getList(MESSAGES_PATH, messages);
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
