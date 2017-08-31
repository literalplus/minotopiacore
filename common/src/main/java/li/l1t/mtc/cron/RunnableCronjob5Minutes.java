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

package li.l1t.mtc.cron;

import li.l1t.mtc.MTC;
import li.l1t.mtc.chat.cmdspy.CommandSpyFilters;
import li.l1t.mtc.clan.ClanHelper;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.misc.CacheHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;


/**
 * A task which runs every five minutes and executes some periodic cleanup tasks for MTC, including,
 * but not limited to, cleaning and updating of caches.
 *
 * @author xxyy98
 */
public class RunnableCronjob5Minutes implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(RunnableCronjob5Minutes.class);
    private static byte cacheExCount = 0;
    private final MTC plugin;
    private boolean forced = false;

    public RunnableCronjob5Minutes(boolean forced, MTC plugin) {
        this.forced = forced;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            //Remove dead CommandSpy filters
            CommandSpyFilters.removeDeadFilters();

            RunnableCronjob5Minutes.cacheExCount++;

            CacheHelper.clearCaches(forced, plugin);

            //clan caches
            if (RunnableCronjob5Minutes.cacheExCount >= 12) {//run every hour
                RunnableCronjob5Minutes.cacheExCount = 0;
                //clear clan caches
                ClanHelper.clearCache();
            }
        } catch (Exception e) {//always occurs on disable //TODO: wat
            LOGGER.catching(Level.INFO, e);
            Bukkit.getConsoleSender().sendMessage("§7[MTC]Cronjob 5M generated an exception: " + e.getClass().getName() + " (see main log)");
        }
    }
}
