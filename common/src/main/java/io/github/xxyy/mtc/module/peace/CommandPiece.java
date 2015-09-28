package io.github.xxyy.mtc.module.peace;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.hook.XLoginHook;
import io.github.xxyy.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandPiece extends MTCPlayerOnlyCommandExecutor implements TabExecutor {

    public static final int PEACE_LIST_PAGE_SIZE = 15;

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
                    return CommandHelper.msg("§cFehler bei der Verarbeitung.", plr);
                }
                int rowstart = 0;
                int page = 1;
                if (args.length > 2) {
                    if (!StringUtils.isNumeric(args[1])) {
                        return CommandHelper.msg("§cDas ist keine gültige Zahl!", plr);
                    }
                    page = Integer.parseInt(args[1]);
                    if (page <= 0) {// if 0, rowstart would be negative
                        page = 1;
                    }
                    if (page > (peaceInfo.getPeaceWithInternal().size() / PEACE_LIST_PAGE_SIZE + 1)) {
                        return CommandHelper.msg("§cDas ist keine gültige Seitenzahl!", plr);
                    }
                    rowstart = (page - 1) * PEACE_LIST_PAGE_SIZE;
                }
                return sendPeaceList(plr, peaceInfo.getPeaceWithInternal(), rowstart, PEACE_LIST_PAGE_SIZE, label, page + 1);
            }
            case "status": {
                if (args.length < 2) {
                    return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
                }
                if (args[1].equalsIgnoreCase(plrName)) {
                    return CommandHelper.msg(MTC.chatPrefix + "Ich nehme an, dass du mit dir selbst Frieden hast.", plr);
                }
                XLoginHook.Profile targetProfile = module.getPlugin().getXLoginHook().getBestProfile(args[1]);
                PeaceInfo initiator = module.getPeaceInfoManager().get(plr.getUniqueId());

                if (PlayerPeaceRelation.areInPeace(module.getPeaceInfoManager(), initiator, targetProfile.getUniqueId())) {
                    return MTCHelper.sendLocArgs("XU-plrpeace", plr, true, args[1]);
                }
                if (LegacyPeaceInfo.hasRequest(plrName, args[1])) {
                    return MTCHelper.sendLocArgs("XU-preqpending", plr, true, args[1]);
                }
                if (LegacyPeaceInfo.hasRequest(args[1], plrName)) {
                    return MTCHelper.sendLocArgs("XU-preqpendingown", plr, true, args[1]);
                }
                return MTCHelper.sendLocArgs("XU-plrnopeace", plr, true, args[1]);
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
                return peaceInfo.getPeaceWithInternal().stream()
                        .map(uuid -> module.getPlugin().getXLoginHook().getBestProfile(uuid.toString()).getName())
                        .collect(Collectors.toList());
            }
            default: {
                return ImmutableList.of();
            }
        }
    }

    private String getPlayerString(UUID uuid) {
        String plrName = module.getPlugin().getXLoginHook().getDisplayString(uuid);
        if (Bukkit.getPlayer(uuid) == null) {
            return MTCHelper.locArgs("XC-membersoff", "CONSOLE", false, plrName);//continuity
        }
        return MTCHelper.locArgs("XC-memberson", "CONSOLE", false, plrName);
    }

    private boolean sendPeaceList(Player sender, List<UUID> uuids, int rowStart, int perPage, String label, int nextPage) {
        String toSend = "";
        int max = rowStart + perPage;
        for (int i = 0; (i < max && uuids.size() > i); i++) {
            toSend += " ► " + getPlayerString(uuids.get(i)) + "\n";
        }
        if (toSend.isEmpty()) {
            toSend = MTCHelper.loc("XU-ppageempty", sender, false);
        }
        if (max <= uuids.size()) {
            return MTCHelper.sendLocArgs("XU-pmorepages", sender, false, label, nextPage);
        }
        return CommandHelper.msg(toSend, sender);
    }
}
