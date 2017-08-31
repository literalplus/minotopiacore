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

package li.l1t.mtc.module.truefalse;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a true/false question
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 4.9.14
 */
@SerializableAs("mtc.tf.question")
public class TrueFalseQuestion implements ConfigurationSerializable {

    private final String text;
    private final boolean answer;

    public TrueFalseQuestion(String text, boolean answer) {
        this.text = text;
        this.answer = answer;
    }

    public static TrueFalseQuestion deserialize(Map<String, Object> input) {
        Validate.isTrue(input.containsKey("text") && input.containsKey("answer"), "Missing either one of text or answer");

        return new TrueFalseQuestion(input.get("text").toString(), Boolean.parseBoolean(input.get("answer").toString()));
    }

    public String getText() {
        return text;
    }

    public boolean getAnswer() {
        return answer;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> rtrn = new HashMap<>();
        rtrn.put("text", text);
        rtrn.put("answer", answer);
        return rtrn;
    }
}
