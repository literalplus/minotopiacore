package io.github.xxyy.mtc.chat.cmdspy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A simple implementation of CommandSpyFilter.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public class PlayerCommandSpyFilter extends MultiSubscriberCommandSpyFilter {
    private final UUID target;

    public PlayerCommandSpyFilter(String notificationFormat, UUID target) {
        super(notificationFormat, (cmd, plr) -> target.equals(plr.getUniqueId()));
        this.target = target;
    }

    public UUID getTarget() {
        return target;
    }

    @Override
    public boolean matches(String command, Player sender) {
        getPlayer();

        return super.matches(command, sender);
    }

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(target);
        if (player == null) { //REFACTOR Is this coupling to hard?
            CommandSpyFilters.unregisterFilter(this);
        }
        return player;
    }

    public String getPlayerName() {
        Player plr = getPlayer();
        return plr == null ? "{offline}" : plr.getName();
    }

    @Override
    public String niceRepresentation() {
        return super.niceRepresentation() + " -> " + target.toString() + "@" + getPlayerName();
    }
}
