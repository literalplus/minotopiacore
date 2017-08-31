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

package li.l1t.mtc.module.command;

import li.l1t.mtc.api.module.MTCModule;
import li.l1t.mtc.api.module.ModuleCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * A module command with some MTC implementation-specific features.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-31
 */ //The whole thing would be an interface if not for Command being a class
public class MTCModuleCommand extends ModuleCommand {
    public MTCModuleCommand(MTCModule module, String name, CommandExecutor executor, String... aliases) {
        super(module, name, executor, aliases);
        addMTCBehaviours();
    }

    public MTCModuleCommand(MTCModule module, String name, CommandExecutor executor, TabCompleter completer,
                            String description, String usageMessage, String... aliases) {
        super(module, name, executor, completer, description, usageMessage, aliases);
        addMTCBehaviours();
    }

    private void addMTCBehaviours() {
        behaviour(MTCBehaviours.mtcCrediting(), MTCBehaviours.messagesChecking(), MTCBehaviours.mtcCrediting());
    }
}
