/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.base;

import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.api.account.MutableAccount;
import li.l1t.lanatus.api.exception.AccountConflictException;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.command.BukkitExecutionExecutor;
import li.l1t.mtc.hook.XLoginHook;

/**
 * Handles the /lagive command, which credits melons to players.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-27
 */
public class LanatusGiveCommand extends BukkitExecutionExecutor {
    private final LanatusClient client;
    private final XLoginHook xLogin;

    public LanatusGiveCommand(LanatusClient client, XLoginHook xLogin) {
        this.client = client;
        this.xLogin = xLogin;
    }

    @Override
    public boolean execute(CommandExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            XLoginHook.Profile profile = argumentProfile(exec.arg(0), exec);
            int melonAmount = exec.intArg(1);
            handleGiveMelons(exec, profile, melonAmount);
        } else {
            showUsage(exec);
        }
        return true;
    }

    private XLoginHook.Profile argumentProfile(String input, CommandExecution exec) {
        return xLogin.findSingleMatchingProfileOrFail(
                input, exec.sender(), profile -> String.format(
                        "/lagive %s %s", profile.getUniqueId(), exec.findArg(1).orElse("")
                )
        );
    }

    private void handleGiveMelons(CommandExecution exec, XLoginHook.Profile profile, int amount) {
        respondOperationStart(exec, profile, amount);
        MutableAccount account = client.accounts().findMutable(profile.getUniqueId());
        checkTransactionPossible(amount, account);
        tryModifyMelonsCount(amount, account);
        respondSuccess(exec, profile, account);
    }

    private void respondOperationStart(CommandExecution exec, XLoginHook.Profile profile, int amount) {
        exec.respond(MessageType.RESULT_LINE, "Versuche, %s %d Melonen zu geben...",
                profile.getName(), amount);
    }

    private void checkTransactionPossible(int amount, MutableAccount account) {
        int expectedFinalCount = account.getMelonsCount() + amount;
        if (expectedFinalCount < 0) {
            throw new UserException(
                    "Nach dieser Aktion hätte %s negative Melonen! (%d - %d = %d)",
                    account.getMelonsCount(), amount * -1, expectedFinalCount
            );
        }
    }

    private void tryModifyMelonsCount(int amount, MutableAccount account) {
        account.modifyMelonsCount(amount);
        try {
            client.accounts().save(account);
        } catch (AccountConflictException e) {
            throw new InternalException("Der Account wurde anderswo geändert. Bitte versuche es erneut.", e);
        }
    }

    private void respondSuccess(CommandExecution exec, XLoginHook.Profile profile, MutableAccount account) {
        AccountSnapshot remoteAccount = client.accounts().find(profile.getUniqueId());
        exec.respond(MessageType.RESULT_LINE_SUCCESS, "Aktion erfolgreich! %s hat jetzt %d Melonen.",
                profile.getName(), remoteAccount.getMelonsCount());
        checkChangedConcurrently(exec, account, remoteAccount);
    }

    private void checkChangedConcurrently(CommandExecution exec, MutableAccount account, AccountSnapshot remoteAccount) {
        if (account.getMelonsCount() != remoteAccount.getMelonsCount()) {
            exec.respond(MessageType.WARNING, "Der Account wurde währenddessen woanders geändert.");
        }
    }

    private void showUsage(CommandExecution exec) {
        exec.respondUsage("", "<Spieler|UUID> <Anzahl>", "Schenkt einem Spieler Melonen.");
    }
}
