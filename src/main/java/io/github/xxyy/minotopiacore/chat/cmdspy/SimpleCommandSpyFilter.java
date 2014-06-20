package io.github.xxyy.minotopiacore.chat.cmdspy;

import org.bukkit.entity.Player;

import java.util.function.BiPredicate;

/**
 * A simple implementation of CommandSpyFilter.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public abstract class SimpleCommandSpyFilter implements CommandSpyFilter {
    private final String notificationFormat;
    private final BiPredicate<String, Player> predicate;

    public SimpleCommandSpyFilter(String notificationFormat, BiPredicate<String, Player> predicate) {
        this.notificationFormat = notificationFormat;
        this.predicate = predicate;
    }

    @Override
    public boolean matches(String command, Player sender) {
        return predicate.test(command, sender);
    }

    protected String getNotificationFormat() {
        return notificationFormat;
    }
}
