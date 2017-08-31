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

package li.l1t.mtc.module.chat.handler;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.misc.Cache;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * A chat handler that denies sending of the same or similar messages repeatedly.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
public class RepeatedMessageHandler extends AbstractChatHandler implements Cache {
    private static final int SPAM_THRESHOLD_DISTANCE = 2;
    private ListMultimap<UUID, String> recentMessages = Multimaps.newListMultimap(
            new ConcurrentHashMap<>(),
            () -> Collections.synchronizedList(new ArrayList<>(3))
    );

    protected RepeatedMessageHandler() {
        super(ChatPhase.FILTERING);
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        List<String> previousMessages = recentMessages.get(evt.getPlayer().getUniqueId());
        String currentMessage = evt.getMessage();
        if (anyOfAreSimilarTo(previousMessages, currentMessage)) {
            evt.tryDenyMessage("Bitte nicht spammen :)", this);
        }
        cacheMessage(evt, previousMessages);
    }

    private boolean anyOfAreSimilarTo(List<String> previousMessages, String currentMessage) {
        return previousMessages.stream()
                .anyMatch(fuzzyMatches(currentMessage));
    }

    @Nonnull
    private Predicate<String> fuzzyMatches(String currentMessage) {
        return previousMessage -> previousMessage.equalsIgnoreCase(currentMessage) ||
                levenshteinMatches(previousMessage, currentMessage);
    }

    private boolean levenshteinMatches(String previousMessage, String currentMessage) {
        return isEligibleForFuzzyMatching(previousMessage, currentMessage) &&
                StringUtils.getLevenshteinDistance(previousMessage, currentMessage) <= SPAM_THRESHOLD_DISTANCE;
    }

    private boolean isEligibleForFuzzyMatching(String previousMessage, String currentMessage) {
        return previousMessage.length() >= 5 || currentMessage.length() >= 5;
    }

    private void cacheMessage(ChatMessageEvent evt, List<String> messages) {
        if (!evt.mayBypassFilters()) {
            if (messages.size() >= 3) {
                messages.remove(0);
            }
            messages.add(evt.getMessage());
        }
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        recentMessages.clear();
    }
}
