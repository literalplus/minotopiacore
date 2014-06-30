package io.github.xxyy.minotopiacore.cron;

import io.github.xxyy.lib.com.Ostermiller.util.ArrayIterator;
import io.github.xxyy.common.sql.QueryResult;
import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.bans.BanHelper;
import io.github.xxyy.minotopiacore.chat.MTCChatHelper;
import io.github.xxyy.minotopiacore.chat.PrivateChat;
import io.github.xxyy.minotopiacore.clan.ClanHelper;
import io.github.xxyy.minotopiacore.cron.fulls.RunnableCheckInvsForFull;
import io.github.xxyy.minotopiacore.helper.StatsHelper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;


/**
 * 5m cronjob
 * <p>
 * does: save-all
 * chat swiping
 * clearing of ban cache (updates!)
 *
 * @author xxyy98
 */
public class RunnableCronjob5Minutes implements Runnable {
    private static int fullInfoExCount = 0;
    private static byte cacheExCount = 0;
    private boolean forced = false;

    public RunnableCronjob5Minutes(boolean forced) {
        this.forced = forced;
    }

    @Override
    public void run() {
        try {
            //saving
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
            //private chats
            if (!MTCChatHelper.directChats.isEmpty()) {
                for (Map.Entry<Integer, PrivateChat> entry : MTCChatHelper.directChats.entrySet()) {
                    PrivateChat chat = entry.getValue();

                    if (chat.activeRecipients.isEmpty()) {
                        MTCChatHelper.directChats.remove(entry.getKey());
                        if (!chat.recipients.isEmpty()) {
                            chat.recipients.stream()
                                    .filter(OfflinePlayer::isOnline)
                                    .forEach(plr -> plr.sendMessage(MTC.chatPrefix + "Der Chat §b#" + chat.chatId + "§6 wurde gelöscht."));
                        }
                        continue;
                    }

                    chat.recipients.stream()
                            .filter(plr -> !plr.isOnline())
                            .forEach(chat::removeRecipient);
                }
            }

            //actual msg
            CommandHelper.broadcast(MTC.chatPrefix + "[Cron-5M] Welt gespeichert! §7§o{" + (new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime())) + "}", "mtc.saveallmsg");

            //stats
            StatsHelper.flushQueue();

            RunnableCronjob5Minutes.cacheExCount++;
            RunnableCronjob5Minutes.fullInfoExCount++;

            //Check Fulls
            int checkEvery = MTC.instance().getConfig().getInt("fulltag.checkEveryInMinutes", 20);
            if (checkEvery > 0 && (RunnableCronjob5Minutes.fullInfoExCount * 5) >= checkEvery && ConfigHelper.isFullTagEnabled()) {
                RunnableCronjob5Minutes.fullInfoExCount = 0;
                (new RunnableCheckInvsForFull(new ArrayIterator<>(Bukkit.getOnlinePlayers()))).run();
            }

            //clan caches
            if (RunnableCronjob5Minutes.cacheExCount >= 12) {//run every hour
                RunnableCronjob5Minutes.cacheExCount = 0;
                //clear clan caches
                ClanHelper.clearCache();
            }
            //clear ban cache (Bungeecord!)
            BanHelper.banCache.clear();

            //player stats
            if (!this.forced && ConfigHelper.isUserStatisticsEnabled()) {
                Calendar cal = Calendar.getInstance();
                SafeSql sql = MTC.instance().getSql();
                String todayString = new SimpleDateFormat("YYYYMMdd").format(cal.getTime());
                String hourString = new SimpleDateFormat("HHmm").format(cal.getTime());
                String serverName = MTC.instance().getConfig().getString("servername");
                if (cal.get(Calendar.HOUR_OF_DAY) == 23 && cal.get(Calendar.MINUTE) > 45) {//there will have been 3 opportunities for the job to be executed, so safe

                    try (QueryResult qr = sql.executeQueryWithResult("SELECT day FROM " + sql.dbName + ".mtc_userstats WHERE serverid=? AND day=?",
                            serverName, todayString).vouchForResultSet()) {
                        if (!qr.rs().isBeforeFirst()) {
                            sql.safelyExecuteUpdate("INSERT INTO " + sql.dbName + ".mtc_userstats SET day=?,serverid=?,count=" +
                                            "(SELECT AVG(count) FROM " + sql.dbName + ".mtc_userstats_day WHERE serverid=? AND dayid=?)",
                                    todayString, serverName, serverName, todayString);
                        }
                    }
                }

                if (cal.get(Calendar.HOUR_OF_DAY) > 10 && cal.get(Calendar.HOUR_OF_DAY) < 23) {
                    sql.safelyExecuteUpdate("INSERT INTO " + sql.dbName + ".mtc_userstats_day SET dayid=?, timeid=?, serverid=?, count=?",
                            todayString, hourString, serverName, Bukkit.getOnlinePlayers().length);
                }
            }
        } catch (Exception e) {//always occurs on disable
            LogHelper.getMainLogger().throwing("RunnableCronJob5Minutes", "run()", e);
            Bukkit.getConsoleSender().sendMessage("§7[MTC]Cronjob 5M generated an exception: " + e.getClass().getName() + " (see main log)");
        }
    }
}
