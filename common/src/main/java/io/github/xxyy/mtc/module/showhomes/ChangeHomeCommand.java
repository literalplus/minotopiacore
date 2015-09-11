package io.github.xxyy.mtc.module.showhomes;

import io.github.xxyy.common.util.UUIDHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This command allows to manipulate homes whose owners are currently offline.
 *
 * @author Janmm14
 */
public class ChangeHomeCommand implements CommandExecutor {

    @NotNull
    private final ShowHomesModule module;

    public ChangeHomeCommand(@NotNull ShowHomesModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        //TODO find out if its laggy and make async in that case
        try {
            if (!sender.hasPermission("essentials.sethome.others") && !sender.hasPermission("essentials.delhome.others") &&
                    !sender.hasPermission("essentials.home.others")) {
                sender.sendMessage("§cDu bist nicht berechtigt, diesen Befehl zu benutzen!");
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
                return true;
            }
            final Player plr = (Player) sender;
            if (args.length < 3) {
                return showHelp(plr);
            }
            UUID uuid = UUIDHelper.getFromString(args[1]);
            if (uuid == null) {
                plr.sendMessage("§c\"§6" + args[1] + "\"§c ist keine valide UUID. Bindestriche vergessen?");
                return true;
            }

            if (uuid.version() != 3 && uuid.version() != 4) {
                plr.sendMessage("§cDie UUID ist keine valide Minecraft-UUID!");
            }
            EssentialsPlayerData user = module.getEssentialsPlayerDataManager().get(uuid);
            if (user == null) {
                plr.sendMessage("§cKonnte Homes nicht lesen!");
                return true;
            }
            Home home = user.getHomes().stream()
                    .filter(home_ -> home_.getName().equalsIgnoreCase(args[2]))
                    .findFirst().orElse(null);
            if (args[0].equalsIgnoreCase("delete")) {
                user.removeHome(plr, home.getName());
            } else if (args[0].equalsIgnoreCase("set")) {
                user.setHome(plr, home.getName());
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                if (!plr.hasPermission("essentials.home.others")) {
                    plr.sendMessage("§cDu hast keine Berechtigung, dich zu Homes anderer Spieler zu teleportieren!");
                    return true;
                }
                plr.teleport(home.getLocation());
                plr.sendMessage(
                        "§aDu hast dich zu " + user.getLastName() + "'s Home " + home.getName() + " teleportiert.");
            } else {
                showHelp(plr);
            }
        } catch (Exception ex) {
            module.handleException(ex);
        }
        return true;
    }

    private boolean showHelp(Player plr) {
        plr.sendMessage("§6ShowHome's ChangeHome:");
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/homeutil"))
                .append("/homeutil").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Zeigt diese Hilfe").color(ChatColor.GOLD)
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/homeutil delete "))
                .append("/homeutil delete <UUID> <Home-Name>").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Löscht das Home <Home-Name> von dem Spieler mit der UUID <UUID>").color(ChatColor.GOLD)
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/homeutil tp "))
                .append("/homeutil tp <UUID> <Home-Name>")
                .color(ChatColor.RED)
                .append(" - ")
                .reset()
                .color(ChatColor.GRAY)
                .append("Teleportiert dich zu dem Home <Home-Name> von dem Spieler mit der UUID <UUID>")
                .color(ChatColor.GOLD)
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/homeutil set "))
                .append("/homeutil set <UUID> <Home-Name>")
                .color(ChatColor.RED)
                .append(" - ")
                .reset()
                .color(ChatColor.GRAY)
                .append("Setzt das Home <Home-Name> von dem Spieler mit der UUID <UUID> zu deiner aktuellen Position.")
                .color(ChatColor.GOLD)
                .create());
        return true;
    }
}
