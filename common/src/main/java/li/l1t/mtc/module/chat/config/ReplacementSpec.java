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

package li.l1t.mtc.module.chat.config;

import li.l1t.common.chat.TextOperators;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Specifies a regular expression replacement on chat messages.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
@SerializableAs("mtc.chat.replace")
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
                (boolean) source.computeIfAbsent(REPLACE_ENTIRELY_PATH, key -> false),
                (String) source.get(REGEX_PATH),
                (String) source.get(REPLACEMENT_PATH)
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
