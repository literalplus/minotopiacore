/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.command;

import li.l1t.common.XycConstants;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.MTC;
import li.l1t.mtc.api.command.CommandBehaviour;
import li.l1t.mtc.helper.LaterMessageHelper;

/**
 * Static utility class providing MTC default behaviours.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-30
 */
public class MTCBehaviours {
    private MTCBehaviours() {

    }

    /**
     * Creates a behaviour that displays a XYC credit and version message when called with the 'xyc'
     * argument.
     *
     * @return the behaviour
     */
    public static CommandBehaviour xycCrediting() {
        return (sender, label, cmd, args) -> {
            if (args.length == 1 && args[0].equalsIgnoreCase("xyc")) {
                CommandHelper.msg("§9▀▄▒▄▀ ▒█░░▒█ ▒█▀▀█ §eXyCommon Library.\n" +
                        "§9░▒█░░ ▒█▄▄▄█ ▒█░░░ §eby xxyy (Philipp Nowak)\n" +
                        "§9▄▀▒▀▄ ░░▒█░░ ▒█▄▄█ §ehttp://xxyy.github.io/\n" +
                        "§9### §e" + XycConstants.VERSION.toString() + " §9###", sender);
                return false;
            }
            return true;
        };
    }

    /**
     * Creates a behaviour that displays a MTC credit and version message when called with the 'mtc'
     * argument.
     *
     * @return the behaviour
     */
    public static CommandBehaviour mtcCrediting() {
        return (sender, label, cmd, args) -> {
            if (args.length >= 1 && args[0].equalsIgnoreCase("mtc")) {
                CommandHelper.msg("§9▒█▀▄▀█  ▀▀█▀▀  ▒█▀▀█ §eMinoTopiaCore.\n" +
                        "§9▒█▒█▒█  ░▒█░░  ▒█░░░ §e\n" +
                        "§9▒█░░▒█  ░▒█░░  ▒█▄▄█ §eby xxyy/Literallie\n" +
                        "§9### §e" + MTC.PLUGIN_VERSION.toString() + " §9###", sender);
                return false;
            }
            return true;
        };
    }

    /**
     * Creates a behaviour that checks for messages from {@link LaterMessageHelper} on every
     * execution and prints them.
     *
     * @return the behaviour
     */
    public static CommandBehaviour messagesChecking() {
        return (sender, label, cmd, args) -> {
            if (LaterMessageHelper.hasMessages(sender.getName())) {
                LaterMessageHelper.sendMessages(sender);
            }
            return true;
        };
    }
}
