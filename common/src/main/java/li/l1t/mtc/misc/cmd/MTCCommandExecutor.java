/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.misc.cmd;

import li.l1t.common.cmd.XYCCommandExecutor;
import li.l1t.mtc.api.command.CommandBehaviour;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.misc.Cache;
import li.l1t.mtc.misc.CacheHelper;
import li.l1t.mtc.module.command.MTCBehaviours;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract base class for command executors implementing some default behaviours for MTC and
 * allowing usage of the {@link CommandBehaviour} API with unsupported {@link Command}s.
 *
 * @author xxyy
 */
public abstract class MTCCommandExecutor extends XYCCommandExecutor implements Cache {
    private final List<CommandBehaviour> behaviours = new ArrayList<>(Arrays.asList(
            MTCBehaviours.messagesChecking(), MTCBehaviours.mtcCrediting()
    ));

    public MTCCommandExecutor() {
        CacheHelper.registerCache(this);
    }

    /**
     * Clears this executor's behaviour list.
     *
     * @return this executor, for call chaining
     */
    public MTCCommandExecutor clearBehaviours() {
        behaviours.clear();
        return this;
    }

    /**
     * Adds behaviours to this executor. Behaviours are applied before execution and implement
     * common pre-execution checks.
     *
     * @param behaviours the behaviours to add
     * @return this executor, for call chaining
     * @see CommandBehaviours
     */
    public MTCCommandExecutor behaviour(CommandBehaviour... behaviours) {
        this.behaviours.addAll(Arrays.asList(behaviours));
        return this;
    }

    @Override
    public final boolean preCatch(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        for (CommandBehaviour behaviour : behaviours) {
            if (!behaviour.apply(sender, label, cmd, args)) {
                return false;
            }
        }
        return true;
    }

}
