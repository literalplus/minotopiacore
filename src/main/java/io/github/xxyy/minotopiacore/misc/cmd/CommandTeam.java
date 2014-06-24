package io.github.xxyy.minotopiacore.misc.cmd;

import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.MTCHelper;
import io.github.xxyy.minotopiacore.hook.PexHook;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandTeam extends MTCCommandExecutor {
    private final List<TeamGroup> groups = new ArrayList<>();
    private final MTC plugin;

    public CommandTeam(MTC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        MTCHelper.sendLoc("XU-teamheader", sender, false);
        if (this.groups.isEmpty()) {
            try {
                if (!plugin.getPexHook().isActive()) {
                    sender.sendMessage("§cPermissionsEx ist nicht verfügbar.");
                    return true;
                }

                plugin.getPexHook().getGroupList().stream()
                        .filter(group -> group.getOptionBoolean("team", null, false))
                        .sorted((group, group2) -> group2.getOptionInteger("teamweight", null, 0) - group.getOptionInteger("teamweight",null,0))
                        .forEach(group -> this.groups.add(new TeamGroup(group)));
            } catch (Throwable e) {
                sender.sendMessage("§cFehler beim Holen der Teammitglieder.");
                e.printStackTrace();
                System.out.println("PermissionsEx Hook failed. (CommandTeam)");
                return true;
            }
        }

        List<TeamMember> allMembers = new LinkedList<>();
        this.groups.stream().forEach((grp) -> allMembers.addAll(grp.getMembers()));

        Arrays.asList(Bukkit.getOnlinePlayers()).stream()
                .forEach((plr) -> allMembers.stream()
                        .forEach((member) -> member.checkMatch(plr)
                        )); //Only loop through online players once

        this.groups.stream()
                .filter(TeamGroup::hasMembers)
                .forEach(group -> sender.sendMessage(group.niceRepresentation()));

        return true;
    }

    @Override
    public void clearCache() {
        this.groups.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class TeamMember {
        private final UUID uuid;
        private boolean lastOnline = false;
        private String lastName;

        public TeamMember(PexHook.User user) {
            Validate.notNull(user);

            this.uuid = user.getUniqueId();
            this.lastName = user.getName();
        }

        public UUID getUuid() {
            return uuid;
        }

        /**
         * @return An online state of this user, as found in the last online check.
         */
        public boolean isLastOnline() {
            return lastOnline;
        }

        public String getLastName() {
            return lastName;
        }

        /**
         * Checks whether the given player is representing the same player as this object.
         *
         * @param plr PLayer to check
         * @return This object's lastOnline state.
         */
        public boolean checkMatch(Player plr) {
            lastOnline = (plr.getUniqueId().equals(getUuid()) || plr.getName().equals(lastName)); //TODO do we need the name check??
            return lastOnline;
        }

        public String niceRepresentation() {
            return MTCHelper.locArgs("XU-team" +
                    (isLastOnline() ? "on" : "off") +
                    "line", "CONSOLE", false, this.getLastName());
        }

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TeamMember that = (TeamMember) o;

            if (!uuid.equals(that.uuid)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }
    }

    public static class TeamGroup {
        private final Set<TeamMember> members = new HashSet<>();
        private final String name;
        private final String prefix;

        /**
         * Constructs a new TeamGroup from a PEx PermissionGroup.
         * This will fetch name, prefix and members.
         *
         * @param permissionGroup Group to import
         */
        public TeamGroup(PexHook.Group permissionGroup) {
            Validate.notNull(permissionGroup);

            this.name = permissionGroup.getName();
            this.prefix = permissionGroup.getPrefix();

            this.members.addAll(permissionGroup.getUsers().parallelStream()
                    .map(TeamMember::new)
                    .collect(Collectors.toList()));
        }

        public Set<TeamMember> getMembers() {
            return members;
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }

        public boolean hasMembers() {
            return !members.isEmpty();
        }

        public String niceRepresentation() {
            if(!hasMembers()){
                return null;
            }

            StringBuilder sb = new StringBuilder(ChatColor.translateAlternateColorCodes('&',getPrefix())).append(' ');
            String separator = MTCHelper.loc("XU-teamseperator", "CONSOLE", false);
            this.members.parallelStream().forEach(member -> sb.append(member.niceRepresentation()).append(separator));
            sb.delete(sb.length() - separator.length(), sb.length()); //Remove trailing separator
            return sb.toString();
        }

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TeamGroup teamGroup = (TeamGroup) o;

            if (!name.equals(teamGroup.name)) return false;
            if (!prefix.equals(teamGroup.prefix)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + prefix.hashCode();
            return result;
        }
    }
}
