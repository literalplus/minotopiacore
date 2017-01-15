/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.chat;

import li.l1t.common.chat.FormattedResponse;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

/**
 * Enumeration of standardised message types as outlined <a href="https://wiki.minotopia.me/w/Chatformat">in
 * the wiki</a>, providing easy format methods for each.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-08-23
 */
public enum MessageType implements FormattedResponse {
    WARNING("§e§lAchtung: §e%s"),
    USER_ERROR("§c§lFehler: §c%s"),
    INTERNAL_ERROR("§4§lInterner Fehler: §c%s"),
    RESULT_LINE(String.format("§e§l%s §6%%s", ChatConstants.LEFT_ARROW)),
    RESULT_LINE_SUCCESS(String.format("§e§l%s §a%%s", ChatConstants.LEFT_ARROW)),
    LIST_HEADER(String.format("§e§l%s §e%%s", ChatConstants.LEFT_ARROW)),
    LIST_ITEM(String.format("§e§l-%s §6%%s", ChatConstants.LEFT_ARROW)),
    HEADER("§x§p»»» %s §p«««"),
    BROADCAST("§x%s");

    private final String template;

    MessageType(String baseFormat) {
        this.template = ChatConstants.convertCustomColorCodes(baseFormat);
    }

    @Override
    public String format(String message, Object... arguments) {
        String fullPattern = ChatConstants.convertCustomColorCodes(
                String.format(template, message)
        );
        return String.format(fullPattern, arguments);
    }

    @Override
    public void sendTo(CommandSender sender, String message, Object... arguments) {
        sender.sendMessage(format(message, arguments));
    }

    @Override
    public void broadcast(Server server, String message, Object... arguments) {
        String finalMessage = format(message, arguments);
        server.getOnlinePlayers().forEach(player -> player.sendMessage(finalMessage));
    }

    public String getTemplate() {
        return template;
    }
}
