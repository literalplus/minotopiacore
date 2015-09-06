/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat.cmdspy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
        super("§4[CmdSpy] §c{0}: §7§o/{1}", patterns.collect(Collectors.toList()));
        this.logger = logger;
    }

    @Override
    public boolean matches(String command, Player sender) { //Doesn't work since super #notifyOnMatch doesn't call matches
        if(super.matches(command, sender)) {
            logger.log(Level.INFO, sender + "(" + sender.getAddress() + "): " + command);
            return true;
        }
        return false;
    }

    @Override
    protected List<Player> getOnlineSubscribers() {
        List<Player> result = new ArrayList<>(Bukkit.getOnlinePlayers());
        result.removeIf(plr -> !plr.hasPermission("mtc.cmdspy"));
        return result;
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
