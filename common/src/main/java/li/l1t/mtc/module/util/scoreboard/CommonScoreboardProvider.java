/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.util.scoreboard;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.util.ScoreboardHelper;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Scoreboards shown by the PvP Stats module, handling displaying and updating.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class CommonScoreboardProvider extends ScoreboardHelper {
    private final List<String> duplicateItemPrefixes = Arrays.asList("§0§f", "§1§f", "§2§f", "§3§f", "§4§f",
            "§5§f", "§6§f", "§7§f", "§8§f", "§9§f", "§a§f", "§b§f", "§c§f", "§d§f", "§e§f", "§f§f");
    private final Map<String, BoardItem> globalItems = new HashMap<>();
    private final Set<UUID> objectiveExistingPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final String OBJECTIVE_NAME = "mtc-side";

    @InjectMe
    public CommonScoreboardProvider(Plugin plugin) {
        super(plugin);
    }

    public void enable() {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(
                getPlugin(),
                () -> {
                    new ArrayList<>(getPlugin().getServer().getOnlinePlayers())
                            .forEach(plr -> removeObjective(plr, OBJECTIVE_NAME));
                    new ArrayList<>(getPlugin().getServer().getOnlinePlayers())
                            .forEach(this::updateScoreboard);
                }
        );
    }

    public void cleanUp(Player plr) {
        objectiveExistingPlayers.remove(plr.getUniqueId());
    }

    public void updateScoreboard(Player player) {
        prepareObjectiveFor(player);
        int i = 0;
        Set<String> usedItemNames = new HashSet<>(globalItems.size());
        for (BoardItem item : globalItems.values()) {
            if (item.isVisibleTo(player)) {
                String uniqueValue = makeItemNameUnique(item.getValue(player), usedItemNames);
                updateScore(player, OBJECTIVE_NAME, uniqueValue, ++i);
                updateScore(player, OBJECTIVE_NAME, item.getDisplayName(player), ++i);
            }
        }
    }

    private void prepareObjectiveFor(Player player) {
        if (!objectiveExistingPlayers.add(player.getUniqueId())) { //may produce a client NPE on reload - Mojang logs & discards that
            removeObjective(player, OBJECTIVE_NAME);
        }
        if (player.getScoreboard() == null) {
            player.setScoreboard(getPlugin().getServer().getScoreboardManager().getMainScoreboard());
        }
        createIntObjective(player, OBJECTIVE_NAME, "§e§l " + player.getName(), DisplaySlot.SIDEBAR);
    }

    private String makeItemNameUnique(String name, Set<String> usedNames) {
        while (!usedNames.add(name)) {
            name = duplicateItemPrefixes.get(RandomUtils.nextInt(duplicateItemPrefixes.size())) + name;
        }
        return name;
    }
}
