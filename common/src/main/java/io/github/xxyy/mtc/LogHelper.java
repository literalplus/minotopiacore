package io.github.xxyy.mtc;

import io.github.xxyy.common.log.XYCFormatter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class LogHelper {

    private static final Logger MAIN_LOGGER = Logger.getLogger("MTC");
    private static final Logger BAD_CMD_LOGGER = Logger.getLogger("MTC.BCMD");
    private static final Logger CHAT_LOGGER = Logger.getLogger("MTC.CHAT");
    private static final Logger CLAN_CHAT_LOGGER = Logger.getLogger("MTC.CCHAT");
    private static final Logger PRIV_CHAT_LOGGER = Logger.getLogger("MTC.PCHAT");
    private static final Logger BAN_LOGGER = Logger.getLogger("MTC.BANS");
    private static final Logger WARN_LOGGER = Logger.getLogger("MTC.WARNS");
    private static final Logger FULL_LOGGER = Logger.getLogger("MTC.FULLS");

    private LogHelper() {

    }

    public static void flushAll() {
        LogHelper.flush(LogHelper.MAIN_LOGGER);
        LogHelper.flush(LogHelper.BAD_CMD_LOGGER);
        LogHelper.flush(LogHelper.CHAT_LOGGER);
        LogHelper.flush(LogHelper.CLAN_CHAT_LOGGER);
        LogHelper.flush(LogHelper.PRIV_CHAT_LOGGER);
        LogHelper.flush(LogHelper.BAN_LOGGER);
        LogHelper.flush(LogHelper.WARN_LOGGER);
        LogHelper.flush(LogHelper.FULL_LOGGER);
    }

    public static Logger getBadCmdLogger() {
        return LogHelper.BAD_CMD_LOGGER;
    }

    public static Logger getBanLogger() {
        return LogHelper.BAN_LOGGER;
    }

    public static Logger getChatLogger() {
        return LogHelper.CHAT_LOGGER;
    }

    public static Logger getClanChatLogger() {
        return LogHelper.CLAN_CHAT_LOGGER;
    }

    public static Logger getFullLogger() {
        return LogHelper.FULL_LOGGER;
    }

    public static Logger getMainLogger() {
        return LogHelper.MAIN_LOGGER;
    }

    public static Logger getPrivChatLogger() {
        return LogHelper.PRIV_CHAT_LOGGER;
    }

    public static Logger getWarnLogger() {
        return LogHelper.WARN_LOGGER;
    }

    public static void initLogs() {

        File logDirectory = new File(MTC.instance().getDataFolder() + "/logs/");

        if (!logDirectory.isDirectory()) {
            if(logDirectory.isFile()) {
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
        if (MTC.instance().getConfig().getBoolean("enable.log.cmds", false)) {
            LogHelper.tryInitLogger(LogHelper.BAD_CMD_LOGGER, MTC.instance().getDataFolder() + "/logs/badcmds.log", "BadCmdLogger", true);
        }
        if (MTC.instance().getConfig().getBoolean("enable.log.chat", true)) {
            LogHelper.tryInitLogger(LogHelper.CHAT_LOGGER, MTC.instance().getDataFolder() + "/logs/chat.log", "ChatLogger", true);
            LogHelper.tryInitLogger(LogHelper.CLAN_CHAT_LOGGER, MTC.instance().getDataFolder() + "/logs/clanchat.log", "ClanChatLogger", true);
            LogHelper.tryInitLogger(LogHelper.PRIV_CHAT_LOGGER, MTC.instance().getDataFolder() + "/logs/privatechats.log", "PrivChatLogger", true);
        }
        if (MTC.instance().getConfig().getBoolean("enable.log.bansNwarns", true)) {
            LogHelper.tryInitLogger(LogHelper.WARN_LOGGER, MTC.instance().getDataFolder() + "/logs/warns.log", "WarnLogger", true);
            LogHelper.tryInitLogger(LogHelper.BAN_LOGGER, MTC.instance().getDataFolder() + "/logs/bans.log", "BanLogger", true);
        }
        if (ConfigHelper.isFullLogEnabled()) {
            LogHelper.tryInitLogger(LogHelper.FULL_LOGGER, MTC.instance().getDataFolder() + "/logs/fulls.log", "FullLogger", true);
        }
        LogHelper.MAIN_LOGGER.setUseParentHandlers(false);
        LogHelper.BAD_CMD_LOGGER.setUseParentHandlers(false);
        LogHelper.CHAT_LOGGER.setUseParentHandlers(false);
        LogHelper.CLAN_CHAT_LOGGER.setUseParentHandlers(false);
        LogHelper.PRIV_CHAT_LOGGER.setUseParentHandlers(false);
        LogHelper.BAN_LOGGER.setUseParentHandlers(false);
        LogHelper.WARN_LOGGER.setUseParentHandlers(false);
        LogHelper.FULL_LOGGER.setUseParentHandlers(false);
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
