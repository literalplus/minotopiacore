package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This module reads essentials userdata files and show you homes of all users in a given radius on command.
 *
 * @author Janmm14
 */
public class ShowHomesModule extends ConfigurableMTCModule {

    public static final String NAME = "ShowHomes";

    private static final int DEFAULT_RADIUS_DEFAULT = 20;
    private static final int MIN_RADIUS_DEFAULT = 3;
    private static final int MAX_RADIUS_DEFAULT = 50;
    private static final int HOLOGRAM_DURATION_DEFAULT = 60;
    private static final int HOLOGRAM_RATE_LIMIT_DEFAULT = 20;

    private File essentialsUserdataFolder;

    private final Multimap<UUID, Home> holosByExecutingUser = HashMultimap.create(3, 40);
    /** Automatic holograms remove task */
    private final Map<UUID, Integer> taskIdByUser = new HashMap<>();

    private int defaultRadius = DEFAULT_RADIUS_DEFAULT;
    private int minRadius = MIN_RADIUS_DEFAULT;
    private int maxRadius = MAX_RADIUS_DEFAULT;
    private int hologramDuration = HOLOGRAM_DURATION_DEFAULT;
    private int hologramRateLimit = HOLOGRAM_RATE_LIMIT_DEFAULT;

    public ShowHomesModule() {
        super(NAME, "modules/showhomes.cfg.yml", ClearCacheBehaviour.RELOAD);
    }

    @Override
    public boolean canBeEnabled(MTC plugin) {
        if (!super.canBeEnabled(plugin)) {
            return false;
        }
        boolean success = true;
        if (!plugin.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            plugin.getLogger().warning("[ShowHomesModule] Could not enable, because HolographicDisplays is not available!");
            success = false;
        }
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            plugin.getLogger().warning("[ShowHomesModule] Could not enable, because Essentials is not available!");
            success = false;
        }
        return success;
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        File essentialsPluginFolder = plugin.getServer().getPluginManager().getPlugin("Essentials").getDataFolder();
        essentialsUserdataFolder = new File(essentialsPluginFolder, "userdata");

        setConfigDefaults();
        readConfig();
        configuration.options().copyDefaults(true).copyHeader(true);
        save();
        plugin.setExec(new ShowHomesCommand(this), "showhomes");
        plugin.setExecAndCompleter(new ChangeHomeCommand(this), "homeutil");
    }

    @Override
    protected void reloadImpl() {
        super.reloadConfig();
        setConfigDefaults();
        readConfig();
        save();
    }

    @Override
    public void disable(MTC plugin) {
        super.disable(plugin);

        holosByExecutingUser.values().stream()
                .map(Home::getHologram)
                .filter(Objects::nonNull)
                .forEach(Hologram::delete);
        holosByExecutingUser.clear();
        taskIdByUser.values()
                .forEach(plugin.getServer().getScheduler()::cancelTask);
        taskIdByUser.clear();
    }

    private void setConfigDefaults() {
        configuration.addDefault("defaultRadius", DEFAULT_RADIUS_DEFAULT);
        configuration.addDefault("minRadius", MIN_RADIUS_DEFAULT);
        configuration.addDefault("maxRadius", MAX_RADIUS_DEFAULT);
        configuration.addDefault("hologramDuration", HOLOGRAM_DURATION_DEFAULT);
        configuration.addDefault("hologramCreationsPerCommandPerTick", HOLOGRAM_RATE_LIMIT_DEFAULT);
    }

    private void readConfig() {
        defaultRadius = configuration.getInt("defaultRadius");
        minRadius = configuration.getInt("minRadius");
        maxRadius = configuration.getInt("maxRadius");
        hologramDuration = configuration.getInt("hologramDuration");
        hologramRateLimit = configuration.getInt("hologramCreationsPerCommandPerTick");
    }

    @Nullable
    public static Integer parsePositiveInt(@NonNull String numberStr) {
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

    public boolean isSimilarHomeDisplayed(Home home) {
        return holosByExecutingUser.containsValue(home);
    }

    public List<Home> getHomesInRadius(Player plr, Location center, int xzRadius) throws IOException {
        List<Home> homes = new ArrayList<>();
        int radius2 = xzRadius * xzRadius; //use squared distance, faster
        //make distance just X and Z sensible
        Location centerYZero = center.clone();
        centerYZero.setY(0);

        File[] files = essentialsUserdataFolder.listFiles();
        if (files == null) {
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
            if ((currMillis - 10 * 1000) > lastProgessSent) {
                lastProgessSent = currMillis;
                plr.sendMessage("§6Ladefortschritt: §b" + i + "§6 / §b" + fileAmount + "§6 Userdaten gelesen" +
                        " (relevante Homes: §b" + homes.size() + "§6)");
            }

            EssentialsDataUser essentialsDataUser = EssentialsDataUser.fromFile(this, file);
            if (essentialsDataUser == null) {
                continue;
            }
            essentialsDataUser.getHomes().stream()
                    .filter(loc -> center.getWorld().equals(loc.getLocation().getWorld()))
                    .forEach(home -> {//filter homes for distance, can't be done nice with Stream methods cause we need both objects later on
                        //make distance just X and Z sensible
                        Location yZero = home.getLocation().clone();
                        yZero.setY(0);
                        if (yZero.distanceSquared(centerYZero) < radius2) { //use squared distance, faster
                            homes.add(home);
                        }
                    });
        }

        long end = System.currentTimeMillis();
        double dur = ((double) (end - start)) / 1000d;
        DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.GERMAN);
        format.applyPattern("###.##");

        plr.sendMessage("§b" + homes.size() + " §6Homes, gefunden in §b" + format.format(dur) + "§6 Sekunden.");

        return homes;
    }

    public static Set<UUID> getPlayerUuidsWithShowHomesPermission() {
        Collection<? extends Player> plrs = Bukkit.getOnlinePlayers();
        return plrs.stream()
                .filter(plr -> plr.hasPermission("showhomes.see"))
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toSet());
    }

    @Deprecated //TODO remove deprecation
    public void handleException(Throwable t) {
        String exceptionString = ExceptionUtils.getFullStackTrace(t);
        Player janmm14 = Bukkit.getPlayerExact("Janmm14");
        if (janmm14 != null) {
            janmm14.sendMessage(exceptionString);
        }
        t.printStackTrace();
    }

    public Multimap<UUID, Home> getHolosByExecutingUser() {
        return holosByExecutingUser;
    }

    public Map<UUID, Integer> getTaskIdByUser() {
        return taskIdByUser;
    }

    public File getEssentialsUserdataFolder() {
        return this.essentialsUserdataFolder;
    }

    public int getDefaultRadius() {
        return this.defaultRadius;
    }

    public int getMinRadius() {
        return this.minRadius;
    }

    public int getMaxRadius() {
        return this.maxRadius;
    }

    public int getHologramDuration() {
        return this.hologramDuration;
    }

    public int getHologramRateLimit() {
        return this.hologramRateLimit;
    }
}
