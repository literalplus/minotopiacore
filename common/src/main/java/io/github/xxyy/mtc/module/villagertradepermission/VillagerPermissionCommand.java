/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.villagertradepermission;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import io.github.xxyy.mtc.module.villagertradepermission.actions.Action;
import io.github.xxyy.mtc.module.villagertradepermission.actions.ActionManager;
import io.github.xxyy.mtc.module.villagertradepermission.actions.PermissionInfoAction;
import io.github.xxyy.mtc.module.villagertradepermission.actions.RemovePermissionAction;
import io.github.xxyy.mtc.module.villagertradepermission.actions.SetPermissionAction;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * A command handler for setting permissions on villagers and to get information about current permissions.
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class VillagerPermissionCommand extends MTCPlayerOnlyCommandExecutor {

    private final VillagerTradePermissionModule module;
    private final ActionManager actionManager;

    public VillagerPermissionCommand(VillagerTradePermissionModule module) {
        this.module = module;
        actionManager = module.getActionManager();
    }

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(plr, label);
            return true;
        }
        switch (args[0]) {
            case "setperm":
            case "setpermission": {
                if (args.length != 2) { //require exact argument count to make sure everyone knows that permissions are spaceless
                    return CommandHelper.msg("§cFalsche Benutzung.\n§6Syntax: §c/" + label + ' ' + args[0].toLowerCase() + " <permission>", plr);
                }
                schedule(plr, new SetPermissionAction(module, args[1]));
                break;
            }
            case "i":
            case "info": {
                schedule(plr, new PermissionInfoAction(module));
                break;
            }
            case "removeperm":
            case "removepermission": {
                schedule(plr, new RemovePermissionAction(module));
                break;
            }
            case "ai":
            case "actioninfo": {
                Action action = actionManager.getScheduledAction(plr);
                if (action == null) {
                    plr.sendMessage("§aDu hast keine Aktion ausgewählt.");
                } else {
                    action.sendActionInfo(plr);
                }
                break;
            }
            case "abort": {
                Action action = actionManager.removeScheduledAction(plr);
                if (action == null) {
                    plr.sendMessage("§aDu hattest keine Aktion ausgewählt.");
                } else {
                    plr.sendMessage("§aAktion §6" + action.getShortDescription() + "§a abgebrochen.");
                }
                break;
            }
            default: {
                plr.sendMessage("§cUnbekannte Aktion §6" + args[0] + "§c. Hilfe mit §6/" + label + " help§c.");
                break;
            }
            case "help": {
                sendHelp(plr, label);
            }
        }
        return true;
    }

    /**
     * Schedules the given action for the given player.
     * <br><br>
     * This also informes the player if it overrides their previous action and invokes {@link Action#sendActionInfo(Player)}
     * @param plr the player to schedule the action for
     * @param action the action to schedule
     */
    private void schedule(Player plr, Action action) {
        if (actionManager.hasAction(plr)) {
            plr.sendMessage("§cDeine zuvor gewählte Aktion wurde überschrieben.");
        }
        actionManager.scheduleAction(plr, action)
            .sendActionInfo(plr);
    }

    private void sendHelp(Player plr, String label) {
        plr.sendMessage("§6MTC VillagerTradePermissionModule");
        plr.sendMessage("§aDie Befehle in§c rot§a arbeiten mit dem anschließend angeklickten Villager.");
        plr.sendMessage("§c/" + label + " info|i §7-§a Informationen über die benötigte Permission zum Handeln");
        plr.sendMessage("§6/" + label + " actioninfo|ai §7-§a Informationen über die aktuell gewählte Villager-Aktion");
        plr.sendMessage("§c/" + label + " setperm[ission] <permission> §7-§a Setzt die benötigte Permisison zum Handeln");
        plr.sendMessage("§c/" + label + " removeperm[ission] §7-§a Macht einen Villager für jeden zugänglich");
        plr.sendMessage("§6/" + label + " abort §7-§a Bricht die aktuelle Aktion ab");
    }
}
