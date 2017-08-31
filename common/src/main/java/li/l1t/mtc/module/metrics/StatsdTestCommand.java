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

package li.l1t.mtc.module.metrics;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.command.MTCExecutionExecutor;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Executes the /sdtest command which allows for sending arbitrary metrics to statsd.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-21-11
 */
public class StatsdTestCommand extends MTCExecutionExecutor {
    private final StatsdModule module;

    public StatsdTestCommand(StatsdModule module) {
        this.module = module;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (!(exec.sender() instanceof ConsoleCommandSender)) {
            throw new UserException("Dieser Befehl kann nur in der Konsole ausgeführt werden.");
        }
        if (!exec.hasArg(1)) {
            respondUsage(exec);
            return true;
        }
        String type = exec.findArg(2).orElse("c");
        int value = exec.intArg(1);
        String metric = "testerino." + exec.arg(0);
        switch (type) {
            case "c":
                handleCounter(metric, value);
                break;
            case "i":
                handleIncrement(metric);
                break;
            case "g":
                handleGauge(metric, value);
                break;
            default:
                respondUsage(exec);
                throw new UserException("Invalid type.");
        }
        exec.respond(MessageType.RESULT_LINE, "Queue for sending.");
        return true;
    }

    private void handleCounter(String metric, int value) {
        module.statsd().count(metric, value);
    }

    private void handleIncrement(String metric) {
        module.statsd().increment(metric);
    }

    private void handleGauge(String metric, int value) {
        module.statsd().gauge(metric, value);
    }

    private void respondUsage(BukkitExecution exec) {
        exec.respondUsage("", "<metric> <(int) value>", "Sends a test metric of type counter");
        exec.respondUsage("", "<metric> <(int) value> [type]", "Sends a test metric of a type");
        exec.respond(MessageType.RESULT_LINE, "c=counter, g=gauge");
    }
}
