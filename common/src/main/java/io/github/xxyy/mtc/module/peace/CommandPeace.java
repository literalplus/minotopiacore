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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandPeace extends MTCPlayerOnlyCommandExecutor implements TabExecutor {

    private static final int PEACE_LIST_PAGE_SIZE = 15;
    private static final Pattern MINUS_PATTERN = Pattern.compile("-", Pattern.LITERAL);

    @NotNull
    private final PeaceModule module;
    private final PeaceInfoManager manager;
    private final XLoginHook xLoginHook;

    public CommandPeace(@NotNull PeaceModule module) {
        this.module = module;
        manager = module.getPeaceInfoManager();
        xLoginHook = module.getPlugin().getXLoginHook();
    }

    @Override
    public boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
        }
        UUID initiatorUuid = plr.getUniqueId();
        switch (args[0].toLowerCase()) {
            case "list": {
                PeaceInfo peaceInfo = manager.get(initiatorUuid);
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
                    if (page < 1) {// if 0, rowstart would be negative
                        page = 1;
                    }
                    if (page > (peaceInfo.getPeaceWith().size() / PEACE_LIST_PAGE_SIZE + 1)) {
                        return CommandHelper.msg("§cDas ist keine gültige Seitenzahl!", plr);
                    }
                    rowstart = (page - 1) * PEACE_LIST_PAGE_SIZE;
                }
                return sendPeaceList(plr, peaceInfo.getPeaceWith(), rowstart, PEACE_LIST_PAGE_SIZE, label, page + 1);
            }
            case "status": {
                if (args.length < 2) {
                    return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
                }
                if (args[1].equalsIgnoreCase(plrName)) {
                    return CommandHelper.msg(MTC.chatPrefix + "Ich nehme an, dass du mit dir selbst Frieden hast.", plr);
                }
                XLoginHook.Profile targetProfile = xLoginHook.getBestProfile(args[1]); //TODO null
                PeaceInfo initiator = manager.get(initiatorUuid);

                UUID targetUuid = targetProfile.getUniqueId();
                if (PlayerPeaceRelation.areInPeace(manager, initiator, targetUuid)) {
                    return MTCHelper.sendLocArgs("XU-plrpeace", plr, true, args[1]);
                }
                if (PlayerPeaceRelation.isRequestSent(manager, initiator, targetUuid)) {
                    return MTCHelper.sendLocArgs("XU-preqpending", plr, true, args[1]);
                }
                if (PlayerPeaceRelation.isRequestRecieved(manager, initiator, targetUuid)) {
                    return MTCHelper.sendLocArgs("XU-preqpendingown", plr, true, args[1]);
                }
                return MTCHelper.sendLocArgs("XU-plrnopeace", plr, true, args[1]);
            }
            case "hilfe":
            case "help": {
                return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
            }
            case "revoke":
            case "zurückrufen": {
                if (args.length < 2) {
                    return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
                }
                if (args[1].equalsIgnoreCase(plrName) ||
                    args[1].equalsIgnoreCase(plr.getUniqueId().toString()) ||
                    args[1].equalsIgnoreCase(MINUS_PATTERN.matcher(plr.getUniqueId().toString()).replaceAll(Matcher.quoteReplacement("")))) {
                    return CommandHelper.msg(MTC.chatPrefix + "Du hast dir selbst keine Friedensanfrage gesendet.", plr);
                }
                PeaceInfo initiator = manager.get(initiatorUuid);
                XLoginHook.Profile targetProfile = xLoginHook.getBestProfile(args[1]); //TODO null
                UUID targetUuid = targetProfile.getUniqueId();
                if (PlayerPeaceRelation.isRequestSent(manager, initiator, targetUuid)) {
                    PeaceInfo targetPi = manager.get(targetUuid);

                    targetPi.getRequestsGot().remove(initiatorUuid);
                    targetPi.setDirty();
                    initiator.getRequestsSent().remove(targetUuid);
                    initiator.setDirty();

                    MTCHelper.sendLocArgs("XU-preqrevoked", plr, true, args[1]);
                    Player targetPlr = module.getPlugin().getServer().getPlayer(targetUuid);
                    if (targetPlr != null) {
                        MTCHelper.sendLocArgs("XU-preqrevokedbyother", targetPlr, true, plrName);
                    }
                    return true;
                }

                return true;
            }
            default:
                return MTCHelper.sendLocArgs("XU-peacehelp", plr, false, label);
        }
    }

    private final List<String> subCommands = Arrays.asList("list", "status", "annehmnen", "ablehnen", "zurückrufen", "anfragen", "hilfe");
    private final List<String> subCommandAliases = Arrays.asList("accept", "deny", "revoke", "request", "help");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }
        Player plr = (Player) sender;
        if (args.length == 0) { //should not happen; api behaviour not documented enough to remove
            return null;
        }
        UUID uuid = plr.getUniqueId();
        switch (args[0].toLowerCase().trim()) {
            case "": {
                return subCommands;
            }
            case "accept":
            case "annehmnen": {
                return getTabCompleteMatchesAndGetPeaceInfoList(uuid, args, 1, PeaceInfo::getRequestsGot);
            }
            case "status": {
                return getTabCompleteMatchesAndGetPeaceInfo(uuid, args, 1, peaceInfo ->
                    Stream.concat(
                        Stream.concat(
                            Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId),
                            peaceInfo.getPeaceWith().stream()),
                        Stream.concat(
                            peaceInfo.getRequestsGot().stream(),
                            peaceInfo.getRequestsSent().stream())));
            }
            case "deny":
            case "ablehnen": {
                return getTabCompleteMatchesAndGetPeaceInfoList(uuid, args, 1, PeaceInfo::getRequestsSent);
            }
            case "revoke":
            case "zurückrufen": {
                return getTabCompleteMatchesAndGetPeaceInfo(uuid, args, 1, peaceInfo ->
                    Stream.concat(
                        peaceInfo.getPeaceWith().stream(),
                        peaceInfo.getRequestsSent().stream()));
            }
            case "request":
            case "anfragen": {
                return getTabCompleteMatchesAndGetPeaceInfo(uuid, args, 1, peaceInfo ->
                    Bukkit.getOnlinePlayers().stream()
                        .map(Player::getUniqueId)
                        .filter(((Predicate<UUID>) peaceInfo.getPeaceWith()::contains).negate()));
            }
            default: {
                if (args.length == 1) {
                    String startedSubCmd = args[0].toLowerCase();

                    List<String> noAliasesMatches = subCommands.stream()
                        .filter(subCmd -> subCmd.startsWith(startedSubCmd))
                        .collect(Collectors.toList());

                    if (!noAliasesMatches.isEmpty()) {
                        return noAliasesMatches;
                    } else {
                        return subCommandAliases.stream()
                            .filter(subCmd -> subCmd.startsWith(startedSubCmd))
                            .collect(Collectors.toList());
                    }
                }
                return ImmutableList.of();
            }
        }
    }

    private List<String> getTabCompleteMatchesAndGetPeaceInfo(UUID uuid, String[] args, int namePos, Function<PeaceInfo, Stream<UUID>> uuidProvider) {
        PeaceInfo peaceInfo = manager.get(uuid);
        if (peaceInfo == null) {
            return null;
        }
        return getTabCompleteMatches(args, namePos, getNamesFromUuid(uuidProvider.apply(peaceInfo)));
    }

    private List<String> getTabCompleteMatchesAndGetPeaceInfoList(UUID uuid, String[] args, int namePos, Function<PeaceInfo, List<UUID>> uuidProvider) {
        PeaceInfo peaceInfo = manager.get(uuid);
        if (peaceInfo == null) {
            return null;
        }
        return getTabCompleteMatches(args, namePos, getNamesFromUuid(uuidProvider.apply(peaceInfo)));
    }

    private Stream<String> getNamesFromUuidStream(List<UUID> uuids) {
        return mapStreamUuidsToNames(uuids.stream());
    }

    private Stream<String> mapStreamUuidsToNames(Stream<UUID> uuidStream) {
        return uuidStream.map(uuid -> xLoginHook.getDisplayString(uuid));
    }

    private List<String> getNamesFromUuid(List<UUID> uuids) {
        return getNamesFromUuidStream(uuids)
            .collect(Collectors.toList());
    }

    private List<String> getNamesFromUuid(Stream<UUID> uuids) {
        return mapStreamUuidsToNames(uuids)
            .collect(Collectors.toList());
    }

    private List<String> getTabCompleteMatches(String[] args, int namePos, Stream<String> names) {
        if (args.length <= namePos + 1) {
            String startedPlayerName = args[namePos].toLowerCase();

            return names
                .filter(s -> s.toLowerCase().startsWith(startedPlayerName))
                .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }

    private List<String> getTabCompleteMatches(String[] args, int namePos, List<String> names) {
        return getTabCompleteMatches(args, namePos, names.stream());
    }

    private String getPlayerStringColoredByOnlineState(UUID uuid) {
        String plrName = xLoginHook.getDisplayString(uuid);
        if (Bukkit.getPlayer(uuid) == null) {
            return MTCHelper.locArgs("XC-membersoff", "CONSOLE", false, plrName);//continuity
        }
        return MTCHelper.locArgs("XC-memberson", "CONSOLE", false, plrName);
    }

    private boolean sendPeaceList(Player sender, List<UUID> uuids, int rowStart, int perPage, String label, int nextPage) {
        String toSend = "";
        int max = rowStart + perPage;
        for (int i = 0; (i < max && uuids.size() > i); i++) {
            toSend += " ► " + getPlayerStringColoredByOnlineState(uuids.get(i)) + "\n";
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
