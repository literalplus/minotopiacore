package io.github.xxyy.minotopiacore.chat.cmdspy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CommandSpy filter for bad commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 20.6.14
 */
public class BadCommandSpyFilter extends RegExCommandSpyFilter {
    private final Logger logger;

    public BadCommandSpyFilter(Stream<Pattern> patterns, Logger logger) {
        super("§4[CmdSpy] §c{}: §7§o/{}", patterns.collect(Collectors.toList()));
        this.logger = logger;
    }

    @Override
    public boolean notifyOnMatch(String command, Player sender) {
        logger.log(Level.INFO, sender + "(" + sender.getAddress() + "): " + command);
        return super.notifyOnMatch(command, sender);
    }

    @Override
    protected Stream<Player> getOnlineSubscriberStream() {
        return Arrays.asList(Bukkit.getOnlinePlayers())
                .stream()
                .filter(plr -> plr.hasPermission("mtc.cmdspy"));
    }

    @Override
    public String niceRepresentation() {
        return "(global) " + super.niceRepresentation();
    }

    @Override
    public boolean canSubscribe() {
        return false;
    }
}
