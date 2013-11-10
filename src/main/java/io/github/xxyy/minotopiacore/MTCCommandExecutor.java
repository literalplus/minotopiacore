package io.github.xxyy.minotopiacore;

import io.github.xxyy.common.cmd.XYCCommandExecutor;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.helper.LaterMessageHelper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public abstract class MTCCommandExecutor extends XYCCommandExecutor {
    
//    /**
//     * Called to catch commands.
//     * Some things have already been done!
//     * @see CommandExecutor#onCommand(CommandSender, Command, String, String[])
//     * @param senderName Pre-fetched to save dat line of code :)
//     * @return Success
//     * @author xxyy98<xxyy98@gmail.com
//     */
//    public abstract boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args);
    
    /**
     * Please DO NOT OVERRIDE.
     */
    @Override 
    public boolean preCatch(CommandSender sender, String senderName, Command cmd, String label, String[] args){
        if(LaterMessageHelper.hasMessages(senderName)){
            LaterMessageHelper.sendMessages(sender);
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("credits")){
            CommandHelper.msg("§9▒█▀▄▀█  ▀▀█▀▀  ▒█▀▀█ §eMinoTopiaCore.\n" + 
                              "§9▒█▒█▒█  ░▒█░░  ▒█░░░ §e"+Const.versionString+"\n" + 
                              "§9▒█░░▒█  ░▒█░░  ▒█▄▄█ §e"+MTC.versionName+"\n" +
                              "§9### §eMinoTopiaCore...xxyy98 §9###",sender);
        }
        return true;
    }
    
}
