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

package li.l1t.mtc;

import li.l1t.common.log.XYCFormatter;
import li.l1t.mtc.logging.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated Has several issues in code style and functionality. Use {@link LogManager}.
 */
@Deprecated
public final class LogHelper {

    private static final Logger MAIN_LOGGER = Logger.getLogger("MTC");

    private LogHelper() {

    }

    public static Logger getMainLogger() {
        return MAIN_LOGGER;
    }

    public static void flushAll() {
        LogHelper.flush(LogHelper.MAIN_LOGGER);
    }

    public static void initLogs() {

        File logDirectory = new File(MTC.instance().getDataFolder() + "/logs/");

        if (!logDirectory.isDirectory()) {
            if (logDirectory.isFile()) {
                throw new IllegalStateException("logs in the plugin data dir is a file while it should be a directory."); //The day this happens is the day I send every user a package with a cookie

            }
            if (!logDirectory.mkdirs()) {
                throw new IllegalStateException("Couldn't create logs directory.");
            }
        } else {
            //noinspection ConstantConditions
            Arrays.asList(logDirectory.listFiles()).stream() //If this returns null we're fucked anyways
                    .filter(fl -> fl.getName().contains(".lck")) //.lck.0
                    .forEach(File::delete);
        }

        if (ConfigHelper.isMainLoggerEnabled()) {
            try {
                FileHandler hdlr = new FileHandler(MTC.instance().getDataFolder() + "/logs/main.log", 10000000, 5, true);
                hdlr.setLevel(Level.FINEST);
                hdlr.setFormatter(new XYCFormatter(MTC.instance(), "Main Log.", true));
                hdlr.setEncoding("UTF-8");
                FileHandler hdlr2 = new FileHandler(MTC.instance().getDataFolder() + "/logs/error.log", 10000000, 5, true);
                hdlr2.setLevel(Level.WARNING);
                hdlr2.setFormatter(new XYCFormatter(MTC.instance(), "Main Log.", true));
                hdlr2.setEncoding("UTF-8");
//                LogHelper.MAIN_LOGGER.removeHandler(LogHelper.MAIN_LOGGER.getHandlers()[0]);
                LogHelper.MAIN_LOGGER.addHandler(hdlr);
                LogHelper.MAIN_LOGGER.addHandler(hdlr2);
                LogHelper.MAIN_LOGGER.setLevel(Level.FINEST);
            } catch (SecurityException | IOException e) {
                e.printStackTrace();
                System.out.println(">>MTC exception when tryin to initialize cmd loggerz.");
            }
        }
        LogHelper.MAIN_LOGGER.setUseParentHandlers(false);
    }

    private static void flush(Logger lgr) {
        for (Handler hdlr : lgr.getHandlers()) {
            hdlr.flush();
        }
    }

    private static void initLogger(Logger lgr, String fileName, String loggerName, boolean setParent) throws Exception {
        FileHandler hdlr = new FileHandler(fileName);
        hdlr.setLevel(Level.FINEST);
        hdlr.setFormatter(new XYCFormatter(MTC.instance(), loggerName, false));
        hdlr.setEncoding("UTF-8");
//        lgr.removeHandler(lgr.getHandlers()[0]);
        lgr.addHandler(hdlr);
        lgr.setLevel(Level.FINEST);
        if (setParent) {
            lgr.setParent(LogHelper.MAIN_LOGGER);
        }
    }

    private static void tryInitLogger(Logger lgr, String fileName, String loggerName, boolean setParent) {
        try {
            LogHelper.initLogger(lgr, fileName, loggerName, setParent);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(">>MTC exception when tryin to initialize " + loggerName + ".");
        }
    }
}
