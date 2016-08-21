/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.chatsuffix.data;

import java.util.UUID;

/**
 * Fetches chat suffixes from an underlying database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public interface ChatSuffixRepository {
    String findChatSuffixById(UUID playerId);

    void saveChatSuffix(UUID playerId, String chatSuffix);

    /**
     * Clears this repository's cache, if any.
     */
    void clearCache();
}
