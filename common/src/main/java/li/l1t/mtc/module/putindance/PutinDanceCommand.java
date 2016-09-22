/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance;

import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.exception.UserException;
import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.board.generator.BoardGenerator;
import li.l1t.mtc.module.putindance.api.game.Game;
import li.l1t.mtc.module.truefalse.TrueFalseModule;
import li.l1t.mtc.util.DyeColorConversions;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * The main command for PutinDance, providing management as well as user operations.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
class PutinDanceCommand implements CommandExecutor {
    private final PutinDanceModule module;

    PutinDanceCommand(PutinDanceModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            try {
                if (attemptHandleCommand(sender, args)) {
                    return true;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new UserException(e.getMessage());
            }
        }
        return sendHelpTo(sender);
    }

    private boolean attemptHandleCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player && attemptHandlePlayerOnlyCommand((Player) sender, args)) {
            return true;
        } else if (attemptHandleAdminCommand(sender, args)) {
            return true;
        }
        return false;
    }

    private boolean attemptHandlePlayerOnlyCommand(Player player, String[] args) {
        if (player.hasPermission(PutinDanceModule.ADMIN_PERMISSION)) {
            switch (args[0].toLowerCase()) {
                case "wand":
                    return handleGetWand(player);
                case "setspawn":
                    return handleSetSpawn(player);
                case "spawn":
                    return handleTeleportToSpawn(player);
            }
        }
        switch (args[0].toLowerCase()) {
            case "join":
                return handleJoin(player);
            case "leave":
                return handleLeave(player);
        }
        return false;
    }

    private boolean attemptHandleAdminCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission(PutinDanceModule.ADMIN_PERMISSION)) {
            switch (args[0].toLowerCase()) {
                case "new":
                    return handleNew();
                case "abort":
                    return handleAbort();
                case "start":
                    return handleStart();
                case "gen":
                    return handleRequestGeneration(sender);
                case "tick":
                    return handleTick(sender);
                case "status":
                    return handleStatus(sender);
                case "addcolor":
                    return handleAddColor(sender, args[1]);
                case "removecolor":
                    return handleRemoveColor(sender, args[1]);
                case "listcolors":
                    return handleListColors(sender);
                case "listallcolors":
                    return handleListAllColors(sender);
            }
        }
        return false;
    }

    private boolean handleTick(CommandSender sender) {
        checkThatThereIsCurrentlyAGame();
        if (!module.getCurrentGame().isTickable()) {
            throw new UserException("Der letzte Tick ist noch nicht fertig!");
        }
        module.getCurrentGame().tick();
        MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "Tick!");
        return true;
    }

    private boolean handleAddColor(CommandSender sender, String arg) {
        DyeColor color = parseDyeColor(arg);
        module.getConfig().getValidColors().add(color);
        module.save();
        MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "%s wird jetzt im Spielfeld verwendet!", color);
        return true;
    }

    private DyeColor parseDyeColor(String arg) {
        try {
            return DyeColor.valueOf(arg.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            throw new UserException(e.getMessage());
        }
    }

    private boolean handleRemoveColor(CommandSender sender, String arg) {
        DyeColor color = parseDyeColor(arg);
        module.getConfig().getValidColors().remove(color);
        module.save();
        MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "%s wird jetzt nicht mehr im Spielfeld verwendet!", color);
        return true;
    }

    private boolean handleListColors(CommandSender sender) {
        MessageType.LIST_HEADER.sendTo(sender, "Folgende Farben werden im Spielfeld verwendet:");
        module.getConfig().getValidColors().forEach(color -> sendColorInfo(sender, color));
        return true;
    }

    private boolean handleListAllColors(CommandSender sender) {
        MessageType.LIST_HEADER.sendTo(sender, "Folgende Farben sind verfügbar:");
        Arrays.stream(DyeColor.values()).forEach(color -> sendColorInfo(sender, color));
        return true;
    }

    private void sendColorInfo(CommandSender sender, DyeColor color) {
        MessageType.LIST_ITEM.sendTo(
                sender, "%s%s", DyeColorConversions.chatColorFromDye(color), color);
    }

    private boolean handleGetWand(Player player) {
        module.getWandHandler().startBoundarySession(player);
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Hier ist dein Zauberstab!");
        return true;
    }

    private boolean handleSetSpawn(Player player) {
        module.setSpawnLocation(XyLocation.of(player.getLocation()));
        module.save();
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Spawn gesetzt!");
        return true;
    }

    private boolean handleTeleportToSpawn(Player player) {
        player.teleport(module.getConfig().getSpawnLocation());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Willkommen am PD-Spawn!");
        return true;
    }

    private boolean handleJoin(Player player) {
        Game game = getOpenGameOrFail();
        game.addPlayer(player);
        return true;
    }

    private Game getOpenGameOrFail() {
        if (!module.hasGame()) {
            throw new UserException("Es läuft momentan kein Spiel :(");
        } else if (!module.hasOpenGame()) {
            throw new UserException("Das aktuelle Spiel hat leider schon begonnen! :(");
        }
        return module.getCurrentGame();
    }

    private boolean handleLeave(Player player) {
        player.sendMessage("§6/spawn");
        return true;
    }

    private boolean handleRequestGeneration(CommandSender sender) {
        checkThatNoGameIsRunning();
        BoardGenerator generator = module.createGenerator();
        generator.startGeneration(module.getPlugin(), 2L);
        MessageType.RESULT_LINE.sendTo(sender, "Generiere Spielfeld für PutinDance...");
        MessageType.RESULT_LINE.sendTo(sender, "Dies kann einige Sekunden dauern.");
        MessageType.RESULT_LINE.sendTo(sender, "Du wirst benachrichtigt, sobald es fertig ist.");
        return true;
    }

    private boolean handleStatus(CommandSender sender) {
        showGameStatusTo(sender);
        if (!module.hasGame()) {
            Board board = module.getCurrentBoard();
            if (board == null) {
                MessageType.RESULT_LINE.sendTo(sender, "Es gibt momentan kein Spielfeld. /pd gen");
            } else {
                showBoardStatus(sender, board);
            }
        }
        return true;
    }

    private void showGameStatusTo(CommandSender sender) {
        if (!module.hasGame()) {
            MessageType.RESULT_LINE.sendTo(sender, "Es gibt momentan kein Spiel.");
        } else {
            Game game = module.getCurrentGame();
            if (game.isOpen()) {
                MessageType.RESULT_LINE.sendTo(sender, "Das Spiel ist momentan offen. (/pd join geht)");
            } else {
                MessageType.RESULT_LINE.sendTo(sender, "Das Spiel ist nicht offen.");
            }
            showBoardStatus(sender, game.getBoard());
        }
    }

    private void showBoardStatus(CommandSender sender, Board board) {
        MessageType.RESULT_LINE.sendTo(sender, "Es gibt momentan ein Spielfeld.");
        MessageType.RESULT_LINE.sendTo(sender,
                "  Layer gesamt: %d (übrig: %d)",
                board.getLayerCount(), board.getActiveLayerCount()
        );
        MessageType.LIST_HEADER.sendTo(sender, "Aktuell aktive Layer (nicht nur Luft):");
        List<Layer> allLayers = board.getAllLayers();
        for (int i = 0; i < allLayers.size(); i++) {
            showLayerStatus(sender, i, allLayers.get(i));
        }
    }

    private void showLayerStatus(CommandSender sender, int index, Layer layer) {
        if (layer.hasBlocksLeft()) {
            MessageType.LIST_ITEM.sendTo(sender,
                    "#%d: %s", index, buildLayerColorString(layer)
            );
        }
    }

    private String buildLayerColorString(Layer layer) {
        StringBuilder sb = new StringBuilder();
        layer.getActiveColors().forEach(dye -> sb
                .append(DyeColorConversions.chatColorFromDye(dye)).append(dye.name()).append(" ")
        );
        return sb.toString();
    }

    private boolean handleStart() {
        checkThatThereIsCurrentlyAGame();
        module.getCurrentGame().startGame();
        return true;
    }

    private boolean handleNew() {
        checkThatNoGameIsRunning();
        checkThatABoardIsLoaded();
        module.newGame();
        return true;
    }

    private void checkThatNoGameIsRunning() {
        if (module.hasGame()) {
            throw new UserException("Es läuft noch ein Spiel! Abbrechen mit /pd abort");
        }
    }

    private void checkThatABoardIsLoaded() {
        if (!module.hasBoard()) {
            throw new UserException("Bitte generiere zuerst ein Spielfeld! /pd gen");
        }
    }

    private boolean handleAbort() {
        checkThatThereIsCurrentlyAGame();
        module.abortGame();
        return true;
    }

    private void checkThatThereIsCurrentlyAGame() {
        if (!module.hasGame()) {
            throw new UserException("Es läuft momentan kein Spiel! /pd new");
        }
    }


    private boolean sendHelpTo(CommandSender receiver) {
        receiver.sendMessage("§9/pd join §2Betritt ein offenes Spiel");
        if (receiver.hasPermission(TrueFalseModule.ADMIN_PERMISSION)) {
            receiver.sendMessage("§9/pd wand §2Gibt den Zauberstab zum Markieren der Spielfeldränder");
            receiver.sendMessage("§9/pd new §2Öffnet ein neues Spiel");
            receiver.sendMessage("§9/pd close §2Schließt das Spiel, d.h. kein /pd join mehr");
            receiver.sendMessage("§9/pd stop §2Beendet das aktuelle Spiel");
            receiver.sendMessage("§9/pd tick §2Entfernt die nächste Farbe");
            receiver.sendMessage("§9/pd setspawn §2Setzt den Spawn");
            receiver.sendMessage("§9/pd spawn §2Teleportiert dich zum Spawn");
            receiver.sendMessage("§9/pd status §2zeigt Infos zum Spiel und Spielfeld");
            receiver.sendMessage("§9/pd addcolor §2Verwendet eine Farbe im Spielfeld");
            receiver.sendMessage("§9/pd removecolor §2Verwendet eine Farbe nicht mehr im Spielfeld");
            receiver.sendMessage("§9/pd listcolors §2Zeigt verwendete Wollfarben für das Spielfeld");
            receiver.sendMessage("§9/pd listallcolors §2Zeigt verfügbare Wollfarben für das Spielfeld");
        }
        return true;
    }
}
