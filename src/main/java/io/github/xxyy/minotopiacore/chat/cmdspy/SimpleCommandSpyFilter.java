package io.github.xxyy.minotopiacore.chat.cmdspy;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * A simple implementation of CommandSpyFilter.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public class SimpleCommandSpyFilter implements CommandSpyFilter {
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

    @Override
    public boolean notifyOnMatch(String command, Player sender) {
        return false;
    }

    @Override
    public Collection<UUID> getSubscribers() {
        return null;
    }

    protected String getNotificationFormat() {
        return notificationFormat;
    }
}
