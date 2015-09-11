package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This module offers utility for Essentials home management.
 *
 * @author Janmm14
 */
public class ShowHomesModule extends ConfigurableMTCModule {

    public static final String NAME = "ShowHomes";

    private File essentialsUserdataFolder;

    private final Multimap<UUID, Home> holosByExecutingUser = HashMultimap.create(3, 40);
    /**
     * Automatic holograms remove task
     */
    private final Map<UUID, Integer> taskIdByUser = new HashMap<>();

    private int defaultRadius = ShowHomesConstants.DEFAULT_RADIUS_DEFAULT;
    private int maxRadius = ShowHomesConstants.MAX_RADIUS_DEFAULT;
    private int hologramDuration = ShowHomesConstants.HOLOGRAM_DURATION_DEFAULT;
    private int hologramRateLimit = ShowHomesConstants.HOLOGRAM_RATE_LIMIT_DEFAULT;

    private EssentialsPlayerDataManager essentialsPlayerDataManager;

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
            plugin.getLogger().warning("[ShowHomesModule] Required plugin HolographicDisplays is missing, won't start up!");
            success = false;
        }
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            plugin.getLogger().warning("[ShowHomesModule] Required plugin Essentials is missing, won't start up!");
            success = false;
        }
        return success;
    }

    @Override
    public void enable(MTC plugin) throws Exception {
        super.enable(plugin);
        File essentialsPluginFolder = plugin.getServer().getPluginManager().getPlugin("Essentials").getDataFolder();
        essentialsUserdataFolder = new File(essentialsPluginFolder, "userdata");
        essentialsPlayerDataManager = new EssentialsPlayerDataManager(this);

        setConfigDefaults();
        readConfig();
        configuration.options().copyDefaults(true).copyHeader(true);
        save();
        plugin.setExec(new ShowHomesCommand(this), "jshowhomes");
        plugin.setExec(new ChangeHomeCommand(this), "jhomeutil");
    }

    @Override
    protected void reloadImpl() {
        super.reloadConfig();
        setConfigDefaults();
        readConfig();
        save();
        essentialsPlayerDataManager.clearCache();
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
        essentialsPlayerDataManager.clearCache();
        essentialsPlayerDataManager = null;
    }

    private void setConfigDefaults() {
        configuration.addDefault("defaultRadius", ShowHomesConstants.DEFAULT_RADIUS_DEFAULT);
        configuration.addDefault("maxRadius", ShowHomesConstants.MAX_RADIUS_DEFAULT);
        configuration.addDefault("hologramDuration", ShowHomesConstants.HOLOGRAM_DURATION_DEFAULT);
        configuration.addDefault("hologramCreationsPerCommandPerTick", ShowHomesConstants.HOLOGRAM_RATE_LIMIT_DEFAULT);
    }

    private void readConfig() {
        defaultRadius = configuration.getInt("defaultRadius");
        maxRadius = configuration.getInt("maxRadius");
        hologramDuration = configuration.getInt("hologramDuration");
        hologramRateLimit = configuration.getInt("hologramCreationsPerCommandPerTick");
    }

    /**
     * Parses a string containing only numeric chars to an Integer
     *
     * @param numberStr the String to parse
     * @return the Integer parsed or {@code null} if the input is not a valid number in the meanings of this method
     */
    @Nullable
    public static Integer parsePositiveInt(@NotNull String numberStr) {
        if (numberStr.isEmpty()) { //required as StringUtils.isNumeric(numberStr) returns true on empty string
            return null;
        }
        if (StringUtils.isNumeric(numberStr)) {
            int i = Integer.parseInt(numberStr); //should not throw NumberFormatEception, as its tested before
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

            EssentialsPlayerData essentialsPlayerData = essentialsPlayerDataManager.getByFile(file);
            if (essentialsPlayerData == null) {
                continue;
            }
            essentialsPlayerData.getHomes().stream()
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

        plr.sendMessage("§b" + homes.size() + " §6Homes, gefunden in §b" + ShowHomesConstants.DECIMAL_FORMAT.format(dur) + "§6 Sekunden.");

        return homes;
    }

    public static Set<UUID> getPermittedPlayerUUIDs() {
        Collection<? extends Player> plrs = Bukkit.getOnlinePlayers();
        return plrs.stream()
                .filter(plr -> plr.hasPermission("showhomes.see"))
                .map(OfflinePlayer::getUniqueId) // why can't we use Player::getUniqueId, java pls allow method references by subclass, using OfflinePlayer looks ugly and leads to misinterpretation we get the oflline uuid
                .collect(Collectors.toSet());
    }

    /**
     * Handles exceptions occurred by first sending it to players having the {@code mtc.err.notify} permission
     * <p>
     * After that it calls t.printStackTrace();
     *
     * @param t The exception to handle
     */
    public void handleException(Throwable t) {
        String exceptionString = ExceptionUtils.getFullStackTrace(t);

        getPlugin().getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("mtc.err.notify"))
                .forEach(player -> player.sendMessage(exceptionString));

        t.printStackTrace();
    }

    public EssentialsPlayerDataManager getEssentialsPlayerDataManager() {
        return essentialsPlayerDataManager;
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
