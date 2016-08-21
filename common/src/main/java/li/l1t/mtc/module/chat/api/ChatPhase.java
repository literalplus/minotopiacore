/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.api;

/**
 * Represents phases of chat handling. Phases with lower ordinal (that appear earlier in this enum)
 * are executed earlier.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public enum ChatPhase {
    /**
     * Initialising handlers, such as these who fetch chat prefixes from external APIs.
     */
    INITIALISING,
    /**
     * Checking handlers, that listen for specific message patterns for use in other subsystems and
     * drop matching messages.
     */
    CHECKING,
    /**
     * Blocking handlers, that block all messages based on external conditions.
     */
    BLOCKING,
    /**
     * Filtering handlers, that block messages based on content.
     */
    FILTERING,
    /**
     * Censoring handlers, that replace forbidden (parts of) messages.
     */
    CENSORING,
    /**
     * Decorating handlers, that change messages to look better or be funnier.
     */
    DECORATING,
    /**
     * Forwarding handlers, that drop some messages and forward them to another subsystem.
     */
    FORWARDING,
    /**
     * Monitoring handlers, that just listen to messages but don't change them.
     */
    MONITORING
}
