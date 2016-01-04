package io.github.xxyy.mtc.module.villagertradepermission;

import com.google.common.collect.Maps;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class VillagerPermissionCommand extends MTCPlayerOnlyCommandExecutor implements Listener {
    private static final String PERMISSION = "mtc.module.villagertradepermission.command";

    private final VillagerTradePermissionModule module;
    private final Map<UUID, Action> scheduledActions = Maps.newHashMapWithExpectedSize(2);

    public VillagerPermissionCommand(VillagerTradePermissionModule module) {
        this.module = module;
        module.getPlugin().getServer().getPluginManager().registerEvents(this, module.getPlugin()); //anti memory leak //click listener not here as it interferes with permission check
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        scheduledActions.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeave(PlayerKickEvent event) {
        scheduledActions.remove(event.getPlayer().getUniqueId());
    }

    public void clearCache() {
        scheduledActions.clear();
    }

    public void disable() {
        clearCache();
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(plr, label);
        }
        switch (args[0]) {
            case "setperm":
            case "setpermission": {
                if (args.length != 2) { //require exact argument count to make sure everyone knows that permissions are spaceless
                    return CommandHelper.msg("§cFalsche Benutzung.\n§6Syntax: §c/" + label + ' ' + args[0].toLowerCase() + " <permission>", plr);
                }
                scheduleAction(plr, new SetPermissionAction(args[1]));
                break;
            }
            case "i":
            case "info": {
                scheduleAction(plr, new PermissionInfoAction());
                break;
            }
            case "removeperm":
            case "removepermission": {
                scheduleAction(plr, new RemovePermissionAction());
                break;
            }
            case "actioninfo": {
                Action action = scheduledActions.get(plr.getUniqueId());
                if (action == null) {
                    plr.sendMessage("§aDu hast keine Aktion ausgewählt.");
                } else {
                    action.sendActionInfo(plr);
                }
                break;
            }
            case "abort": {
                Action action = scheduledActions.remove(plr.getUniqueId());
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

    private void scheduleAction(Player plr, Action action) {
        checkOverrideAction(plr);
        scheduledActions.put(plr.getUniqueId(), action);
        action.sendActionInfo(plr);
    }

    private void checkOverrideAction(Player plr) {
        if (scheduledActions.containsKey(plr.getUniqueId())) {
            plr.sendMessage("§cDu hast die zuvor ausgewählte Aktion noch nicht ausgeführt gehabt.");
        }
    }

    private void sendHelp(Player plr, String label) {
        plr.sendMessage("§6MTC VillagerTradePermissionModule");
        plr.sendMessage("§aDie Befehle in§c rot§a arbeiteten mit dem anschließend angeklickten Villager.");
        plr.sendMessage("§c/" + label + " info|i §7-§a Infrmationen über die benötigte Permission zum Handeln");
        plr.sendMessage("§6/" + label + " actioninfo|ai §7-§a Informationen über die aktuell gewählte Villager-Aktion");
        plr.sendMessage("§c/" + label + " setperm[ission] <permission> §7-§a Setzt die benötigte Permisison zum Handeln");
        plr.sendMessage("§c/" + label + " removeperm[ission] §7-§a Macht einen Villager für jeden zugänglich");
        plr.sendMessage("§6/" + label + " abort §7-§a Bricht die aktuelle Aktion ab");
    }

    /**
     * Checks whether an action was scheduled per command on next villager click and executes it.
     *
     * @param plr      the player who clicked the villager
     * @param selected the clicked villager
     * @return Whether an action was scheduled (if yes, normal click behaviour should be cancelled)
     */
    public boolean doAction(@NotNull Player plr, @NotNull Villager selected) {
        if (!plr.hasPermission(PERMISSION)) {
            return false;
        }
        Action action = scheduledActions.get(plr.getUniqueId());
        if (action == null) {
            return false;
        }
        action.execute(plr, selected);
        scheduledActions.remove(plr.getUniqueId());
        return true;
    }

    public String getPermission() {
        return PERMISSION;
    }

    private interface Action {

        void execute(@NotNull Player plr, @NotNull Villager selected);

        void sendActionInfo(@NotNull Player plr);

        String getShortDescription();
    }

    private final class SetPermissionAction implements Action {

        @NotNull
        private final String newPermission;

        private SetPermissionAction(@NotNull String newPermission) {
            this.newPermission = newPermission;
        }

        @Override
        public void execute(@NotNull Player plr, @NotNull Villager selected) {
            VillagerInfo villagerInfo = module.findVillagerInfo(selected);
            if (villagerInfo == null) {
                villagerInfo = VillagerInfo.createNewBy(selected);
            }
            String oldPermission = villagerInfo.getPermission();
            villagerInfo.setPermission(newPermission);
            module.save();
            plr.sendMessage("§aDieser Villager ist nun mit der Permission §6" + newPermission + " §averfügbar.");
            if (oldPermission == null) {
                plr.sendMessage("§aVorher war er jedem zugänglich.");
            } else {
                plr.sendMessage("§aVorher war er mit der Permission §6" + oldPermission + " §azugänglich.");
            }
        }

        @Override
        public void sendActionInfo(@NotNull Player plr) {
            plr.sendMessage("§aDer nächste von dir angeklickte Villager wird mit der Permission §6" + newPermission + " §averfügbar sein.");
        }

        @Override
        public String getShortDescription() {
            return "Setze Permission zu " + newPermission;
        }
    }

    private final class RemovePermissionAction implements Action {

        @Override
        public void execute(@NotNull Player plr, @NotNull Villager selected) {
            VillagerInfo villagerInfo = module.findVillagerInfo(selected);
            if (villagerInfo == null) {
                plr.sendMessage("§cDieser Villager ist bereits jedem zuägnglich.");
                return;
            }
            String oldPermission = villagerInfo.getPermission();
            if (oldPermission == null) {
                module.removeVillagerInfo(villagerInfo);
                plr.sendMessage("§cDieser Villager ist bereits jedem zuägnglich.");
                return;
            }
            villagerInfo.setPermission(null);
            module.save();
            plr.sendMessage("§aDieser Villager ist nun jedem zugänglich.");
            plr.sendMessage("§aVorher war er mit der Permission §6" + oldPermission + " §azugänglich.");
        }

        @Override
        public void sendActionInfo(@NotNull Player plr) {
            plr.sendMessage("§aDer nächste von dir angeklickte Villager wird für jeden zugänglich sein werden.");
        }

        @Override
        public String getShortDescription() {
            return "Entferne Permission";
        }
    }

    private class PermissionInfoAction implements Action {

        @Override
        public void execute(@NotNull Player plr, @NotNull Villager selected) {
            VillagerInfo villagerInfo = module.findVillagerInfo(selected);
            if (villagerInfo == null) {
                plr.sendMessage("§aFür diesen Villager wurde keine Permission gesetzt.");
                return;
            }
            if (villagerInfo.getPermission() == null) {
                module.removeVillagerInfo(villagerInfo);
                plr.sendMessage("§aFür diesen Villager wurde keine Permission gesetzt.");
                return;
            }
            plr.sendMessage("§aDieser Villager ist mit der Permission §6" + villagerInfo.getPermission() + " §6verfügbar.");
        }

        @Override
        public void sendActionInfo(@NotNull Player plr) {
            plr.sendMessage("§aDu erhälst Informationen über den nächsten angeklickten Villager.");
        }

        @Override
        public String getShortDescription() {
            return "Zeige Permission";
        }
    }
}
