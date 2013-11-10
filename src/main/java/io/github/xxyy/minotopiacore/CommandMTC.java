package io.github.xxyy.minotopiacore;

import io.github.xxyy.common.HelpManager;
import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.common.localisation.XyLocalizable;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.bans.BanHelper;
import io.github.xxyy.minotopiacore.chat.MTCChatHelper;
import io.github.xxyy.minotopiacore.clan.ClanHelper;
import io.github.xxyy.minotopiacore.cron.RunnableCronjob5Minutes;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class CommandMTC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!MTCHelper.isEnabledAndMsg(".command.mtc", sender)) {
            return true;
        }
        //if(!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc", label)) return true;
        if (args.length == 0) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.credits", label)) {
                return true;
            }
            sender.sendMessage("§eMinoTopiaCore AKA MTC AKA MTS AKA XyUtil by xxyy98. http://bit.ly/_xy");
            sender.sendMessage("§9Version " + Const.versionString + ")");
            sender.sendMessage("§3Hilfe? /" + label + " help | Kommandos? /help minotopiacore");
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.help", label)) {
                    return true;
                }
                //permissions included in tryPrintHelp()
                if (args.length == 1) {
                    HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
                } else {
                    String pageNum = ((args.length < 3) ? "" : args[2]);
                    if (!HelpManager.tryPrintHelp(args[1].toLowerCase(), sender, args[1].toLowerCase(), pageNum, "mtc help " + args[1].toLowerCase())) {
                        sender.sendMessage("§8Kein Kommando mit diesem Namen vorhanden oder interner Fehler!");
                    }
                }
            } else {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!CommandHelper.checkActionPermAndMsg(sender, "mtc.cmd.mtc.reload", "MTC reloaden")) {
                        return true;
                    }
                    CommandHelper.sendImportantActionMessage(sender, "Reloading MTC..");
                    Bukkit.getPluginManager().disablePlugin(MTC.instance());
                    Bukkit.getPluginManager().enablePlugin(MTC.instance());
                    CommandHelper.sendImportantActionMessage(sender, "Reloaded MTC!");
                } else {
                    if (args[0].equalsIgnoreCase("config")) {
                        switch (args[1]) {
                        case "set":
                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.config.set", label + " config set")) {
                                return true;
                            }
                            if (args.length < 4) {
                                sender.sendMessage("§8Invalide Argumente f§r /" + label + " config set. Hilfe:");
                                HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
                                return true;
                            }
                            if (args[2] == "enable.mtc" && sender instanceof Player) {
                                sender.sendMessage("§8Dieser Wert kann nur von der Konsole gesetzt werden,da irreversibel.");
                                return true;
                            }
                            String strValue = args[3];
                            Object value = strValue;

                            if (StringUtils.isNumeric(strValue)) {
                                value = Integer.parseInt(strValue);
                            } else {
                                if (strValue.equalsIgnoreCase("true") || strValue.equalsIgnoreCase("false")) {
                                    value = Boolean.parseBoolean(strValue);
                                }
                            }

                            MTC.instance().getConfig().set(args[2], value);
                            MTC.instance().saveConfig();
                            sender.sendMessage("§3" + args[2] + "§7 in der Config gesetzt auf: §3" + value + ".");
                            CommandHelper.sendImportantActionMessage(sender, "Set Config Value §3" + args[2] + "§a§o to §3" + value);
                            break;
                        case "get":
                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.config.get", label + " config get")) {
                                return true;
                            }
                            if (args.length < 3) {
                                sender.sendMessage("§7Invalide Argumente f§r §3/" + label + " config get§7. Hilfe:");
                                HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
                                return true;
                            }
                            String get = String.valueOf(MTC.instance().getConfig().get(args[2]));
                            sender.sendMessage("§7Der Wert §3" + args[2] + "§7 ist im Moment gesetzt auf: §3" + get + "§e.");
                            break;
                        case "reload":
                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.config.reload", label + " config reload")) {
                                return true;
                            }
                            MTC.instance().reloadConfig();
                            ConfigHelper.onConfigReload();
                            sender.sendMessage("§6Config neu geladen!");
                            CommandHelper.sendImportantActionMessage(sender, "Reloaded mtc config");
                            break;
                        default:
                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.config", label + " config")) {
                                return true;
                            }
                            sender.sendMessage("§8Unbekannte Aktion config " + args[1] + ". Hilfe:");
                            HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
                        }
                        ConfigHelper.onConfigReload();
                    } else {
                        if (args[0].equalsIgnoreCase("fm")) {
                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.fm", label + " fm")) {
                                return true;
                            }
                            String text = "";
                            for (int i = 1; i < args.length; i++) {
                                text += ((i == 1) ? "" : " ") + args[i];
                            }
                            text = ChatColor.translateAlternateColorCodes('&', text);
                            Bukkit.broadcastMessage(text);
                        } else {
                            if (args[0].equalsIgnoreCase("rename")) {
                                if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.rename", label + " rename")) {
                                    return true;
                                }
                                if (!(sender instanceof Player)) {
                                    sender.sendMessage("§7Du kannst dieses Kommando nur als Spieler benutzen!");
                                    return true;
                                }
                                Player plr = (Player) sender;
                                ItemStack stack = plr.getItemInHand();
                                if (stack == null || stack.getAmount() == 0) {
                                    plr.sendMessage("§8Du hast nichts in der Hand!");
                                    return true;
                                }
                                String text = "";
                                for (int i = 1; i < args.length; i++) {
                                    text += ((i == 1) ? "" : " ") + args[i];
                                }
                                text = ChatColor.translateAlternateColorCodes('&', text);
                                if (text.length() >= 60) {
                                    plr.sendMessage("§8So lange Namen crashen Server :/ true story, bro!");
                                    return true;
                                }
                                ItemMeta meta = stack.getItemMeta();
                                meta.setDisplayName(text);
                                stack.setItemMeta(meta);
                                plr.setItemInHand(stack);
                                plr.sendMessage("§7Der Name deines Items wurde auf §3" + text + "§7 gesetzt.");
                            } else {
                                if (args[0].equalsIgnoreCase("milk")) {
                                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.milk", label + " milk")) {
                                        return true;
                                    }
                                    if (!(sender instanceof Player)) {
                                        sender.sendMessage("Du kannst dieses Kommando nur als Spieler benutzen!");
                                        return true;
                                    }
                                    Player plr = (Player) sender;
                                    for (PotionEffect pot : plr.getActivePotionEffects()) {
                                        plr.removePotionEffect(pot.getType());
                                    }
                                    plr.sendMessage("§7Du hast die Macht der §f§lMILCH §7benutzt.");
                                } else {
                                    if (args[0].equalsIgnoreCase("ci")) {
                                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.ci", label + " ci")) {
                                            return true;
                                        }
                                        if (!(sender instanceof Player)) {
                                            sender.sendMessage("Du kannst dieses Kommando nur als Spieler benutzen!");
                                            return true;
                                        }
                                        CommandHelper.clearInv((Player) sender);
                                        sender.sendMessage("§7Marcel Davis von 1&1 hat deine Items gegessen.");
                                        return true;
                                    } else {
                                        if (args[0].equalsIgnoreCase("sign")) {
                                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.sign", label + " sign")) {
                                                return true;
                                            }
                                            if (args.length < 3) {
                                                sender.sendMessage("§3/" + label + " sign <line#> <text>§7 <-- Benutzung");
                                                return true;
                                            }
                                            if (!(sender instanceof Player)) {
                                                sender.sendMessage("§7Du kannst diesen Befehl nur als Spieler benutzen.");
                                                return true;
                                            }
                                            int line = 0;
                                            try {
                                                line = Integer.parseInt(args[1]);
                                            } catch (Exception e) {
                                                sender.sendMessage("§8Das ist keine Zahl.");
                                                return true;
                                            }
                                            Player plr = (Player) sender;
                                            @SuppressWarnings("deprecation")
                                            List<Block> blks = plr.getLastTwoTargetBlocks(null, 120);
                                            Block target = blks.get(1);
                                            //System.out.println(target.toString());
                                            if (!(target.getType() == Material.WALL_SIGN) && !(target.getType() == Material.SIGN_POST)) {
                                                sender.sendMessage("§8Das nennst du ein Schild?!");
                                                return true;
                                            }
                                            String text = "";
                                            for (int i = 2; i < args.length; i++) {
                                                text += ((i == 2) ? "" : " ") + args[i];
                                            }
                                            text = ChatColor.translateAlternateColorCodes('&', text);

                                            Sign sgn = (Sign) target.getState();
                                            sgn.setLine(line, text);
                                            sgn.update();
                                            sender.sendMessage("§7Das Schild wurde editiert.");
                                        } else {
                                            if (args[0].equalsIgnoreCase("dline")) {
                                                if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.sign", label + " dline")) {
                                                    return true;
                                                }
                                                if (args.length < 2) {
                                                    sender.sendMessage("§3/" + label + " dline <line>§7 <-- Benutzung");
                                                    return true;
                                                }
                                                if (!(sender instanceof Player)) {
                                                    sender.sendMessage("§8Du kannst diesen Befehl nur als Spieler benutzen.");
                                                    return true;
                                                }
                                                int line = 0;
                                                try {
                                                    line = Integer.parseInt(args[1]);
                                                } catch (Exception e) {
                                                    sender.sendMessage("§8Das ist keine Zahl.");
                                                    return true;
                                                }
                                                Player plr = (Player) sender;
                                                @SuppressWarnings("deprecation")
                                                List<Block> blks = plr.getLastTwoTargetBlocks(null, 120);
                                                Block target = blks.get(1);
                                                //System.out.println(target.toString());
                                                if (!(target.getType() == Material.WALL_SIGN) && !(target.getType() == Material.SIGN_POST)) {
                                                    sender.sendMessage("§8Das nennst du ein Schild?!");
                                                    return true;
                                                }
                                                Sign sgn = (Sign) target.getState();
                                                sgn.setLine(line, "");
                                                sgn.update();
                                                sender.sendMessage("§7Das Schild wurde editiert.");
                                            } else {
                                                if (args[0].equalsIgnoreCase("rne")) {
                                                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.rnentity", label + " rnentity")) {
                                                        return true;
                                                    }
                                                    if (args.length < 2) {
                                                        sender.sendMessage("§3/" + label + " rne <neuer Name>§7 <-- Benutzung");
                                                        return true;
                                                    }
                                                    if (!(sender instanceof Player)) {
                                                        sender.sendMessage("§7Du kannst diesen Befehl nur als Spieler benutzen.");
                                                        return true;
                                                    }
                                                    String text = "";
                                                    for (int i = 1; i < args.length; i++) {
                                                        text += ((i == 1) ? "" : " ") + args[i];
                                                    }
                                                    text = ChatColor.translateAlternateColorCodes('&', text);
                                                    Player plr = (Player) sender;
                                                    List<Entity> ents = plr.getNearbyEntities(1, 1, 1);
                                                    if (ents.size() < 1) {
                                                        plr.sendMessage("§8FOREVER ALONE");
                                                        return true;
                                                    }
                                                    Entity ent = ents.get(0);
                                                    if (!(ent instanceof LivingEntity)) {
                                                        plr.sendMessage("§8Das ist tot.");
                                                        return true;
                                                    }
                                                    ((LivingEntity) ent).setCustomName(text);
                                                    ((LivingEntity) ent).setCustomNameVisible(true);
                                                    plr.sendMessage("§7Der Name des/der " + ent.getType().toString() + " in deiner Nähe wurde geändert.");
                                                    return true;
                                                } else {
                                                    if (args.length >= 1 && args[0].equalsIgnoreCase("cc")) {
//			if(!CommandHelper.checkPermAndMsg(sender, "mtc.chatclear", label)) return true;
//			for(Player plr:Bukkit.getOnlinePlayers()){
//				if(plr.hasPermission("mtc.chatclear.exempt")){
//					plr.sendMessage(String.format(LangHelper.localizeString("XU-ccex", plr.getName(), MinoTopiaCore.instance().getName()),sender.getName()));
//					continue;
//				}
//				for(int i = 0;i < 200;i++){
//					plr.sendMessage("  ");
//				}
//			}
//			Bukkit.broadcastMessage(LangHelper.localizeString("XU-ccglo", "CONSOLE", MinoTopiaCore.instance().getName()));
                                                        sender.sendMessage(MTC.chatPrefix + "DEPRECATED! /cc");
                                                        return true;
                                                    } else {
                                                        if (args.length >= 1 && args[0].equalsIgnoreCase("motd")) {
                                                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.motd", label)) {
                                                                return true;
                                                            }
                                                            if (args.length == 1) {
                                                                sender.sendMessage(MTC.chatPrefix + "Aktuelle Motd: " + MTC.instance().motd);
                                                                return true;
                                                            }
                                                            String text = "";
                                                            for (int i = 1; i < args.length; i++) {
                                                                text += ((i == 1) ? "" : " ") + args[i];
                                                            }
                                                            MTC.instance().getConfig().set("motd", text);
                                                            MTC.instance().saveConfig();
                                                            text = ChatColor.translateAlternateColorCodes('&', text);//absichtlich nachher wgn encoding
                                                            MTC.instance().motd = text;
                                                            sender.sendMessage(MTC.chatPrefix + "Motd geändert auf: " + text);
                                                            return true;
                                                        } else {
                                                            if (args.length >= 1 && (args[0].equalsIgnoreCase("glomu") || args[0].equalsIgnoreCase("globalmute"))) {
//			if(!CommandHelper.checkPermAndMsg(sender, "mtc.globalmute.toggle", label)) return true;
//			ChatHelper.isGlobalMute = !ChatHelper.isGlobalMute;
//			Bukkit.broadcastMessage(MinoTopiaCore.chatPrefix+"GlobalMute wurde "+((ChatHelper.isGlobalMute) ? "" : "de")+"aktiviert!");
                                                                sender.sendMessage(MTC.chatPrefix + "DEPRECATED! /glomu");
                                                                return true;
                                                            } else {
                                                                if (args.length >= 1 && (args[0].equalsIgnoreCase("spy") || args[0].equalsIgnoreCase("chatspy"))) {
                                                                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.spy", label)) {
                                                                        return true;
                                                                    }
                                                                    if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                                                                        return true;
                                                                    }
                                                                    if (MTCChatHelper.spies.contains(sender.getName())) {
                                                                        MTCChatHelper.spies.remove(sender.getName());
                                                                        sender.sendMessage(MTC.chatPrefix + "Spy deaktiviert!");
                                                                        return true;
                                                                    }
                                                                    MTCChatHelper.spies.add(sender.getName());
//			MinoTopiaCore.instance().getConfig().set("spies", ChatHelper.spies);
//			MinoTopiaCore.instance().saveConfig();
                                                                    sender.sendMessage(MTC.chatPrefix + "Spy aktiviert!");
                                                                    return true;
                                                                } else {
                                                                    if (args.length > 0 && args[0].equalsIgnoreCase("rstlng")) {
                                                                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.rstlng", label)) {
                                                                            return true;
                                                                        }
                                                                        try {
                                                                            HashMap<String, YamlConfiguration> map = new HashMap<>();
                                                                            for (String lang : ((XyLocalizable) MTC.instance()).getShippedLocales()) {
                                                                                String dir = "plugins/XYC/lang/" + MTC.instance().getName();
                                                                                String fl = lang + ".lng.yml";
                                                                                File destFl = new File(dir, fl);
                                                                                File destDir = new File(dir);
                                                                                try {
                                                                                    destDir.mkdirs();
                                                                                    destFl.createNewFile();
                                                                                    FileOutputStream out = new FileOutputStream(destFl);
                                                                                    InputStream in = MTC.instance().getResource("lang/" + lang + ".lng.yml");
                                                                                    int read = -1;
                                                                                    while ((read = in.read()) != -1) {
                                                                                        out.write(read);
                                                                                    }
                                                                                    out.flush();
                                                                                    out.close();
                                                                                    in.close();
                                                                                    map.put(lang, YamlConfiguration.loadConfiguration(destFl));

                                                                                } catch (Exception e) {
                                                                                    System.out.println("[MTC]Could not reset localization files from JAR: " + lang);
                                                                                    e.printStackTrace();
                                                                                }
                                                                                LangHelper.reloadLang(MTC.instance(), lang);
                                                                                ConfigHelper.setLangRevision(LangHelper.getOption("revision", MTC.instance().getShippedLocales()[0], MTC.instance().
                                                                                        getName()));
                                                                            }
                                                                            sender.sendMessage(MTC.chatPrefix + "Sprachdateien resettet!");
                                                                        } catch (Exception e) {
                                                                            sender.sendMessage("§6Exception. Vielleicht ein /reload?");
                                                                            CommandHelper.sendMessageToOpsAndConsole("§c[MTC] Could not reset languages from JAR!");
                                                                            e.printStackTrace();
                                                                        }
                                                                        return true;
                                                                    } else {
                                                                        if (args.length >= 1 && args[0].equalsIgnoreCase("clearcache")) {
                                                                            BanHelper.banCache = new HashMap<>();
                                                                            MTCChatHelper.cfCache = new HashMap<>();
                                                                            ClanHelper.clearCache();
                                                                            sender.sendMessage(MTC.chatPrefix + "Cache geleert.");
                                                                        } else {
                                                                            if (args.length >= 1 && args[0].equalsIgnoreCase("forcecron")) {
                                                                                if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.forcecron", label)) {
                                                                                    return true;
                                                                                }
                                                                                (new RunnableCronjob5Minutes(true)).run();
                                                                                sender.sendMessage(MTC.chatPrefix + "Forced Cronjob (5m)!");
                                                                            } else {
                                                                                if (args.length >= 1 && args[0].equalsIgnoreCase("setspawn")) {
                                                                                    if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.setspawn", label)) {
                                                                                        return true;
                                                                                    }
                                                                                    if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                                                                                        return true;
                                                                                    }
                                                                                    Player plr = (Player) sender;
                                                                                    MTC.instance().getConfig().set("fixes.netherroof.spawn.worldName", plr.getLocation().getWorld().getName());
                                                                                    MTC.instance().getConfig().set("fixes.netherroof.spawn.x", plr.getLocation().getBlockX());
                                                                                    MTC.instance().getConfig().set("fixes.netherroof.spawn.y", plr.getLocation().getBlockY());
                                                                                    MTC.instance().getConfig().set("fixes.netherroof.spawn.z", plr.getLocation().getBlockZ());
                                                                                    MTC.instance().getConfig().set("fixes.netherroof.spawn.pitch", plr.getLocation().getPitch());
                                                                                    MTC.instance().getConfig().set("fixes.netherroof.spawn.yaw", plr.getLocation().getYaw());
                                                                                    MTC.instance().refreshSpawn();
                                                                                    MTC.instance().saveConfig();
                                                                                    sender.sendMessage(MTC.chatPrefix + "Spawn gesetzt.");
                                                                                } else {
                                                                                    if (args[0].equalsIgnoreCase("itemonjoin")) {
                                                                                        if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc", label)) {
                                                                                            return true;
                                                                                        }
                                                                                        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                                                                                            return true;
                                                                                        }
                                                                                        MTC.instance().getConfig().set("itemonjoin", ((Player) sender).getItemInHand());
                                                                                        MTC.instance().saveConfig();
                                                                                        sender.sendMessage(MTC.chatPrefix + "itemonjoin gesetzt.");
                                                                                    } else {
                                                                                        if (args[0].equalsIgnoreCase("test")) {
                                                                                            if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc", label)) {
                                                                                                return true;
                                                                                            }
                                                                                            if (CommandHelper.kickConsoleFromMethod(sender, label)) {
                                                                                                return true;
                                                                                            }
                                                                                            sender.sendMessage(((Player) sender).getItemInHand().hashCode() + ((Player) sender).getItemInHand().
                                                                                                    toString());
                                                                                        } else {
                                                                                            sender.sendMessage("§8Unbekannte Aktion §3'" + args[0] + "'§8. Hilfe:");
                                                                                            HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
