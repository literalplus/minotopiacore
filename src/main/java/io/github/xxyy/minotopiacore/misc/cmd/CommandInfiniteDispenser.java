package io.github.xxyy.minotopiacore.misc.cmd;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValueAdapter;

public final class CommandInfiniteDispenser extends MTCCommandExecutor {

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.infinitedispenser", label)) {
            return true;
        }
        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }
        Player plr = (Player) sender;
        if (args.length > 0) {
            switch (args[0]) {
            case "on":
                BlockState disp = CommandInfiniteDispenser.checkTargetBlock(plr);
                if (disp == null) {
                    return true;
                }
                disp.setMetadata("mtc.infinite", new MetadataValueAdapter(MTC.instance()) {
                    @Override public void invalidate() {
                    }

                    @Override public Object value() {
                        return true;
                    }
                });
                return MTCHelper.sendLoc("XU-infdispon", sender, true);
            case "off":
                BlockState disp2 = CommandInfiniteDispenser.checkTargetBlock(plr);
                if (disp2 == null) {
                    return true;
                }
                disp2.removeMetadata("mtc.infinite", MTC.instance());
                return MTCHelper.sendLoc("XU-infdispoff", sender, true);
            default:
                break;
            }
        }
        return MTCHelper.sendLocArgs("XU-infdisphelp", sender, false, label);
    }

    private static BlockState checkTargetBlock(Player plr) {
        @SuppressWarnings("deprecation")
        Block blk = plr.getTargetBlock(null, 100); //BUKKIT! HAVEN'T I TOLD YOU NOT TO DEPRECATED USEFUL STUFF?!
        if (blk == null || !(blk.getState() instanceof InventoryHolder)) {
            MTCHelper.sendLoc("XU-nodisp", plr, true);
            return null;
        }
        return blk.getState();
    }
}
