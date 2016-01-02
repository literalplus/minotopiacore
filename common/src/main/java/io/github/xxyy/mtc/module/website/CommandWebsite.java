/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.website;

import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.common.util.math.NumberHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles /website command, which can be used to enable and disable the player's website account as well as change the
 * password. This is eventually going to be replaced with an xLogin authentication system.?
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11/10/14
 */
public class CommandWebsite implements CommandExecutor {
    private final WebsiteModule module;
    //Player UUIDs to confirmation codes used for /hp (de)?activate
    private final Map<UUID, String> playerCodes = new HashMap<>();

    public CommandWebsite(WebsiteModule module) {
        this.module = module;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player plr = (Player) sender;

        if (args.length > 0 && !args[0].equalsIgnoreCase("help")) {
            switch (args[0].toLowerCase()) {
                case "aktivieren":
                case "activate":
                case "activ8":
                case "aktivieren!": //For the special snowflakes who don't get that they should not include the exclamation mark when it says "use /hp aktivieren!"
                    if (hasConfirmedActivate(plr, args)) { //also prints a red warning message in bold ok
                        setAccountStatus(plr, "aktiviert! Du kannst dich jetzt einloggen!", true, args); //activated
                    }
                    return true;
                case "deaktivieren":
                case "deactivate":
                    setAccountStatus(plr, "§cde§aaktiviert!", false, args); //deactivated
                    return true;
                case "pw":
                    return changePassword(plr, label, args);
                default:
                    plr.sendMessage(String.format("§cUnbekannte Aktion: %s!", args[0])); //Unknown action: %s!
                    break; //Fall through to potential help listing
            }
        }

        plr.sendMessage("§c/<command> [aktivieren|pw|deaktivieren]");
        return true;
    }

    private boolean changePassword(Player plr, String label, String[] args) {
        if (!module.isPasswordChangeEnabled()) {
            plr.sendMessage("§cDas Ändern von Passwörtern ist momentan aus Sicherheitsgründen deaktiviert. " + //Changing of passwords is currently disabled due to
                    "Bitte kontaktiere den Support, falls du deines vergessen haben solltest.");  //security concerns. Contact support if you forgot yours.
            return true;
        }

        if (args.length < 2) {
            plr.sendMessage("§c/" + label + " pw [Neues Passwort]");
            return true;
        }

        int rowsAffected;
        try {
            rowsAffected = module.getPlugin().getSql().safelyExecuteUpdate("UPDATE " + WebsiteModule.WEBSITE_USER_TABLE_NAME +
                            " SET pass=? WHERE name=?", //TODO: UUIDs
                    NumberHelper.bytesToHex(MessageDigest.getInstance("MD5").digest(args[1].getBytes())), //TODO: Why the fuck does the website hash stuff like this? Even for a legacy pile of crap this is unacceptable TODO: Can we use PasswordHelper here?
                    plr.getName());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("The universe has collapsed, sorry."); //Cannot happen
        }

        if (rowsAffected == 1) {
            plr.sendMessage("§aDein Passwort wurde geändert!"); //Your password has been changed!
        } else if (rowsAffected > 1) {
            plr.sendMessage("§cFür deinen Namen gibt es mehrere Accounts! Kontaktiere bitte den Support, danke!"); //Multiple accounts for your name, contact support.
        } else { //0 or less
            plr.sendMessage("§cDu hast noch keinen Account registriert! Bitte fülle dieses Formular aus: http://www.minotopia.me/?p=2"); //You don't have any account, please fill this to create one
        }
        return true;
    }

    private boolean hasConfirmedActivate(Player plr, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase(playerCodes.get(plr.getUniqueId()))) {
            String confirmationCode = StringHelper.alphanumericString(8, false).toUpperCase();
            confirmationCode = String.format("%s-%s", confirmationCode.substring(0, 4), confirmationCode.substring(4, 8));
            playerCodes.put(plr.getUniqueId(), confirmationCode);

            plr.spigot().sendMessage(new ComponentBuilder("Achtung, du bist gerade in Begriff, deinen " +
                    "Homepage-Account zu aktivieren!").color(ChatColor.RED).bold(true).create()); //You're about to activate your website account!
            plr.spigot().sendMessage(new ComponentBuilder("Solltest du nicht gerade einen Account auf " +
                    "unserer ").color(ChatColor.YELLOW).bold(true) //If you didn't create one at our site,
                    .append("Webseite (https://minotopia.me/)").underlined(true)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.minotopia.me/")) //vv you're
                    .append(" registriert haben, bist du möglicherweise Opfer eines Betrugs!").underlined(false).create());
            plr.spigot().sendMessage(new ComponentBuilder("Ansonsten klicke hier").color(ChatColor.GOLD).underlined(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hp aktivieren " + confirmationCode))
                    .append(" oder tippe ").underlined(false).color(ChatColor.YELLOW)
                    .append("/hp aktivieren " + confirmationCode).color(ChatColor.GOLD).create());
            return false;
        }
        return true;
    }

    private void setAccountStatus(Player plr, String newStateDescription, boolean enabled, String[] args) {
        int rowsAffected = module.getPlugin().getSql().safelyExecuteUpdate("UPDATE " + WebsiteModule.WEBSITE_USER_TABLE_NAME +
                " SET status=? WHERE name=?", enabled ? 1 : 0, plr.getName()); //TODO: UUIDs

        if (rowsAffected == 1) {
            plr.sendMessage("§aDein Homepageaccount wurde " + newStateDescription); //Your account has been activated, you can log in now!
        } else if (rowsAffected > 1) {
            plr.sendMessage("§cFür deinen Namen gibt es mehrere Accounts! Kontaktiere bitte den Support, danke!"); //Multiple accounts for your name, contact support.
        } else { //0 or less
            plr.sendMessage("§cDu hast noch keinen Account registriert! Bitte fülle dieses Formular aus: http://www.minotopia.me/?p=2"); //You don't have any account, please fill this to create one
        }
    }
}
