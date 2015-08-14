package io.github.xxyy.mtc.module.showhomes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class ChangeHomeCommand implements TabExecutor {

    @NonNull
    private final ShowHomesModule module;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        //TODO make async if laggy
        try {
            if (!cs.hasPermission("essentials.sethome.others") && !cs.hasPermission("essentials.delhome.others") &&
                    !cs.hasPermission("essentials.home.others")) {
                cs.sendMessage("§cDu bist nicht berechtigt, dieses Kommnado zu benutzen!");
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage("§cNur Spieler können dieses Kommando verwenden!");
                return true;
            }
            final Player plr = (Player) cs;
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
            EssentialsDataUser user = EssentialsDataUser.readHomes(uuid);
            if (user != null) {
                List<Home> filteredHomes = user.stream()
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
                }
                if (args[0].equalsIgnoreCase("set")) {
                    user.setHome(plr, home.getName(), null);
                    return true;
                }
                if (args[0].equalsIgnoreCase("tp")) {
                    if (plr.hasPermission("essentials.home.others")) {
                        Player aim = Bukkit.getPlayer(uuid);
                        if (aim != null && aim.isOnline()) {
                            plr.performCommand("/home " + aim.getName() + ':' + home.getName());
                            return true;
                        }
                        plr.teleport(home.getLocation());
                        plr.sendMessage(
                                "§aDu hast dich zu " + user.getLastName() + "'s Home " + home.getName() + " teleportiert.");
                    } else {
                        plr.sendMessage("§cDu hast keine Berechtigung, dich zu Homes anderer Spieler zu teleportieren!");
                    }
                    return true;
                }
            } else {
                plr.sendMessage("§cKonnte Homes nicht lesen!");
            }
            return true;
        } catch (Exception ex) {
            ShowHomesModule.handleException(new Exception("ChangeHomeCommand#onCommand", ex));
            return true;
        }
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
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        return null;
    }
}
