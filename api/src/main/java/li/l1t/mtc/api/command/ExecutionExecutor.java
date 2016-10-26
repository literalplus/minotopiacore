/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.command;

import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;

/**
 * Something that executes commands specified by {@link CommandExecution} instances.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-26
 */
public interface ExecutionExecutor {
    /**
     * Executes the command specified by given execution.
     *
     * @param exec the execution to process
     * @return whether the execution was "successful". An error message may be shown by the caller
     * if the execution was unsuccessful.
     * @throws UserException     if incorrect user input led to an error
     * @throws InternalException if internal system conditions led to an error
     */
    boolean execute(CommandExecution exec) throws UserException, InternalException;
}
