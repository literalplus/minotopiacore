/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.scoreboard;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import li.l1t.common.collections.Pair;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.ExternalDependencies;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.util.ScoreboardHelper;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages Scoreboards shown by the PvP Stats module, handling displaying and updating.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
@ExternalDependencies("com.comphenix")
public class CommonScoreboardProvider extends ScoreboardHelper {
    public static final String OBJECTIVE_NAME = "mtc-side";
    private final List<String> duplicateItemPrefixes = Arrays.asList("§0§f", "§1§f", "§2§f", "§3§f", "§4§f",
            "§5§f", "§6§f", "§7§f", "§8§f", "§9§f", "§a§f", "§b§f", "§c§f", "§d§f", "§e§f", "§f§f");
    private final Map<String, BoardItem> globalItems = new HashMap<>();
    private final Set<UUID> objectiveExistingPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<UUID> boardHiddenPlayers = new HashSet<>();

    @InjectMe
    public CommonScoreboardProvider(MTCPlugin plugin) {
        super(plugin, ProtocolLibrary.getProtocolManager());
    }

    public void hideBoardFor(Player player) {
        Preconditions.checkNotNull(player, "player");
        boardHiddenPlayers.add(player.getUniqueId());
        if (objectiveExistingPlayers.remove(player.getUniqueId())) {
            removeObjective(player, OBJECTIVE_NAME);
        }
    }

    public void unhideBoardFor(Player player) {
        Preconditions.checkNotNull(player, "player");
        boardHiddenPlayers.remove(player.getUniqueId());
        updateScoreboardFor(player);
    }

    public boolean isBoardHiddenFor(Player player) {
        Preconditions.checkNotNull(player, "player");
        return boardHiddenPlayers.contains(player);
    }

    public void registerBoardItem(BoardItem item) {
        Preconditions.checkNotNull(item, "item");
        Preconditions.checkNotNull(item.getIdentifier(), "item.getIdentifier()");
        globalItems.put(item.getIdentifier(), item);
    }

    public void cleanUp(Player player) {
        objectiveExistingPlayers.remove(player.getUniqueId());
        boardHiddenPlayers.remove(player.getUniqueId());
    }

    public void updateScoreboardFor(Player player) {
        if (!boardHiddenPlayers.contains(player.getUniqueId())) {
            renderScoreboardWithItems(player, computePersonalItems(player));
        }
    }

    private Map<String, String> computePersonalItems(Player player) {
        return globalItems.values().stream()
                .filter(item -> item.isVisibleTo(player))
                .map(item -> Pair.pairOf(item.getDisplayName(player), item.getValue(player)))
                .filter(pair -> pair.getRight() != null)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private void renderScoreboardWithItems(Player player, Map<String, String> personalItems) {
        if (personalItems.isEmpty()) {
            if(objectiveExistingPlayers.remove(player.getUniqueId())) {
                removeObjective(player, OBJECTIVE_NAME);
            }
            return;
        }
        prepareObjectiveFor(player);
        int i = 0;
        Set<String> usedItemNames = new HashSet<>(globalItems.size());
        for (Map.Entry<String, String> item : personalItems.entrySet()) {
            updateScore(player, OBJECTIVE_NAME, makeItemNameUnique(item.getValue(), usedItemNames), ++i);
            updateScore(player, OBJECTIVE_NAME, makeItemNameUnique(item.getKey(), usedItemNames), ++i);
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

    public Collection<BoardItem> getItems() {
        return ImmutableList.copyOf(globalItems.values());
    }
}
