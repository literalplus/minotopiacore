/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
