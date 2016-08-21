/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
