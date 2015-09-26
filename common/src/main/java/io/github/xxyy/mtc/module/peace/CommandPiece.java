package io.github.xxyy.mtc.module.peace;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPiece extends MTCPlayerOnlyCommandExecutor implements TabExecutor {

    @NotNull
    private final PeaceModule module;

    public CommandPiece(@NotNull PeaceModule module) {
        this.module = module;
    }

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
        }
        switch (args[0].toLowerCase()) {
            case "list": {
                PeaceInfo peaceInfo = module.getPeaceInfoManager().get(plr.getUniqueId());
                if (peaceInfo == null) {
                    return CommandHelper.msg("§cFehler bei der Verarbeitung. ", plr);
                }
                break;
            }
            case "status": {
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }
        Player plr = (Player) sender;
        if (args.length == 0) {
            List<String> list = new ArrayList<>();
            list.add("list");
            list.add("status");
            list.add("help");
            list.add("revoke");
            list.addAll(CommandHelper.getOnlinePlayerNames());
            return list;
        }
        switch (args[0].toLowerCase()) {
            case "revoke": {
                PeaceInfo peaceInfo = module.getPeaceInfoManager().get(plr.getUniqueId());
                if (peaceInfo == null) {
                    return null;
                }
                //TODO remove because it could lag on main thread if you have many friends?
                return peaceInfo.getPeaceWith().stream()
                        .map(uuid -> module.getPlugin().getXLoginHook().getBestProfile(uuid.toString()).getName())
                        .collect(Collectors.toList());
            }
            default: {
                return ImmutableList.of();
            }
        }
    }
}
