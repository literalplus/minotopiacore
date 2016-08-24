/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
