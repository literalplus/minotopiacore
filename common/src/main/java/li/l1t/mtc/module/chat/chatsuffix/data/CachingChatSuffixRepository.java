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

package li.l1t.mtc.module.chat.chatsuffix.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class CachingChatSuffixRepository implements ChatSuffixRepository {
    private final ChatSuffixRepository proxy;
    private final Map<UUID, String> suffixCache = new HashMap<>();

    public CachingChatSuffixRepository(ChatSuffixRepository proxy) {
        this.proxy = proxy;
    }

    @Override
    public String findChatSuffixById(UUID playerId) {
        return suffixCache.computeIfAbsent(playerId, proxy::findChatSuffixById);
    }

    @Override
    public void saveChatSuffix(UUID playerId, String chatSuffix) {
        suffixCache.put(playerId, chatSuffix);
        proxy.saveChatSuffix(playerId, chatSuffix);
    }

    @Override
    public void clearCache() {
        suffixCache.clear();
        proxy.clearCache();
    }
}
