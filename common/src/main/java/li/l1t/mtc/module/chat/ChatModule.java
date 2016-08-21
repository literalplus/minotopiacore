/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.chat.api.ChatDispatcher;
import li.l1t.mtc.module.chat.api.ChatHandler;
import li.l1t.mtc.module.chat.command.CommandChatClear;
import li.l1t.mtc.module.chat.handler.DefaultHandlers;
import li.l1t.mtc.module.chat.impl.SimpleChatDispatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.List;

/**
 * A module that provides a new, handler-based approach to chat events.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class ChatModule extends ConfigurableMTCModule implements Listener {
    public static final String NAME = "Chat";
    private ChatDispatcher dispatcher;

    protected ChatModule() {
        super(NAME, "modules/chat.cfg.yml", ClearCacheBehaviour.RELOAD_ON_FORCED, true);
        dispatcher = new SimpleChatDispatcher(this);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        DefaultHandlers.registerAllWith(this);
        registerListener(this);
        registerCommand(new CommandChatClear(this), "chatclear", "cc")
                .behaviour(CommandBehaviours.permissionChecking("mtc.chatclear"));
    }

    @Override
    protected void reloadImpl() {

    }

    public String formatMessage(String message, Object... params) {
        return getChatPrefix() + String.format(message, params);
    }

    /**
     * Attempts to register a chat handler with the dispatcher. Does not register the handler if it
     * is disabled in the configuration. The handler is identified by its {@link
     * Class#getSimpleName() simple name} in the configuration. Also enables the handler.
     *
     * @param handler the handler to register
     * @return whether the handler is enabled, succeeded to enable itself and was registered
     */
    public boolean registerHandler(ChatHandler handler) {
        return isEnabled(handler) && getDispatcher().registerHandler(handler);
    }

    private boolean isEnabled(ChatHandler handler) {
        String configPath = "enable." + handler.getClass().getSimpleName();
        return getConfigBoolean(configPath, true);
    }

    /**
     * Retrieves a boolean value from the configuration file, creating it if it does not yet exist.
     *
     * @param configPath the path of the value
     * @param def        the default value
     * @return the value, or the default if not found
     */
    public boolean getConfigBoolean(String configPath, boolean def) {
        setDefaultIfUnset(configPath, def);
        return configuration.getBoolean(configPath);
    }

    private void setDefaultIfUnset(String configPath, Object def) {
        if (!configuration.contains(configPath)) {
            configuration.set(configPath, def);
        }
    }

    /**
     * Retrieves a list of strings from the configuration file, creating it with passed default
     * values if it does not yet exist.
     *
     * @param configPath the path of the list
     * @param defaults   the default entries
     * @return the list, or the defaults, if it was created
     */
    public List<String> getConfigStringList(String configPath, String... defaults) {
        setDefaultIfUnset(configPath, Arrays.asList(defaults));
        return configuration.getStringList(configPath);
    }

    public ChatDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(ChatDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getChatPrefix() {
        return getPlugin().getChatPrefix();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent bukkitEvent) {
        dispatcher.dispatchEvent(bukkitEvent);
    }
}
