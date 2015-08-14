package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Joiner;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This module reads essentials userdata files and show you homes of all users in a given radius on command.
 * @author Janmm14
 */
public class ShowHomesModule extends ConfigurableMTCModule {

    public static final String NAME = "ShowHomes";

    private static File essentialsUserdataFolder;

    private static final int defaultRadiusDefault = 20;
    private static final int minRadiusDefault = 3;
    private static final int maxRadiusDefault = 50;
    private static final int hologramDurationDefault = 60;
    private static final int hologramCreationsPerCommandPerTickDefault = 20;

    final Map<UUID, List<Home>> holosByExecutingUser = new HashMap<>();
    final Map<UUID, Integer> autoRemoveTaskIdByUser = new HashMap<>();

    private int defaultRadius = defaultRadiusDefault;
    private int minRadius = minRadiusDefault;
    private int maxRadius = maxRadiusDefault;
    private int hologramDuration = hologramDurationDefault;
    private int hologramCreationsPerCommandPerTick = hologramCreationsPerCommandPerTickDefault;

    public ShowHomesModule() {
        super(NAME, "showhomes.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public boolean canBeEnabled(MTC plugin) {
        return super.canBeEnabled(plugin)
                && plugin.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")
                && plugin.getServer().getPluginManager().isPluginEnabled("Essentials");
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        essentialsUserdataFolder =
                new File(plugin.getServer().getPluginManager().getPlugin("Essentials").getDataFolder(), "userdata");

        setConfigDefaults();
        readConfig();
        configuration.options().copyDefaults(true).copyHeader(true);
        save();
        plugin.getCommand("showhomes").setExecutor(new ShowHomesCommandHandler());
        setTabExecutor("homeutil", new ChangeHomeCommand(this));
    }

    private <T extends CommandExecutor & TabCompleter> PluginCommand setTabExecutor(String commandName, T tabExecutor) {
        PluginCommand cmd = getPlugin().getCommand(commandName);
        cmd.setExecutor(tabExecutor);
        cmd.setTabCompleter(tabExecutor);
        return cmd;
    }

    private void setConfigDefaults() {
        FileConfiguration cfg = configuration;
        cfg.addDefault("defaultRadius", defaultRadiusDefault);
        cfg.addDefault("minRadius", minRadiusDefault);
        cfg.addDefault("maxRadius", maxRadiusDefault);
        cfg.addDefault("hologramDuration", hologramDurationDefault);
        cfg.addDefault("hologramCreationsPerCommandPerTick", hologramCreationsPerCommandPerTickDefault);
    }

    private void readConfig() {
        FileConfiguration cfg = configuration;
        defaultRadius = cfg.getInt("defaultRadius");
        minRadius = cfg.getInt("minRadius");
        maxRadius = cfg.getInt("maxRadius");
        hologramDuration = cfg.getInt("hologramDuration");
        hologramCreationsPerCommandPerTick = cfg.getInt("hologramCreationsPerCommandPerTick");
    }

    @Override
    public void disable(MTC plugin) {
        super.disable(plugin);

        HologramsAPI.getHolograms(plugin)
                .forEach(Hologram::delete);
        holosByExecutingUser.values().stream()
                .flatMap(List::stream)
                .map(Home::getHologram)
                .filter(Objects::nonNull)
                .forEach(Hologram::delete);
        holosByExecutingUser.clear();
        autoRemoveTaskIdByUser.values()
                .forEach(plugin.getServer().getScheduler()::cancelTask);
        autoRemoveTaskIdByUser.clear();
        essentialsUserdataFolder = null;
    }

    @Override
    protected void reloadImpl() {
        super.reloadConfig();
        setConfigDefaults();
        readConfig();
    }

    private boolean showHologramsCmd(Player plr, int radius) {
        plr.sendMessage("§6Lade Homes im Umkreis " + radius);
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                UUID uuid = plr.getUniqueId();

                //remove existing holograms
                if (holosByExecutingUser.containsKey(uuid)) {
                    AtomicInteger holos = new AtomicInteger(0);
                    holosByExecutingUser.get(uuid).stream()
                            .map(Home::getHologram)
                            .filter(Objects::nonNull)
                            .forEach(hologram -> {
                                hologram.delete();
                                holos.incrementAndGet();
                            });
                    holosByExecutingUser.remove(uuid);

                    getPlugin().getServer().getScheduler().cancelTask(autoRemoveTaskIdByUser.get(uuid));
                    autoRemoveTaskIdByUser.remove(uuid);

                    plr.sendMessage(
                            "§6Deine aktuellen Home-Hologramme (§b" + holos.get() + " §6Stück) wurden zuerst entfernt.");
                }

                //show holograms
                List<Home> homes = showHolograms(plr, radius);
                if (homes == null) {
                    return;
                }
                //add remove task
                BukkitTask task = getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                    try {
                        AtomicInteger holos = new AtomicInteger(0);
                        homes.stream()
                                .map(Home::getHologram)
                                .forEach(hologram -> {
                                    hologram.delete();
                                    holos.incrementAndGet();
                                });
                        holosByExecutingUser.remove(uuid);
                        autoRemoveTaskIdByUser.remove(uuid);
                        plr.sendMessage("§6Deine Home-Hologramme sind nun abgelaufen und wurden daher entfernt.");
                    } catch (Exception ex) {
                        handleException(new Exception("showHologramsCmd#asynctask#synctask(" + plr + ", " + radius + ")", ex));
                    }
                }, 20 * hologramDuration);

                //save user data
                holosByExecutingUser.put(uuid, homes);
                autoRemoveTaskIdByUser.put(uuid, task.getTaskId());
            } catch (Exception ex) {
                handleException(new Exception("showHologramsCmd(" + plr + ", " + radius + ")", ex));
            }
        });

        return true;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    @Nullable
    public static Integer getIfNotNegativeInt(@NonNull String numberStr) {
        if (numberStr.length() < 1) {
            return null;
        }
        if (StringUtils.isNumeric(numberStr)) {
            int i = Integer.parseInt(numberStr);
            if (i >= 0) {
                return i;
            }
        }
        return null;
    }

    private boolean isSimilarHomeDisplayed(Home home) {
        for (List<Home> homes : holosByExecutingUser.values()) {
            if (homes.contains(home)) {
                return true;
            }
        }
        return false;
    }

    private List<Home> getHomesInRadius(Player plr, Location center, int xzRadius) {
        List<Home> homes = new ArrayList<>();
        try {
            int radius2 = xzRadius * xzRadius; //use squared distance, faster
            //makes distance just X and Z sensible
            Location centerYZero = center.clone();
            centerYZero.setY(0);

            File[] files = essentialsUserdataFolder.listFiles();
            if (files == null) {
                plr.sendMessage("Fehler beim Lesen der Essentials-Userdaten.");
                throw new IOException("Could not get essentials userdata folder contents!");
            }
            List<File> userDataFiles = Arrays.stream(files)
                    .filter(file -> !file.isDirectory() && file.getName().endsWith(".yml"))
                    .collect(Collectors.toList());

            long lastProgessSent = System.currentTimeMillis();
            final long start = lastProgessSent;

            for (int i = 0, fileAmount = userDataFiles.size(); i < fileAmount; i++) {
                File file = userDataFiles.get(i);

                long currMillis = System.currentTimeMillis();
                if ((currMillis - 2000) > lastProgessSent) {
                    lastProgessSent = currMillis;
                    plr.sendMessage(
                            "§6Ladefortschritt: §b" + i + " §6von §b" + fileAmount + " §6Userdaten gelesen (§b" + homes.size() +
                                    " relevante Homes gefunden)");
                }

                EssentialsDataUser essentialsDataUser = EssentialsDataUser.readHomes(file);
                if (essentialsDataUser == null) {
                    continue;
                }
                essentialsDataUser.stream()
                        .filter(loc -> center.getWorld().equals(loc.getLocation().getWorld()))
                        .forEach(home -> {
                            //filter homes for distance, can't be done nice with Stream methods cause we need both objects later on
                            //makes distance just X and Z sensible
                            Location yZero = home.getLocation().clone();
                            yZero.setY(0);
                            if (yZero.distanceSquared(centerYZero) < radius2) { //use squared distance, faster
                                homes.add(home);
                            }
                        });
            }

            long end = System.currentTimeMillis();
            @SuppressWarnings("UnnecessaryParentheses")
            double dur = (((double) (end - start)) / 1000d);
            long l = (long) Math.floor(dur);
            String durStr = Double.toString(dur);
            durStr = durStr.substring(0, Math.min(durStr.length(), Long.toString(l).length() + 3));

            plr.sendMessage("§6Ladevorgang abgeschlossen. §b" + homes.size() + " §6relevante Homes gefunden. " +
                    "Dauer: §b" + durStr + "§6s");
        } catch (Exception ex) {
            handleException(new Exception("getHomesInRadius(" + plr + ", " + center + ", " + xzRadius + ")", ex));
        }
        return homes;
    }

    private List<Home> showHolograms(@NonNull Player plr, int radius) {
        try {
            List<Home> homes = getHomesInRadius(plr, plr.getLocation(), radius).stream()
                    .filter(home -> !isSimilarHomeDisplayed(home))
                    .collect(Collectors.toList());
            plr.sendMessage("§6Die Homes werden nun dargestellt. " +
                    "(Geschwindigkeit: §b" + (hologramCreationsPerCommandPerTick * 20) + " §6pro Sekunde)");
            final int amountOfRepeats = (homes.size() % hologramCreationsPerCommandPerTick) + 1;

            for (int i = 0; i < amountOfRepeats; i++) {
                final int iFinal = i;
                final int jStart = i * amountOfRepeats;
                final int max = Math.min(jStart + amountOfRepeats, homes.size());
                getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                    try {
                        for (int j = jStart; j < max; j++) {
                            Home home = homes.get(j);

                            home.showHologram(this);
                        }
                        if (iFinal == (amountOfRepeats - 1)) {
                            plr.sendMessage("§6Nun sind alle Home-Hologramme dargestellt.");
                        }
                    } catch (Exception ex) {
                        handleException(ex);
                    }
                }, i);
            }

            return homes;
        } catch (Exception ex) {
            handleException(new Exception("showHolograms(" + plr + ", " + radius + ")", ex));
            return null;
        }
    }

    public static List<UUID> getPlayersWithShowHomesPermission() {
        Collection<? extends Player> plrs = Bukkit.getOnlinePlayers();
        return plrs.stream()
                .filter(plr -> plr.hasPermission("showhomes.see"))
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toList());
    }

    private boolean showHelp(Player plr) {
        plr.sendMessage("§6ShowHomes Hilfe:");
        plr.sendMessage("§6ShowHomes zeigt Homes aller User in deiner Umgebung als Hologramme an.");
        plr.sendMessage("Command Aliases: " + Joiner.on(", ").join(getPlugin().getCommand("showhomes").getAliases()));

        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes"))
                .append("/showhomes").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Zeigt die Homes im Standardumkreis 20").color(ChatColor.GOLD) //TODO default radius
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes 20")) //TODO default radius
                .append("/showhomes").color(ChatColor.RED)
                .append(" <radius>").color(ChatColor.BLUE)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Zeigt die Homes im gegebenen ").color(ChatColor.GOLD)
                .append("Umkreis").color(ChatColor.BLUE)
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes remove"))
                .append("/showhomes remove").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Entfernt von dir erstellte Home-Hologramme").color(ChatColor.GOLD)
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes remove all"))
                .append("/showhomes remove all").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Entfernt alle erstellten Home-Hologramme").color(ChatColor.GOLD)
                .create());
        return true;
    }

    public static void handleException(Throwable t) {
        String exceptionString = ExceptionUtils.getFullStackTrace(t);
        Player janmm14 = Bukkit.getPlayerExact("Janmm14");
        if (janmm14 != null) {
            janmm14.sendMessage(exceptionString);
        }
        t.printStackTrace();
    }

    class ShowHomesCommandHandler implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
            try {
                if (!cmd.getName().equalsIgnoreCase("showhomes")) {
                    getPlugin().getLogger().warning(
                            "[ShowHomes] The command '" + cmd.getName() + "', executed by " + cs +
                                    " was forwarded to ShowHomes, but it is not handled by it: " +
                                    '/' + alias + ' ' + Joiner.on(' ').join(args));
                    cs.sendMessage("§cEs ist ein interner Fehler beim Verarbeiten dieses Kommandos aufgetreten.");
                    return true;
                }
                if (!cs.hasPermission("showhomes.execute")) {
                    cs.sendMessage("§cDu bist nicht berechtigt, dieses Kommnado zu benutzen!");
                    return true;
                }

                if (!(cs instanceof Player)) {
                    cs.sendMessage("§cNur Spieler können dieses Kommando verwenden!");
                    return true;
                }
                final Player plr = (Player) cs;
                if (args.length == 0) {
                    return showHologramsCmd(plr, defaultRadius);
                }
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                        return showHelp(plr);
                    }
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (plr.hasPermission("showhomes.reload")) {
                            reloadConfig();
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("all")) {
                                AtomicInteger holos = new AtomicInteger(0);
                                holosByExecutingUser.values()
                                        .forEach(value -> value.stream()
                                                .map(Home::getHologram)
                                                .filter(Objects::nonNull)
                                                .forEach(hologram -> {
                                                    hologram.delete();
                                                    holos.incrementAndGet();
                                                }));
                                holosByExecutingUser.clear();
                                autoRemoveTaskIdByUser.values()
                                        .forEach(getPlugin().getServer().getScheduler()::cancelTask);
                                autoRemoveTaskIdByUser.clear();
                                plr.sendMessage("§aAlle Home-Hologramme wurden entfernt. (§b" + holos.get() + "§aStück)");
                                return true;
                            } else {
                                showHelp(plr);
                            }
                        }
                        UUID uuid = plr.getUniqueId();
                        List<Home> homes = holosByExecutingUser.get(uuid);
                        if (homes == null) {
                            plr.sendMessage("§cDu hast keine Home-Hologramme heraufbeschworen.");
                            return true;
                        }
                        int holos = homes.size();
                        homes.stream()
                                .map(Home::getHologram)
                                .filter(Objects::nonNull)
                                .forEach(Hologram::delete);
                        holosByExecutingUser.remove(uuid);
                        getPlugin().getServer().getScheduler().cancelTask(autoRemoveTaskIdByUser.get(uuid));
                        autoRemoveTaskIdByUser.remove(uuid);
                        plr.sendMessage("§aDeine Home-Hologramme wurden entfernt. (§b" + holos + " §aStück)");
                        return true;
                    }
                    Integer radius = getIfNotNegativeInt(args[0]);
                    if (radius == null) {
                        plr.sendMessage("§cDas ist keine (gültige) Zahl: " + args[0]);
                        return showHelp(plr);
                    }
                    if (radius > maxRadius) {
                        plr.sendMessage("§6Radius gekappt auf §b" + maxRadius + " §6Blöcke.");
                        radius = maxRadius;
                    }
                    if (radius < minRadius) { //TODO make configurable
                        plr.sendMessage("§6Radius geringer als Mindestradius, daher erhöht auf §b" + minRadius + " §6Blöcke.");
                        radius = minRadius;
                    }
                    return showHologramsCmd(plr, radius);
                }

                return true;
            } catch (Exception ex) {
                handleException(new Exception("onCommand", ex));
                return true;
            }
        }
    }
}
