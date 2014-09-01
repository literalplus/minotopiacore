package io.github.xxyy.mtc.misc.cmd;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;
import io.github.xxyy.mtc.hook.PexHook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandTeam extends MTCCommandExecutor {
    private final List<TeamGroup> groups = new ArrayList<>();
    private final MTC plugin;
    private final List<TeamMember> allMembers = new LinkedList<>();

    public CommandTeam(MTC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        MTCHelper.sendLoc("XU-teamheader", sender, false);
        if (this.groups.isEmpty()) {
            plugin.getLogger().info("Now fetching groups!");
            try {
                if (!plugin.getPexHook().isActive()) {
                    sender.sendMessage("§cPermissionsEx ist nicht verfügbar.");
                    return true;
                }

                plugin.getPexHook().getGroupList().stream()
                        .filter(group -> group.getOptionBoolean("team", null, false))
                        .sorted((group, group2) -> group2.getOptionInteger("teamweight", null, 0) - group.getOptionInteger("teamweight", null, 0))
                        .forEach(group -> this.groups.add(new TeamGroup(group)));

                groups.stream().forEach(grp -> allMembers.addAll(grp.getMembers()));
            } catch (Throwable e) {
                sender.sendMessage("§cFehler beim Holen der Teammitglieder.");
                e.printStackTrace();
                System.out.println("PermissionsEx Hook failed. (CommandTeam)");
                return true;
            }
        }

        this.groups.stream()
                .filter(TeamGroup::hasMembers)
                .forEach(group -> sender.sendMessage(group.niceRepresentation()));

        return true;
    }

    @Override
    public void clearCache(boolean forced, MTC plugin) {
        this.groups.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class TeamMember {
        private final UUID uuid;
        private String lastName;

        public TeamMember(PexHook.User user) {
            Validate.notNull(user);

            this.uuid = user.getUniqueId(); //Not calling (UUID, String) for the null-check
            this.lastName = user.getName();
        }

        public TeamMember(UUID uuid, String lastName) { //For tests
            this.uuid = uuid;
            this.lastName = lastName;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getLastName() {
            return lastName;
        }

        public boolean isOnline() {
            return Bukkit.getPlayer(uuid) != null;
        }

        public String niceRepresentation() {
            return MTCHelper.locArgs("XU-team" +
                    (isOnline() ? "on" : "off") +
                    "line", "CONSOLE", false, this.getLastName());
        }

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TeamMember that = (TeamMember) o;

            if (!uuid.equals(that.uuid)) {
                return false;
            }

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
            if (!hasMembers()) {
                return null;
            }

            StringBuilder sb = new StringBuilder(ChatColor.translateAlternateColorCodes('&', getPrefix())).append(' ');
            String separator = MTCHelper.loc("XU-teamseperator", "CONSOLE", false);
            this.members.stream().forEach(member -> sb.append(member.niceRepresentation()).append(separator));
            sb.delete(sb.length() - separator.length(), sb.length()); //Remove trailing separator
            return sb.toString();
        }

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TeamGroup teamGroup = (TeamGroup) o;

            if (!name.equals(teamGroup.name)) {
                return false;
            }
            if (!prefix.equals(teamGroup.prefix)) {
                return false;
            }

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
