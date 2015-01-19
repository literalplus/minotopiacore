/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Provides a text-based admin front-end to the Chal module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
//TODO
class CommandShop implements CommandExecutor {
    private final ShopModule module;


    public CommandShop(ShopModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            //FIXME
        }


        sender.sendMessage("§9/chal list §2Listet alle Kisten auf");
        sender.sendMessage("§9/chal chest [Monat-Tag] §2Setzt eine Kiste");
        sender.sendMessage("§9/chal reset [Name|UUID] §2Setzt die geöffneten Kisten für einen Spieler zurück");
        sender.sendMessage("§9/chal edit §2Erlaubt, Chal-Kisten zu ändern");
        sender.sendMessage("§eMehr Info: https://www.minotopia.me/wiki/index.php/Chal");
        return true;
    }
}
