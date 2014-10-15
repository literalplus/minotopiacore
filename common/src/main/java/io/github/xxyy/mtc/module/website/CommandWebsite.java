package io.github.xxyy.mtc.module.website;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.xxyy.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Handles /website command, which can be used to enable and disable the player's website account as well as change the
 * password. This is eventually going to be replaced with an xLogin authentication system.?
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11/10/14
 */
public class CommandWebsite extends MTCPlayerOnlyCommandExecutor {
    private final WebsiteModule module;

    public CommandWebsite(WebsiteModule module) {
        this.module = module;
    }

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args) {
        if (args.length > 0 && !args[0].equalsIgnoreCase("help")) {
            switch (args[0].toLowerCase()) {
                case "aktivieren":
                case "activate":
                case "activ8":
                case "aktivieren!": //For the special snowflakes who don't get that they should not include the exclamation mark when it says "use /hp aktivieren!"
                    setAccountStatus(plr, "aktiviert! Du kannst dich jetzt einloggen!", true); //activated
                    return true;
                case "deaktivieren":
                case "deactivate":
                    setAccountStatus(plr, "§cde§aaktiviert!", false); //deactivated
                    return true;
                case "pw":
                    return changePassword(plr, label, args);
                default:
                    plr.sendMessage(String.format("§cUnbekannte Aktion: %s!", args[0])); //Unknown action: %s!
                    break; //Fall through to potential help listing
            }
        }

        //This is where a help listing would be if this command were more advanced.

        return false;
    }

    private boolean changePassword(Player plr, String label, String[] args) {
        if(!module.isPasswordChangeEnabled()) {
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
                    MessageDigest.getInstance("MD5").digest(args[1].getBytes()), //TODO: Why the fuck does the website hash stuff like this? Even for a legacy pile of crap this is unacceptable
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

    private void setAccountStatus(Player plr, String newStateDescription, boolean enabled) {
        int rowsAffected = module.getPlugin().getSql().safelyExecuteUpdate("UPDATE " + WebsiteModule.WEBSITE_USER_TABLE_NAME +
                " SET status=? WHERE name=?", enabled ? 1 : 0, plr.getName()); //TODO: UUIDs

        if (rowsAffected == 1) {
            plr.sendMessage("§aDein Homepageaccount wurde " + newStateDescription); //Your account has been activated, you can log in now!
        } else if (rowsAffected > 1) {
            plr.sendMessage("§cFür deinen Namen gibt es mehrere Accounts! Kontaktiere bitte den Support, danke!"); //Multipel accounts for your name, contact support.
        } else { //0 or less
            plr.sendMessage("§cDu hast noch keinen Account registriert! Bitte fülle dieses Formular aus: http://www.minotopia.me/?p=2"); //You don't have any account, please fill this to create one
        }
    }
}
