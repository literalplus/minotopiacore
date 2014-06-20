package io.github.xxyy.minotopiacore.chat.cmdspy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public class MultiSubscriberCommandSpyFilter extends SimpleCommandSpyFilter {
    private final Set<UUID> subscribers = new HashSet<>();

    public MultiSubscriberCommandSpyFilter(String notificationFormat, BiPredicate<String, Player> predicate) {
        super(notificationFormat, predicate);
    }

    public void notifySubscribers(String command, Player sender) {
        getOnlineSubscriberStream()
                .forEach(plr -> sendNotification(MessageFormat.format(getNotificationFormat(), sender.getName(), command), plr));
    }

    protected Stream<Player> getOnlineSubscriberStream() {
        return subscribers.stream()
                .map(this::getPlayerIfPresent)
                .filter(p -> p != null);
    }

    protected void sendNotification(String notificationMessage, Player plr) {
        plr.sendMessage(notificationMessage);
    }

    @Override
    public boolean notifyOnMatch(String command, Player sender) {
        if (matches(command, sender)) {
            notifySubscribers(command, sender);
            return true;
        }
        return false;
    }

    public Set<UUID> getSubscribers() {
        return subscribers;
    }

    private Player getPlayerIfPresent(UUID uuid) {
        Player rtrn = Bukkit.getPlayer(uuid);
        if (rtrn == null) {
            subscribers.remove(uuid);
        }
        return rtrn;
    }
}
