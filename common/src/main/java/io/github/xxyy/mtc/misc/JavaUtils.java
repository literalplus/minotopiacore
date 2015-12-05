package io.github.xxyy.mtc.misc;

import java.util.function.Predicate;

/**
 * Provides utilities for the
 */
public class JavaUtils {
    private JavaUtils() {
        throw new UnsupportedOperationException("Instantiation of utility class");
    }

    /**
     * Negates a predicate.<br>
     * <br>
     * You simply can't just do things like this easily:
     * {@code (StringUtils::isNumeric).negate()}<br>
     * <br>
     * For the sane of the code, this function is available and<br>
     * you can import this functon static for even nicer code.
     *
     * @param predicate predicate to negate
     * @param <T>       {@link Predicate} (the type of the input to the predicate)
     * @return the negated predicate
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }
}
