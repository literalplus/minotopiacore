/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.nub.tp;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.common.util.LocationHelper;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.hook.VaultHook;
import li.l1t.mtc.module.nub.api.ProtectionService;
import li.l1t.mtc.module.nub.service.SimpleProtectionService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Executes the /ntp command which allows players to teleport themselves to a random location for money.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class NubTpCommand extends MTCExecutionExecutor {
    private final NubTpConfig config;
    private final LocationSecurer locationSecurer;
    private final CoordinateSelector coordinateSelector;
    private final ProtectionService protectionService;
    private final VaultHook vaultHook;
    private final Plugin plugin;

    @InjectMe
    public NubTpCommand(NubTpConfig config, LocationSecurer locationSecurer, CoordinateSelector coordinateSelector,
                        SimpleProtectionService protectionService, VaultHook vaultHook, MTCPlugin plugin) {
        this.config = config;
        this.locationSecurer = locationSecurer;
        this.coordinateSelector = coordinateSelector;
        this.protectionService = protectionService;
        this.vaultHook = vaultHook;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            switch (exec.arg(0)) {
                case "tp":
                    return handleTeleport(exec);
            }
        }
        respondUsage(exec);
        return true;
    }

    private boolean handleTeleport(BukkitExecution exec) {
        exec.requireIsPlayer();
        requireCanAffordIfNotEligibleForFreeTeleport(exec);
        Location targetLocation = coordinateSelector.selectLocation(findTeleportWorldOrFail());
        exec.respond(MessageType.WARNING, "Du wirst in 2 Sekunden teleportiert. Bitte stillhalten...");
        teleportLaterIfHasntMoved(exec.player(), exec, targetLocation);
        return true;
    }

    private void requireCanAffordIfNotEligibleForFreeTeleport(BukkitExecution exec) {
        if (!isEligibleForFreeTeleport(exec.player())) {
            requireCanAfford(exec.player(), config.getVaultTeleportCost());
        }
    }

    private World findTeleportWorldOrFail() {
        World world = plugin.getServer().getWorld(config.getTeleportWorldName());
        if (world == null) {
            throw new InternalException("Unbekannte Teleportwelt, bitte kontaktiere den Support.");
        }
        return world;
    }

    private BukkitTask teleportLaterIfHasntMoved(Player player, BukkitExecution exec, Location targetLocation) {
        Location initialLocation = player.getLocation();
        return plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (LocationHelper.softEqual(initialLocation, player.getLocation())) {
                doPaymentAndTeleport(player, exec, targetLocation);
            } else {
                exec.respond(MessageType.USER_ERROR, "Du hast dich bewegt. Bitte versuche es erneut.");
            }
        }, 2L * 20L);
    }

    private void doPaymentAndTeleport(Player player, BukkitExecution exec, Location targetLocation) {
        if (processPayment(player, exec)) {
            locationSecurer.secureLocation(targetLocation, Material.GLASS);
            player.teleport(targetLocation);
            exec.respond(MessageType.RESULT_LINE_SUCCESS, "Du wurdest teleportiert. Tippe /sethome, " +
                    "um hier dein Home zu setzen.");
        }
    }

    private boolean processPayment(Player player, BukkitExecution exec) {
        if (isEligibleForFreeTeleport(player)) {
            exec.respond(MessageType.RESULT_LINE, "Da du noch durch N.u.b. geschützt bist, kannst du " +
                    "dich kostenlos teleportieren.");
        } else if (attemptWithdrawFromVault(player, config.getVaultTeleportCost())) {
            exec.respond(MessageType.RESULT_LINE, "%d MineCoins wurden von deinem Konto eingezogen.",
                    config.getVaultTeleportCost());
        } else {
            exec.respond(MessageType.USER_ERROR, "Fehler bei der Zahlung, bitte versuche es erneut.");
            return false;
        }
        return true;
    }

    private boolean attemptWithdrawFromVault(Player player, int amount) {
        return isEligibleForFreeTeleport(player) || !vaultHook.isEconomyHooked() ||
                vaultHook.withdrawPlayer(player, amount).transactionSuccess();
    }

    private boolean isEligibleForFreeTeleport(Player player) {
        return protectionService.hasProtection(player);
    }

    private void requireCanAfford(Player player, int amount) {
        if (vaultHook.isEconomyHooked() && !vaultHook.canAfford(player, amount)) {
            throw new UserException("Das kannst du dir nicht leisten. Du brauchst %d MineCoins.", amount);
        }
    }

    private void respondUsage(BukkitExecution exec) {
        exec.respond(MessageType.RESULT_LINE, "N.u.b. TP teleportiert dich zu zufälligen Koordinaten, " +
                "damit du dir eine halbwegs sichere Basis bauen kannst.");
        exec.respondUsage("tp", "", "Teleportiert dich.");
        if (exec.sender() instanceof Player && isEligibleForFreeTeleport(exec.player())) {
            exec.respond(MessageType.WARNING, "Solange du noch durch N.u.b. geschützt bist, kannst du " +
                            "dich kostenlos teleportieren. Danach kostet jede Teleportation %d MineCoins.",
                    config.getVaultTeleportCost());
        } else {
            exec.respond(MessageType.RESULT_LINE, "Jede Teleportation kostet dich %d MineCoins.",
                    config.getVaultTeleportCost());
        }
    }
}
