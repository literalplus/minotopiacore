/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Executes the /stats command, which allows users to view their own stats and administrators to manage stats.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class CommandStats implements CommandExecutor {
    private final PvPStatsModule module;

    public CommandStats(PvPStatsModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //FIXME
        return true;
    }
}
