package io.github.xxyy.mtc.module.showhomes;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This command allowes to manipulate homes which owners are currently offline.
 *
 * @author Janmm14
 */
public class ChangeHomeCommand implements TabExecutor {

    @NonNull
    private final ShowHomesModule module;

    public ChangeHomeCommand(ShowHomesModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        //make async if laggy
        try {
            if (!sender.hasPermission("essentials.sethome.others") && !sender.hasPermission("essentials.delhome.others") &&
                    !sender.hasPermission("essentials.home.others")) {
                sender.sendMessage("§cDu bist nicht berechtigt, dieses Kommnado zu benutzen!");
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNur Spieler können dieses Kommando verwenden!");
                return true;
            }
            final Player plr = (Player) sender;
            if (args.length < 3) {
                return showHelp(plr);
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(args[1]);
            } catch (Exception ex) {
                plr.sendMessage("§cDie UUID ist nicht valid! Vermutlich hast du eine UUID ohne §b- §ceingegeben.");
                plr.sendMessage("§cFehlermeldung: §6" + ex.getMessage());
                return true;
            }
            if (uuid.version() != 3 && uuid.version() != 4) {
                plr.sendMessage("§cDie UUID ist keine valide Minecraft-UUID!");
            }
            EssentialsDataUser user = EssentialsDataUser.fromFile(module, uuid);
            if (user == null) {
                plr.sendMessage("§cKonnte Homes nicht lesen!");
            } else {
                List<Home> filteredHomes = user.getHomes().stream()
                        .filter(home -> home.getName().equalsIgnoreCase(args[2]))
                        .collect(Collectors.toList());
                if (filteredHomes.size() > 1) {
                    module.getPlugin().getLogger()
                            .warning("[ShowHomes] The user " + user.getLastName() + " (UUID: " + user.getUuid() +
                                    ") has multiple homes with the name " + args[2] + ". Please check what's wrong where!");
                    plr.sendMessage("§cDer User " + user.getLastName() + " hat mehrere Homes mit dem Namen " + args[2] +
                            ". Dies ist komsich, melde das bitte einem Owner.");
                    return true;
                }
                Home home = filteredHomes.get(0);
                if (args[0].equalsIgnoreCase("delete")) {
                    user.removeHome(plr, home.getName());
                    return true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    user.setHome(plr, home.getName(), null);
                    return true;
                } else if (args[0].equalsIgnoreCase("tp")) {
                    if (!plr.hasPermission("essentials.home.others")) {
                        plr.sendMessage("§cDu hast keine Berechtigung, dich zu Homes anderer Spieler zu teleportieren!");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(uuid);
                    if (target != null && target.isOnline()) {
                        plr.performCommand("home " + target.getName() + ':' + home.getName());
                        return true;
                    }
                    plr.teleport(home.getLocation());
                    plr.sendMessage(
                            "§aDu hast dich zu " + user.getLastName() + "'s Home " + home.getName() + " teleportiert.");
                    return true;
                } else {
                    showHelp(plr);
                }
            }
        } catch (Exception ex) {
            module.handleException(new Exception("ChangeHomeCommand#onCommand", ex));
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return null; //maybe sometimes later
    }
}
