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

package li.l1t.mtc.module.quiz;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a quiz question
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
@SerializableAs("mtc.quiz.question")
public class QuizQuestion implements ConfigurationSerializable {
    private final String text;
    private final String answer;
    private final Pattern pattern;

    public QuizQuestion(String text, String answer) {
        Validate.notNull(text);
        Validate.notNull(answer);
        this.text = text;
        this.answer = answer;
        this.pattern = Pattern.compile("(?<!\\w)" + Pattern.quote(answer) + "(?!\\w|-)", Pattern.CASE_INSENSITIVE);
    }

    public static QuizQuestion deserialize(Map<String, Object> input) {
        Validate.isTrue(input.containsKey("text") && input.containsKey("answer"), "Missing either one of text or answer");

        return new QuizQuestion(input.get("text").toString(), input.get("answer").toString());
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }

    public Matcher matcher(String input) {
        return pattern.matcher(input);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> rtrn = new HashMap<>();
        rtrn.put("text", text);
        rtrn.put("answer", answer);
        return rtrn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizQuestion)) return false;

        QuizQuestion that = (QuizQuestion) o;

        return answer.equals(that.answer) && text.equals(that.text);

    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + answer.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "text='" + text + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    public static final class Builder {
        private String text;
        private String answer;

        public void answer(String answer) {
            this.answer = answer;
        }

        public void text(String text) {
            this.text = text;
        }

        public boolean isReady() {
            return text != null && answer != null;
        }

        public QuizQuestion build(QuizModule module) {
            Validate.isTrue(isReady(), "Not all fields have been filled!");
            QuizQuestion instance = new QuizQuestion(text, answer);
            module.getQuestions().add(instance);
            module.save();
            return instance;
        }
    }
}
