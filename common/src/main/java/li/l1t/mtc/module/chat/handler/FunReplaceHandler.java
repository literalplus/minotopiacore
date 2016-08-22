/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.handler;

import li.l1t.common.chat.TextReplacementService;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.config.ReplacementSpec;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Chat handler that replaces parts of or entire messages that match configurable patterns.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
public class FunReplaceHandler extends AbstractChatHandler {
    private static final List<ReplacementSpec> DEFAULT_REPLACEMENTS = Arrays.asList(
            new ReplacementSpec(false, "n[o0u\\(\\)]+b", "nette Person"),
            new ReplacementSpec(false, "s[p]+[a4]+[s]+t", "sehr nette Person"),
            new ReplacementSpec(false, "h[u]+[r]*[e3]*[n]*[s]+[o0u\\(\\)]+[h]*[n]*", "Sohn einer lieben Person"),
            new ReplacementSpec(false, "w[i1]+[chksx]+(?:[e3]+[r]+|[a4]+)", "belebte Person"),
            new ReplacementSpec(false, "[a4](rsch)?[l1][o0\\(\\)]+(ch)?|[a4]rsch", "elegante Person"),
            new ReplacementSpec(false, "fuck", "fluff"),
            new ReplacementSpec(false, "[?!]{3,}", "."),
            new ReplacementSpec(true, "\\bez\\b", "Ich bedanke mich für den guten Kampf.")
    );
    private TextReplacementService service;

    protected FunReplaceHandler() {
        super(ChatPhase.DECORATING);
    }

    @Override
    public boolean enable(ChatModule module) {
        ConfigurationSerialization.registerClass(ReplacementSpec.class);
        List<ReplacementSpec> replacementSpecs = module.getConfigList(
                "replaces.replacements", ReplacementSpec.class, DEFAULT_REPLACEMENTS
        );
        List<UnaryOperator<String>> operators = replacementSpecs.stream()
                .map(ReplacementSpec::toOperator)
                .collect(Collectors.toList());
        service = new TextReplacementService(operators);
        return true;
    }

    @Override
    public void disable(ChatModule module) {
        ConfigurationSerialization.unregisterClass(ReplacementSpec.class);
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        evt.setMessage(service.apply(evt.getMessage()));
    }
}
