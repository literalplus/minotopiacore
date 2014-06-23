package io.github.xxyy.minotopiacore.misc.cmd;

import io.github.xxyy.common.cmd.XYCCommandExecutor;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.Const;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.LaterMessageHelper;
import io.github.xxyy.minotopiacore.misc.CacheHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public abstract class MTCCommandExecutor extends XYCCommandExecutor implements CacheHelper.Cache {

    public MTCCommandExecutor() {
        CacheHelper.registerCache(this);
    }

    /**
     * Please DO NOT OVERRIDE.
     */
    @Override
    public boolean preCatch(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (LaterMessageHelper.hasMessages(senderName)) {
            LaterMessageHelper.sendMessages(sender);
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("credits")) {
            CommandHelper.msg("§9▒█▀▄▀█  ▀▀█▀▀  ▒█▀▀█ §eMinoTopiaCore.\n" +
                              "§9▒█▒█▒█  ░▒█░░  ▒█░░░ §e" + Const.versionString + "\n" +
                              "§9▒█░░▒█  ░▒█░░  ▒█▄▄█ §eby xxyy/Literallie\n" +
                              "§9### §e" + MTC.PLUGIN_VERSION.toString() + " §9###", sender);
        }
        return true;
    }

}
