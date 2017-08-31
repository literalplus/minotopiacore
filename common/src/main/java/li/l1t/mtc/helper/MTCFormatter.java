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

package li.l1t.mtc.helper;

import li.l1t.mtc.MTC;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MTCFormatter extends Formatter {

    @Override
    public String format(LogRecord rec) {
        return "[" + new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:S").format(rec.getMillis()) + "|" + rec.getLevel() + "@" + rec.getLoggerName() + "] " + rec.getMessage() +
                "  {@" + rec.getSourceMethodName() + "}\n";
    }

    @Override
    public String getHead(Handler h) {
        return "******************** MTC LOG FILE ********************\n" +
                " * Date: " + (new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Calendar.getInstance().getTimeInMillis()) + "\n" +
                " * Bukkit Version: " + Bukkit.getVersion() + "\n" +
                " * Plugin Version: " + MTC.PLUGIN_VERSION.toString() + "\n" +
                " * Encoding: " + h.getEncoding() + "\n" +
                " * Formatter: MTCFormatter\n" +
                "******************** MTC LOG FILE ********************\n");
    }

}
