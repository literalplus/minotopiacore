/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
