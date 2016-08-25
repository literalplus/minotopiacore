/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatDispatcher;
import li.l1t.mtc.module.chat.api.ChatHandler;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import net.md_5.bungee.api.ChatColor;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

/**
 * A simple implementation of a chat dispatcher.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class SimpleChatDispatcher implements ChatDispatcher {
    private static final Logger LOGGER = LogManager.getLogger(ChatDispatcher.class);
    private final ChatModule module;
    private ListMultimap<ChatPhase, ChatHandler> handlerMap = MultimapBuilder
            .enumKeys(ChatPhase.class)
            .arrayListValues()
            .build();

    public SimpleChatDispatcher(ChatModule module) {
        this.module = module;
    }

    @Override
    public boolean registerHandler(ChatHandler handler) {
        verifyHandler(handler);
        if (!handler.enable(module)) {
            return false;
        }
        handlerMap.put(handler.getPhase(), handler);
        return true;
    }

    private void verifyHandler(ChatHandler handler) {
        Preconditions.checkNotNull(handler, "handler");
        Preconditions.checkNotNull(handler.getPhase(), "handler.getPhase()");
    }

    @Override
    public void unregisterHandler(ChatHandler handler) {
        verifyHandler(handler);
        handlerMap.remove(handler.getPhase(), handler);
    }

    @Override
    public void dispatchEvent(AsyncPlayerChatEvent bukkitEvent) {
        ChatMessageEvent event = createEvent(bukkitEvent);
        dispatchAllPhases(event);
        if (bukkitEvent.isCancelled()) {
            return;
        }
        writeBackChangesFromTo(event, bukkitEvent);
        logMessage(bukkitEvent);
        notifyIfOnlyPlayer(event);
    }

    private void notifyIfOnlyPlayer(ChatMessageEvent event) {
        if (Bukkit.getOnlinePlayers().size() <= 1) {
            event.getPlayer().sendMessage(module.formatMessage("Niemand hört dich :("));
        }
    }

    private void logMessage(AsyncPlayerChatEvent bukkitEvent) {
        LOGGER.info(ChatColor.stripColor(bukkitEvent.getFormat()));
    }

    private void writeBackChangesFromTo(ChatMessageEvent event, AsyncPlayerChatEvent bukkitEvent) {
        String message = escapeForStringFormat(event);
        String format = event.getPrefix() + "§7" + event.getPlayer().getName() + "§7: §f"
                + event.getSuffix() + message;
        bukkitEvent.setFormat(format);
    }

    private String escapeForStringFormat(ChatMessageEvent event) {
        return event.getMessage().replaceAll("%", "%%");
    }

    private BukkitChatMessageEvent createEvent(AsyncPlayerChatEvent bukkitEvent) {
        return new BukkitChatMessageEvent(bukkitEvent, module);
    }

    private void dispatchAllPhases(ChatMessageEvent event) {
        for (ChatPhase phase : ChatPhase.values()) {
            dispatchPhase(event, phase);
            if (!event.shouldContinueHandling()) {
                return;
            }
        }
    }

    private void dispatchPhase(ChatMessageEvent event, ChatPhase phase) {
        List<ChatHandler> handlers = handlerMap.get(phase);
        for (ChatHandler handler : handlers) {
            if (!event.shouldContinueHandling()) {
                return;
            }
            dispatchToHandler(event, handler);
        }
    }

    private void dispatchToHandler(ChatMessageEvent event, ChatHandler handler) {
        try {
            handler.handle(event);
        } catch (Exception e) {
            handleHandlerException(event, handler, e);
        }
    }

    private void handleHandlerException(ChatMessageEvent event, ChatHandler handler, Exception e) {
        LOGGER.warn("Handler {} failed to handle event {}: ", handler, event);
        LOGGER.warn("Exception: ", e);
    }
}
