/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.config;

import li.l1t.common.chat.TextOperators;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Specifies a regular expression replacement on chat messages.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
public class ReplacementSpec implements ConfigurationSerializable {
    private static final String REPLACE_ENTIRELY_PATH = "replace-entirely";
    private static final String REGEX_PATH = "pattern-regex";
    private static final String REPLACEMENT_PATH = "replacement";
    private final boolean replaceEntirely;
    private final String regex;
    private final String replacement;

    public ReplacementSpec(boolean replaceEntirely, String regex, String replacement) {
        this.replaceEntirely = replaceEntirely;
        this.regex = regex;
        this.replacement = replacement;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(REPLACE_ENTIRELY_PATH, replaceEntirely);
        result.put(REGEX_PATH, regex);
        result.put(REPLACEMENT_PATH, replacement);
        return result;
    }

    public static ReplacementSpec deserialize(Map<String, Object> source) {
        return new ReplacementSpec(
                (boolean) source.computeIfAbsent("global", key -> false),
                (String) source.get("pattern-regex"),
                (String) source.get("replacement")
        );
    }

    public UnaryOperator<String> toOperator() {
        if (replaceEntirely) {
            return TextOperators.replaceEntirely(regex, replacement);
        } else {
            return TextOperators.replaceAll(regex, replacement);
        }
    }
}
