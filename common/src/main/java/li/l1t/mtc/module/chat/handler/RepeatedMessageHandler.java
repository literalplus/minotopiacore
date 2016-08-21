/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
                messages.add(evt.getMessage());
            }
        }
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        recentMessages.clear();
    }
}
