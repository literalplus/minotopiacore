/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat.cmdspy;

import io.github.xxyy.mtc.LogHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * CommandSpy filter for bad commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 20.6.14
 */
public class BadCommandSpyFilter extends RegExCommandSpyFilter {
    private final Logger logger;

    public BadCommandSpyFilter() {
        super("§4[CmdSpy] §c{0}: §7§o/{1}", new ArrayList<>());
        this.logger = LogHelper.getBadCmdLogger();
    }

    @Override
    protected String formatMatch(Matcher matcher, Player sender, String command) {
        logger.log(Level.INFO, sender + "(" + sender.getAddress() + "): " + command);
        return super.formatMatch(matcher, sender, command);
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

    public void addCommand(String commandName) {
        getPatterns().addAll(CommandSpyFilters.getStringFilterPatterns(commandName).collect(Collectors.toList()));
    }
}
