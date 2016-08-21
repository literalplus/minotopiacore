/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.impl;

import com.google.common.base.Preconditions;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatHandler;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * A chat message event that proxies a Bukkit event.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class BukkitChatMessageEvent implements ChatMessageEvent {
    private static final Logger LOGGER = LogManager.getLogger(ChatMessageEvent.class);
    private final AsyncPlayerChatEvent bukkitEvent;
    private final ChatModule module;
    private String message;
    private String prefix = "";
    private String suffix = "";
    private boolean handlingStopped = false;

    public BukkitChatMessageEvent(AsyncPlayerChatEvent bukkitEvent, ChatModule module) {
        this.bukkitEvent = Preconditions.checkNotNull(bukkitEvent, "bukkitEvent");
        this.message = bukkitEvent.getMessage();
        this.module = module;
    }

    @Override
    public boolean tryDenyMessage(String errorMessage, ChatHandler handler) {
        if (mayBypassFilters()) {
            sendPrefixed(String.format(
                    "Chatfilter %s ignoriert.",
                    handler.getClass().getSimpleName()
            ));
            return false;
        }
        sendPrefixed(errorMessage);
        bukkitEvent.setCancelled(true);
        return true;
    }

    @Override
    public boolean mayBypassFilters() {
        return getPlayer().hasPermission("mtc.ignore");
    }

    @Override
    public void sendPrefixed(String errorMessage) {
        getPlayer().sendMessage(module.formatMessage(errorMessage));
    }

    @Override
    public void dropMessage() {
        bukkitEvent.setCancelled(true);
    }

    @Override
    public void stopHandling() {
        this.handlingStopped = true;
    }

    @Override
    public void logWarning(String messagePattern, Object... params) {
        LOGGER.warn(messagePattern, params);
    }

    @Override
    public Player getPlayer() {
        return bukkitEvent.getPlayer();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getInitialMessage() {
        return bukkitEvent.getMessage();
    }

    @Override
    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void appendToPrefix(String toAppend) {
        this.prefix = prefix.concat(toAppend);
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void appendToSuffix(String toAppend) {
        this.suffix = suffix.concat(toAppend);
    }

    @Override
    public boolean shouldContinueHandling() {
        return !handlingStopped && !bukkitEvent.isCancelled();
    }
}
