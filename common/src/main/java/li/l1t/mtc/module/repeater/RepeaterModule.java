/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
