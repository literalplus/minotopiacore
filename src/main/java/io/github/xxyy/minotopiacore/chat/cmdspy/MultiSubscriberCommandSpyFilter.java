package io.github.xxyy.minotopiacore.chat.cmdspy;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * A CommandSpyFilter impl that allows for multiple subscribers and keeps them internally as a set of their UUIDs.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public class MultiSubscriberCommandSpyFilter extends SimpleCommandSpyFilter {
    private final Set<UUID> subscribers = new HashSet<>();
    private final String notificationFormat;

    public MultiSubscriberCommandSpyFilter(String notificationFormat, BiPredicate<String, Player> predicate) {
        super(predicate);
        MessageFormat.format(notificationFormat, "testPlayerName", "cmdspy"); //Throw exception immediately if pattern is invalid -> tests :)
        this.notificationFormat = notificationFormat;
    }

    public void notifySubscribers(String command, Player sender) {
        getOnlineSubscriberStream()
                .forEach(plr -> sendNotification(MessageFormat.format(getNotificationFormat(), sender.getName(), command), plr));
    }

    protected Stream<Player> getOnlineSubscriberStream() {
        return ImmutableList.copyOf(subscribers).stream() //Using the original would cause a CME
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

    protected String getNotificationFormat() {
        return notificationFormat;
    }
}
