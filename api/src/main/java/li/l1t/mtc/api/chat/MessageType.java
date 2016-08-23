/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.chat;

import org.bukkit.command.CommandSender;

/**
 * Enumeration of standardised message types as outlined <a href="https://wiki.minotopia.me/w/Chatformat">in
 * the wiki</a>, providing easy format methods for each.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public enum MessageType {
    WARNING("§e§lAchtung: §e%s"),
    USER_ERROR("§c§lFehler: §c%s"),
    INTERNAL_ERROR("§4§lInterner Fehler: §c%s"),
    RESULT_LINE(String.format("§e§l%s §6", ChatConstants.LEFT_ARROW)),
    RESULT_LINE_SUCCESS(String.format("§e§l%s §a", ChatConstants.LEFT_ARROW)),
    LIST_HEADER(String.format("§e§l%s §e", ChatConstants.LEFT_ARROW)),
    LIST_ITEM(String.format("§e§l-%s §6", ChatConstants.LEFT_ARROW)),
    HEADER("§x»»» §p%s §s«««"),
    BROADCAST("§x%s");

    private final String template;

    MessageType(String baseFormat) {
        this.template = ChatConstants.convertCustomColorCodes(baseFormat);
    }

    /**
     * Formats a message of this type according to the type's template.
     *
     * @param message   the message to format, possibly {@link ChatConstants#convertCustomColorCodes(String)
     *                  with custom color codes}, with arguments represented like in {@link
     *                  String#format(String, Object...)}
     * @param arguments the arguments for the message
     * @return the formatted message
     */
    public String format(String message, Object... arguments) {
        String fullPattern = ChatConstants.convertCustomColorCodes(
                String.format(template, message)
        );
        return String.format(fullPattern, arguments);
    }

    /**
     * Formats a message of this type and sends the formatted message to a command sender.
     *
     * @param sender    the receiver of the message
     * @param message   the message to format
     * @param arguments the arguments for the message
     * @see #format(String, Object...) for details on formatting
     */
    public void sendTo(CommandSender sender, String message, Object... arguments) {
        sender.sendMessage(format(message, arguments));
    }

    public String getTemplate() {
        return template;
    }
}
