/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.chat.cmdspy;

import li.l1t.mtc.logging.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * CommandSpy filter for bad commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 20.6.14
 */
public class BadCommandSpyFilter extends RegExCommandSpyFilter {
    private static final Logger LOGGER = LogManager.getLogger(BadCommandSpyFilter.class);

    public BadCommandSpyFilter() {
        super("§4[CmdSpy] §c{0}: §7§o/{1}", new ArrayList<>());
    }

    @Override
    protected String formatMatch(Matcher matcher, Player sender, String command) {
        LOGGER.info("{}({}): {}", sender.getName(), sender.getAddress(), command);
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
