/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.impl;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatPhase;

/**
 * Abstract base class for handlers that need access to the chat module. Note that implementations
 * need to call the superclass' {@link #enable(ChatModule)} if they choose to override that method.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public abstract class ModuleAwareChatHandler extends AbstractChatHandler {
    private ChatModule module;

    protected ModuleAwareChatHandler(ChatPhase phase) {
        super(phase);
    }

    @Override
    public boolean enable(ChatModule module) {
        this.module = module;
        return super.enable(module);
    }

    public ChatModule getModule() {
        return Preconditions.checkNotNull(module, "module");
    }
}
