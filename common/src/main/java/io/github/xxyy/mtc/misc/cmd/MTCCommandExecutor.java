/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc.cmd;

import io.github.xxyy.common.cmd.XYCCommandExecutor;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.LaterMessageHelper;
import io.github.xxyy.mtc.misc.CacheHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public abstract class MTCCommandExecutor extends XYCCommandExecutor implements CacheHelper.Cache {

    public MTCCommandExecutor() {
        CacheHelper.registerCache(this);
    }

    @Override
    public final boolean preCatch(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (LaterMessageHelper.hasMessages(senderName)) {
            LaterMessageHelper.sendMessages(sender);
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("mtc")) {
            CommandHelper.msg("§9▒█▀▄▀█  ▀▀█▀▀  ▒█▀▀█ §eMinoTopiaCore.\n" +
                              "§9▒█▒█▒█  ░▒█░░  ▒█░░░ §e\n" +
                              "§9▒█░░▒█  ░▒█░░  ▒█▄▄█ §eby xxyy/Literallie\n" +
                              "§9### §e" + MTC.PLUGIN_VERSION.toString() + " §9###", sender);
        }
        return true;
    }

}
