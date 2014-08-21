package io.github.xxyy.mtc.misc.cmd;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.cron.RunnableCronjob5Minutes;
import io.github.xxyy.mtc.misc.RunnableReloadTimer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public final class CommandBReload extends MTCCommandExecutor {

    public static int taskId = -2;

    private static final List<Integer> SECONDS = new ArrayList<>();
    private static final List<Long> DELAYS = new ArrayList<>();

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.breload", label)) {
            return true;
        }
        // TODO provide reasons. generic reasons. random messages.
        if (args.length >= 1 && args[0].equalsIgnoreCase("veto")) {
            if (CommandBReload.taskId < 0) {
                sender.sendMessage("§cNoch kein Reload gestartet. §e/breload help");
                return true;
            }
            Bukkit.getScheduler().cancelTask(CommandBReload.taskId);
            Command.broadcastCommandMessage(sender, "§cCancelled Reload!");
            Bukkit.broadcastMessage(MTC.chatPrefix + "§cReload abgebrochen!");
            CommandBReload.taskId = -3;
            CommandBReload.SECONDS.clear();
            CommandBReload.DELAYS.clear();
            return true;
        }
        if (!CommandBReload.SECONDS.isEmpty() || !CommandBReload.DELAYS.isEmpty()) {
            sender.sendMessage("§cEin Reload wurde bereits gestartet. §eAbbrechen: /breload veto");
            return true;
        }
        long minuteAdd = 0;
        int secCount = 0;
        BukkitScheduler sched = Bukkit.getScheduler();
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("help") || !StringUtils.isNumeric(args[0])) {
                CommandHelper.msg("§eReloadet mit Verzögerung.\n" +
                        "§6/breload §eReloadet in 30 Sekunden.\n" +
                        "§6/breload <x> §6Reloadet in x Sekunden.\n" +
                        "§6/breload veto §6Bricht einen Reload ab.", sender);
                return true;
            }
            secCount = Integer.parseInt(args[0]);
            minuteAdd = (secCount - 1);
            if (secCount <= 0) {
                sender.sendMessage("§cDas kann jetzt aber nicht negativ oder null sein!");
                return true;
            }
            CommandBReload.SECONDS.add(secCount);
            CommandBReload.schedule(15, secCount - 15);
        }
        if (secCount == 0) {
            CommandBReload.SECONDS.add(15);
        } else {
            minuteAdd += 100;
        }
        CommandBReload.SECONDS.addAll(Arrays.asList(10, 5, 4, 3, 2, 1, 0));
        CommandBReload.DELAYS.addAll(Arrays.asList(minuteAdd + 100, 100L,
                20L, 20L, 20L, 20L, 20L));

        RunnableReloadTimer.delays = CommandBReload.DELAYS;
        RunnableReloadTimer.iDelays = CommandBReload.DELAYS.iterator();
        RunnableReloadTimer.seconds = CommandBReload.SECONDS;
        RunnableReloadTimer.iSeconds = CommandBReload.SECONDS.iterator();
        CommandBReload.taskId = sched.runTaskLater(MTC.instance(), new RunnableReloadTimer(sender), 10).getTaskId();//message order

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, ((secCount != 0) ? secCount * 60 : 30));
        Command.broadcastCommandMessage(sender, "§d§oScheduled Reload for " + (new SimpleDateFormat("HH:mm:ss")).format(cal.getTime()) + "§7§o");
        Bukkit.getScheduler().runTask(MTC.instance(), new RunnableCronjob5Minutes(true));
        return true;
    }

    private static void schedule(int second, long delay) {
        CommandBReload.SECONDS.add(second);
        CommandBReload.DELAYS.add(delay);
    }
}
