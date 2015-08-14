package io.github.xxyy.mtc.module.showhomes;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShowHomesCommand implements CommandExecutor {

    @NonNull
    private final ShowHomesModule module;

    public ShowHomesCommand(ShowHomesModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        try {
            if (!cmd.getName().equalsIgnoreCase("showhomes")) {
                module.getPlugin().getLogger().warning(
                        "[ShowHomes] The command '" + cmd.getName() + "', executed by " + cs +
                                " was forwarded to ShowHomes, but it is not handled by it: " +
                                '/' + alias + ' ' + Joiner.on(' ').join(args));
                cs.sendMessage("§cEs ist ein interner Fehler beim Verarbeiten dieses Kommandos aufgetreten.");
                return true;
            }
            if (!cs.hasPermission("showhomes.execute")) {
                cs.sendMessage("§cDu bist nicht berechtigt, dieses Kommnado zu benutzen!");
                return true;
            }

            if (!(cs instanceof Player)) {
                cs.sendMessage("§cNur Spieler können dieses Kommando verwenden!");
                return true;
            }
            final Player plr = (Player) cs;
            if (args.length == 0) {
                return showHologramsCmd(plr, module.getDefaultRadius());
            }
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                    return showHelp(plr);
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (plr.hasPermission("showhomes.reload")) {
                        module.reloadConfig();
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length >= 2) {
                        if (!args[1].equalsIgnoreCase("all")) {
                            showHelp(plr);
                        } else {
                            Collection<Home> shownHomes = module.getHolosByExecutingUser().values();
                            int holoAmount = shownHomes.size();
                            shownHomes.forEach(Home::hideHologram);
                            module.getHolosByExecutingUser().clear();
                            module.getTaskIdByUser().values()
                                    .forEach(module.getPlugin().getServer().getScheduler()::cancelTask);
                            module.getTaskIdByUser().clear();
                            plr.sendMessage("§aAlle Home-Hologramme wurden entfernt. (§b" + holoAmount + "§aStück)");
                            return true;
                        }
                    }
                    UUID uuid = plr.getUniqueId();
                    Collection<Home> shownHomes = module.getHolosByExecutingUser().get(uuid);
                    if (shownHomes == null) {
                        plr.sendMessage("§cDu hast keine Home-Hologramme heraufbeschworen.");
                        return true;
                    }
                    int holoAmount = shownHomes.size();
                    shownHomes.forEach(Home::hideHologram);
                    module.getHolosByExecutingUser().removeAll(uuid);
                    module.getPlugin().getServer().getScheduler().cancelTask(module.getTaskIdByUser().get(uuid));
                    module.getTaskIdByUser().remove(uuid);
                    plr.sendMessage("§aDeine Home-Hologramme wurden entfernt. (§b" + holoAmount + " §aStück)");
                    return true;
                }
                Integer radius = ShowHomesModule.parsePositiveInt(args[0]);
                if (radius == null) {
                    plr.sendMessage("§cDas ist keine (gültige) Zahl: " + args[0]);
                    return showHelp(plr);
                }
                if (radius > module.getMaxRadius()) {
                    plr.sendMessage("§6Radius gekappt auf §b" + module.getMaxRadius() + " §6Blöcke.");
                    radius = module.getMaxRadius();
                }
                if (radius < module.getMinRadius()) {
                    plr.sendMessage("§6Radius geringer als Mindestradius, daher erhöht auf §b" + module.getMinRadius() + " §6Blöcke.");
                    radius = module.getMinRadius();
                }
                return showHologramsCmd(plr, radius);
            }
            return true;
        } catch (Exception ex) {
            module.handleException(new Exception("onCommand", ex));
            return true;
        }
    }

    private boolean showHologramsCmd(Player plr, int radius) {
        plr.sendMessage("§6Lade Homes im Umkreis " + radius);
        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(), () -> {
            try {
                UUID uuid = plr.getUniqueId();

                //remove existing holograms
                if (module.getHolosByExecutingUser().containsKey(uuid)) {
                    Collection<Home> shownHomes = module.getHolosByExecutingUser().get(uuid);
                    int holoAmount = shownHomes.size();
                    shownHomes.forEach(Home::hideHologram);
                    module.getHolosByExecutingUser().removeAll(uuid);

                    module.getPlugin().getServer().getScheduler().cancelTask(module.getTaskIdByUser().get(uuid));
                    module.getTaskIdByUser().remove(uuid);

                    plr.sendMessage(
                            "§6Deine aktuellen Home-Hologramme (§b" + holoAmount + " §6Stück) wurden zuerst entfernt.");
                }

                //show holograms
                Set<Home> homes = showHolograms(plr, radius);
                if (homes == null) {
                    return;
                }
                module.getHolosByExecutingUser().putAll(uuid, homes);
                //add remove task
                BukkitTask task = module.getPlugin().getServer().getScheduler().runTaskLater(module.getPlugin(), () -> {
                    try {
                        module.getHolosByExecutingUser().removeAll(uuid)
                                .forEach(Home::hideHologram);
                        module.getTaskIdByUser().remove(uuid);
                        plr.sendMessage("§6Deine Home-Hologramme sind nun abgelaufen und wurden daher entfernt.");
                    } catch (Exception ex) {
                        module.handleException(new Exception("showHologramsCmd#asynctask#synctask(" + plr + ", " + radius + ")", ex));
                    }
                }, 20 * module.getHologramDuration());

                module.getTaskIdByUser().put(uuid, task.getTaskId());
            } catch (Exception ex) {
                module.handleException(new Exception("showHologramsCmd(" + plr + ", " + radius + ")", ex));
            }
        });

        return true;
    }

    public Set<Home> showHolograms(@NonNull Player plr, int radius) {
        try {
            Set<Home> homes = module.getHomesInRadius(plr, plr.getLocation(), radius).stream()
                    .filter(home -> !module.isSimilarHomeDisplayed(home))
                    .collect(Collectors.toSet());
            final int amountOfRepeats = (homes.size() % module.getHologramRateLimit()) + 1;

            for (int i = 0; i < amountOfRepeats; i++) {
                int iFinal = i;
                int jStart = i * amountOfRepeats;
                int max = Math.min(jStart + amountOfRepeats, homes.size());
                module.getPlugin().getServer().getScheduler().runTaskLater(module.getPlugin(), () -> { //TODO own class, turn into repeating task
                    try {
                        for (int j = jStart; j < max; j++) {
                            //Home home = homes.get(j);//TODO fix
                            ;
                            //home.showHologram(this);
                        }
                        if (iFinal == (amountOfRepeats - 1)) {
                            plr.sendMessage("§6Alle Homes dargestellt.");
                        }
                    } catch (Exception ex) {
                        module.handleException(ex);
                    }
                }, i);
            }
            plr.sendMessage("§6Stelle Homes dar...");

            return homes;
        } catch (Exception ex) {
            module.handleException(new Exception("showHolograms(" + plr + ", " + radius + ")", ex));
            return null;
        }
    }

    private boolean showHelp(Player plr) {
        plr.sendMessage("§6ShowHomes Hilfe:");
        plr.sendMessage("§6ShowHomes zeigt Homes aller User in deiner Umgebung als Hologramme an.");
        plr.sendMessage("Command Aliases: " + Joiner.on(", ").join(module.getPlugin().getCommand("showhomes").getAliases()));

        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes"))
                .append("/showhomes").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Zeigt die Homes im Standardumkreis 20").color(ChatColor.GOLD) //TODO default radius
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes 20")) //TODO default radius
                .append("/showhomes").color(ChatColor.RED)
                .append(" <radius>").color(ChatColor.BLUE)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Zeigt die Homes im gegebenen ").color(ChatColor.GOLD)
                .append("Umkreis").color(ChatColor.BLUE)
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes remove"))
                .append("/showhomes remove").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Entfernt von dir erstellte Home-Hologramme").color(ChatColor.GOLD)
                .create());
        plr.spigot().sendMessage(new ComponentBuilder("")
                .underlined(true)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/showhomes remove all"))
                .append("/showhomes remove all").color(ChatColor.RED)
                .append(" - ").reset().color(ChatColor.GRAY)
                .append("Entfernt alle erstellten Home-Hologramme").color(ChatColor.GOLD)
                .create());
        return true;
    }

}
