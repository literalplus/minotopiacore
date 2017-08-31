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

package li.l1t.mtc.module.lanatus.base;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.api.account.LanatusAccount;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.module.scoreboard.CommonScoreboardProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handles the /lagive command, which credits melons to players.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-27
 */
class LanatusGiveCommand extends MTCExecutionExecutor {
    private final LanatusClient client;
    private final XLoginHook xLogin;
    private final Plugin plugin;
    @InjectMe(required = false)
    private CommonScoreboardProvider scoreboard;

    @InjectMe
    public LanatusGiveCommand(MTCLanatusClient client, XLoginHook xLogin, MTCPlugin plugin) {
        this.client = client;
        this.xLogin = xLogin;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            XLoginHook.Profile profile = argumentProfile(exec.arg(0), exec);
            int melonAmount = exec.intArg(1);
            handleGiveMelons(exec, profile, melonAmount);
        } else {
            showUsage(exec);
        }
        return true;
    }

    private XLoginHook.Profile argumentProfile(String input, BukkitExecution exec) {
        return xLogin.findSingleMatchingProfileOrFail(
                input, exec.sender(), profile -> String.format(
                        "/lagive %s %s", profile.getUniqueId(), exec.findArg(1).orElse("")
                )
        );
    }

    private void handleGiveMelons(BukkitExecution exec, XLoginHook.Profile profile, int amount) {
        respondOperationStart(exec, profile, amount);
        LanatusAccount account = client.accounts().findOrDefault(profile.getUniqueId());
        checkTransactionPossible(amount, account);
        tryModifyMelonsCount(exec, account, amount);
        respondSuccess(exec, profile);
        updateTargetPlayerScoreboard(profile);
    }

    private void updateTargetPlayerScoreboard(XLoginHook.Profile profile) {
        if(scoreboard == null) {
            return;
        }
        Player targetPlayer = plugin.getServer().getPlayer(profile.getUniqueId());
        if(targetPlayer != null) {
            scoreboard.updateScoreboardFor(targetPlayer);
        }
    }

    private void respondOperationStart(BukkitExecution exec, XLoginHook.Profile profile, int amount) {
        exec.respond(MessageType.RESULT_LINE, "Versuche, %s %d Melonen zu geben...",
                profile.getName(), amount);
    }

    private void checkTransactionPossible(int amount, LanatusAccount account) {
        int expectedFinalCount = account.getMelonsCount() + amount;
        if (expectedFinalCount < 0) {
            throw new UserException(
                    "Nach dieser Aktion hätte diese Person negative Melonen! (%d - %d = %d)",
                    account.getMelonsCount(), amount * -1, expectedFinalCount
            );
        }
    }

    private void tryModifyMelonsCount(BukkitExecution exec, LanatusAccount account, int amount) {
        client.creditMelons(account.getPlayerId())
                .withComment(String.format("/lagive von %s (%s)", exec.senderName(), exec.senderId()))
                .withMelonsCount(amount)
                .build();
    }

    private void respondSuccess(BukkitExecution exec, XLoginHook.Profile profile) {
        AccountSnapshot remoteAccount = client.accounts().findOrDefault(profile.getUniqueId());
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Aktion erfolgreich! %s hat jetzt %d Melonen.",
                profile.getName(), remoteAccount.getMelonsCount());
    }

    private void showUsage(BukkitExecution exec) {
        exec.respondUsage("", "<Spieler|UUID> <Anzahl>", "Schenkt einem Spieler Melonen.");
    }
}
