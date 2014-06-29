package io.github.xxyy.minotopiacore.misc;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.misc.cmd.CommandLore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LoreManager {

    public ItemMeta meta;
    public String label;
    public String[] args;
    public CommandSender sender;
    public Player plr;
    public ItemStack itemInHand;
    public CommandLore cl;
    private int tempInt;

    public LoreManager(CommandSender sdr, String lbl, String[] argz, CommandLore colo) {
        this.sender = sdr;
        this.label = lbl;
        this.args = argz;
        this.cl = colo;
    }

    public boolean addLore() {
        if (this.prepareLore(true)) {
            return true;
        }
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.cmd.lore.add", this.label + " add")) {
            return true;
        }

        String text = this.colorFormatAndToStringArgs(1);
        List<String> lore = this.meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        if (this.checkIfFullAndMsg(lore)) {
            return true;
        }
        lore.add("§r" + text);
        this.meta.setLore(lore);
        this.itemInHand.setItemMeta(this.meta);
        this.formatLore(lore, "gesetzt auf");

        return true;
    }

    public boolean checkIfFullAndMsg(List<String> lore) {
        if (lore.size() <= 0) {
            return false;
        }
        for (String str : lore) {
            if (str.startsWith("§9full:")) {
                this.sender.sendMessage("§4Du kannst die Lore einer registrierten Full-Rüstung nicht editieren!");
                return true;
            }
        }
        return false;
    }

    //checks if index & lore are valid
    //returns true if operation should be aborted
    public boolean checkIndexAndLore(List<String> lore) {
        if (lore == null) {
            this.sender.sendMessage("§7Das Item in deiner Hand hat keine Lore => Es gibt nichts zu entfernen :)");
            this.cl.printHelpToSender(this.sender, this.label);
            return true;
        }
        try {
            this.tempInt = Integer.parseInt(this.args[1]);
        } catch (Exception e) {
            this.sender.sendMessage("§7Dein 2.Argument '" + this.args[1] + "' ist keine Zahl! Hilfe:");
            this.cl.printHelpToSender(this.sender, this.label);
            return true;
        }
        if (this.tempInt > lore.size() || this.tempInt <= 0) {
            this.sender.sendMessage("§7Es gibt keine Zeile mit dieser Nummer!");
            this.cl.printHelpToSender(this.sender, this.label);
            return true;
        }
        return false;
    }

    public boolean checkNeedExtendedArgCount() {
        return this.checkNeedExtendedArgCount(2);
    }

    public boolean checkNeedExtendedArgCount(int requiredAmount) {
        if (this.args.length < requiredAmount) {
            this.sender.sendMessage("§7Falsche Verwendung von /" + this.label + ":§7 Mindestens 2 Argumente erforderlich!");
            this.cl.printHelpToSender(this.sender, this.label);
            return true;
        }
        return false;
    }

    //util methods
    public boolean clearLore() {
        if (this.prepareLore(false)) {
            return true;
        }
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.cmd.lore.clear", this.label + " clear")) {
            return true;
        }

        List<String> lore = this.meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        if (this.checkIfFullAndMsg(lore)) {
            return true;
        }
        this.meta.setLore(null);
        this.itemInHand.setItemMeta(this.meta);
        this.sender.sendMessage("§7Lore des Items in deiner Hand (§8" + this.itemInHand.getType().name() + "§7) geleert.");

        return true;
    }

    public String colorFormatAndToStringArgs(int beginAtIndex) {
        String text = "";
        for (int i = beginAtIndex; i < this.args.length; i++) {
            text += ((i == beginAtIndex) ? "" : " ").concat(this.args[i]);
        }
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    public void formatLore(List<String> lore, String suffix) {
        if (!CommandHelper.checkActionPermAndMsg(this.sender, "mtc.cmd.lore.list", "Anzeigen der Lore des Items in deiner Hand")) {
            return;
        }
        this.sender.sendMessage("§7Lore für das Item in deiner Hand mit der ID (§8" + this.itemInHand.getType().name() + "§7) " + suffix + ":");
        int j = 1;
        for (String item : lore) {
            this.sender.sendMessage(" §7" + j + " §8=> " + item);
            j++;
        }
    }

    public boolean listlore() {
        if (this.prepareLore(false)) {
            return true;
        }
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.cmd.lore.list", this.label + " list")) {
            return true;
        }

        List<String> lore = this.meta.getLore();
        this.formatLore(lore, "ist");

        return true;
    }

    public boolean prepareLore(boolean extArgCount) {
        return this.prepareLore(extArgCount, 2);
    }

    public boolean prepareLore(boolean extArgCount, int minArgAmount) {
        if (LoreManager.kickConsoleFromMethod(this.sender, this.label)) {
            return true;
        }
        if (extArgCount && this.checkNeedExtendedArgCount(minArgAmount)) {
            return true;
        }
        this.plr = (Player) this.sender;
        if (this.plr.getInventory().getItemInHand().getAmount() == 0) {
            this.sender.sendMessage("§7Du hast nichts in der Hand!");
            return true;
        }
        this.itemInHand = this.plr.getInventory().getItemInHand();
        this.meta = this.itemInHand.getItemMeta();
        return false;
    }

    public boolean removeLore() {
        if (this.prepareLore(true)) {
            return true;
        }
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.cmd.lore.remove", this.label + " remove")) {
            return true;
        }

        List<String> lore = this.meta.getLore();
        if (lore == null) {
            this.sender.sendMessage("§7Das Item in deiner Hand hat keine Lore => Es gibt nichts zu entfernen :)");
            this.cl.printHelpToSender(this.sender, this.label);
            return true;
        }
        if (this.checkIfFullAndMsg(lore)) {
            return true;
        }
        int index;
        try {
            index = Integer.parseInt(this.args[1]);
        } catch (Exception e) {
            this.sender.sendMessage("§7Dein 2.Argument '" + this.args[1] + "' ist keine Zahl! Hilfe:");
            this.cl.printHelpToSender(this.sender, this.label);
            return true;
        }
        if (index > lore.size() || index <= 0) {
            this.sender.sendMessage("§7Es gibt keine Zeile mit dieser Nummer!");
            this.cl.printHelpToSender(this.sender, this.label);
            return true;
        }
        String removed = lore.remove(index - 1);
        this.meta.setLore(lore);
        this.itemInHand.setItemMeta(this.meta);
        this.formatLore(lore, "gesetzt auf:");
        this.sender.sendMessage("§7Die folgende Zeile wurde entfernt: §7" + removed);

        return true;
    }

    public boolean setLoreAt() {
        if (this.prepareLore(true, 3)) {
            return true;
        }
        if (!CommandHelper.checkPermAndMsg(this.sender, "mtc.cmd.lore.set", this.label + " set")) {
            return true;
        }

        List<String> lore = this.meta.getLore();
        if (this.checkIndexAndLore(lore)) {
            return true;
        }
        String text = this.colorFormatAndToStringArgs(2);
        lore.set(this.tempInt - 1, "§r" + text);
        this.meta.setLore(lore);
        this.itemInHand.setItemMeta(this.meta);
        this.formatLore(lore, "gesetzt auf");

        return true;
    }

    private static boolean kickConsoleFromMethod(CommandSender sender, String label) {
        return CommandHelper.kickConsoleFromMethod(sender, label);
    }
}
