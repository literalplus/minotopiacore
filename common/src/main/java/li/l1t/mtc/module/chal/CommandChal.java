/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.chal;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.UUIDHelper;
import li.l1t.mtc.hook.XLoginHook;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.UUID;

/**
 * Provides a text-based admin front-end to the Chal module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.11.14
 */
class CommandChal implements CommandExecutor {
    private final ChalModule module;

    CommandChal(ChalModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "list":
                    module.getLocations().stream().forEach(cl -> ComponentSender.sendTo(
                            new XyComponentBuilder(" -> ", ChatColor.GOLD)
                                    .append(cl.getDate().toReadable(), ChatColor.YELLOW)
                                    .append(" @ " + cl.prettyPrint() + " ", ChatColor.GOLD)
                                    .append("[tp]", ChatColor.GREEN, ChatColor.UNDERLINE)
                                    .hintedCommand(cl.toTpCommand(sender.getName())), sender
                    ));
                    if (module.getLocations().isEmpty()) {
                        sender.sendMessage("§cKeine Kisten gespeichert!");
                    }
                    return true;
                case "delete":
                case "remove":
                    sender.sendMessage("§aZerstöre einfach die jeweilige Kiste! :)");
                    return true;
                case "chest":
                    if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                        return true;
                    } else if (args.length < 2) {
                        sender.sendMessage("§a/chal chest [Monat-Tag]");
                        return true;
                    }

                    ChalDate date;
                    try {
                        date = ChalDate.deserialize(args[1]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage("§cFalsches Datumsformat: " + e.getMessage());
                        return true;
                    }

                    module.startSelection((Player) sender, date);
                    sender.sendMessage("§eAuswahl gestartet für " + date.toReadable() + ". Klicke jetzt die Kiste deiner Wahl an.");
                    return true;
                case "reset":
                    if (args.length < 2) {
                        sender.sendMessage("§a/chal reset [Name oder UUID]");
                        return true;
                    }

                    UUID uuid;
                    if (UUIDHelper.isValidUUID(args[1])) {
                        uuid = UUID.fromString(args[1]);
                    } else {
                        List<XLoginHook.Profile> profiles = module.getPlugin().getXLoginHook().getProfiles(args[1]);
                        if (profiles.size() == 1) {
                            uuid = profiles.get(0).getUniqueId();
                        } else if (profiles.isEmpty()) {
                            sender.sendMessage("§aSo einen Spieler kennen wir nicht!");
                            return true;
                        } else {
                            sender.sendMessage("§aMehrere Spieler gefunden:");
                            profiles.forEach(profile -> ComponentSender.sendTo(
                                    new XyComponentBuilder(profile.getName(), ChatColor.GOLD)
                                            .append(" => ", ChatColor.YELLOW)
                                            .append(profile.getUniqueId().toString() + " (", ChatColor.GOLD)
                                            .append(profile.isPremium() ? "Premium" : "Cracked", ChatColor.YELLOW)
                                            .append(") ", ChatColor.GOLD)
                                            .append("[*]", ChatColor.BLUE)
                                            .hintedCommand("/chal reset " + profile.getUniqueId().toString()), sender
                            ));
                            return true;
                        }
                    }

                    module.resetOpened(uuid);
                    sender.sendMessage("§aGeöffnete Kisten entfernt.");
                    return true;
                case "edit":
                    if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                        return true;
                    }

                    Player plr = ((Player) sender);
                    if (plr.hasMetadata(ChalModule.METADATA_KEY)) {
                        plr.removeMetadata(ChalModule.METADATA_KEY, module.getPlugin());
                        plr.sendMessage("§eEdit Mode deaktiviert");
                    } else {
                        plr.setMetadata(ChalModule.METADATA_KEY, new FixedMetadataValue(module.getPlugin(), true));
                        plr.sendMessage("§eEdit Mode aktiviert (Du kannst Kisten jetzt bearbeiten)");
                    }
                    return true;
            }
        }

        sender.sendMessage("§9/chal list §2Listet alle Kisten auf");
        sender.sendMessage("§9/chal chest [Monat-Tag] §2Setzt eine Kiste");
        sender.sendMessage("§9/chal reset [Name|UUID] §2Setzt die geöffneten Kisten für einen Spieler zurück");
        sender.sendMessage("§9/chal edit §2Erlaubt, Chal-Kisten zu ändern");
        sender.sendMessage("§eMehr Info: https://www.minotopia.me/wiki/index.php/Chal");
        return true;
    }
}
