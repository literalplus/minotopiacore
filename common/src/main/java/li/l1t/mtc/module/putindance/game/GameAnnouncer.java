/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game;

import li.l1t.mtc.module.putindance.PutinDanceModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Handles announcing general events related to a game of PutinDance.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class GameAnnouncer {
    private final String[] joinLines = new String[]{
            "Willkommen bei PutinDance!",
            "Du bist Geheimagent/in und im Geheimauftrag von Putin.",
            "Um deine Reaktionsfähigkeit und Loyalität zu testen, wirst",
            "du einem Test unterzogen. Alle paar Sekunden",
            "konsumieren die Kapitalisten alle Blöcke einer Farbe.",
            "Wer zuletzt noch auf einem Wollblock steht, hat gewonnen!",
            "Putin gibt dir immer wieder Tipps."
    };

    public void announceGameJoin(Player player) {
        sendAll(player, joinLines);
    }

    public void announceGameOpen(Collection<? extends CommandSender> receivers) {
        sendToAllPrefixed(receivers, "Wir spielen PutinDance! §6/pd join");
    }

    public void announceGameStart(Collection<? extends CommandSender> receivers) {
        sendToAllPrefixed(receivers, "Das Spiel beginnt jetzt!");
    }

    public void announceGameAbort(Collection<? extends CommandSender> receivers) {
        sendToAllPrefixed(receivers, "Das Spiel wurde beendet!");
    }

    private void sendAll(CommandSender receiver, String[] messages) {
        for (String line : messages) {
            sendMessagePrefixed(receiver, line);
        }
    }

    private void sendMessagePrefixed(CommandSender receiver, String message) {
        receiver.sendMessage(PutinDanceModule.CHAT_PREFIX + message);
    }

    private void sendToAllPrefixed(Collection<? extends CommandSender> receivers, String message) {
        receivers.forEach(receiver -> sendMessagePrefixed(receiver, message));
    }
}
