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
