package io.github.xxyy.mtc.misc;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Provides utilities for basic java classes
 */
public final class JavaUtils {
    private JavaUtils() {
        throw new UnsupportedOperationException("Instantiation of utility class");
    }

    /**
     * Negates a predicate.
     * <p>
     * You simply can't just do things like this easily:
     * {@code (StringUtils::isNumeric).negate()}
     * <p>
     * For the sane of the code, this function is available and
     * you can import this functon static for even nicer code.
     *
     * @param predicate predicate to negate
     * @param <T>       {@link Predicate} (the type of the input to the predicate)
     * @return the negated predicate
     */
    @NotNull
    public static <T> Predicate<T> not(@NotNull Predicate<T> predicate) {
        return predicate.negate();
    }
}
