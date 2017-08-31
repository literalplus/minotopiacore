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

package li.l1t.mtc.hook;

import com.google.common.base.Preconditions;
import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.hook.impl.XLoginHookImpl;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Helps interfacing with the xLogin plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class XLoginHook extends SimpleHookWrapper {
    private XLoginHookImpl unsafe;

    public XLoginHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    public boolean isAuthenticated(Player plr) throws IllegalStateException {
        return isAuthenticated(plr.getUniqueId());
    }

    public boolean isAuthenticated(UUID uuid) throws IllegalStateException {
        return isActive() && unsafe.isAuthenticated(uuid);
    }

    public Location getSpawnLocation() {
        if (!isActive()) {
            return null;
        }

        return unsafe.getSpawnLocation();
    }

    public void resetSpawnLocation() {
        if (isActive()) {
            unsafe.resetSpawnLocation();
        }
    }

    public String getDisplayString(UUID uuid) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            return onlinePlayer.getName();
        }

        String foundName = unsafe.getName(uuid);
        if (foundName != null) {
            return foundName;
        }

        return uuid.toString();
    }

    public List<Profile> getProfiles(String nameOrId) {
        return unsafe.getProfiles(nameOrId);
    }

    public Profile getBestProfile(String nameOrId) {
        List<Profile> profiles = unsafe.getProfiles(nameOrId);

        if (profiles.size() == 1) {
            return profiles.get(0); //If there's only one premium player, xLogin only returns that one
        } else {
            return null;
        }
    }

    /**
     * Gets the xLogin profile for a given unique id, or null if there is no known profile for that
     * id.
     *
     * @param uuid the unique id
     * @return the profile or null
     */
    public Profile getProfile(UUID uuid) {
        return unsafe.getProfile(uuid);
    }

    public UUID getBestUniqueId(String nameOrId) {
        Profile profile = getBestProfile(nameOrId);
        return profile == null ? null : profile.getUniqueId();
    }

    /**
     * Finds the single matching profile for an input string. If there are no matches, a descriptive
     * exception is thrown. If there are too many matches, the sender is provided JSON chat links to
     * select the correct result.
     *
     * @param input          the input string, either a unique id or player name
     * @param sender         the sender to send messages to
     * @param commandBuilder a function that provides the command lines to execute for each profile
     * @return the single matching profile, never null
     * @throws UserException if the match count is different from one
     */
    public XLoginHook.Profile findSingleMatchingProfileOrFail(String input, CommandSender sender,
                                                              Function<Profile, String> commandBuilder) {
        List<XLoginHook.Profile> profiles = getProfiles(input);
        if (profiles.isEmpty()) {
            throw new UserException("Unbekannter Spieler: %s", input);
        } else if (profiles.size() > 1) {
            MessageType.USER_ERROR.sendTo(sender, "Mehrere Spieler zu diesem Kriterium gefunden:");
            profiles.forEach(profile -> sendProfileInfoTo(sender, profile, commandBuilder));
            throw new UserException("Bitte wähle oben den gewünschten Spieler aus.");
        }
        return Preconditions.checkNotNull(profiles.get(0), "profiles.get(0), for ", input);
    }

    /**
     * Sends a chat line to a sender, describing a given profile. Given function will be used to
     * convert the profile into a Minecraft command line that will be linked next to the profile
     * info.
     *
     * @param sender         the sender to send to
     * @param profile        the profile to display
     * @param commandBuilder the command line builder
     */
    public void sendProfileInfoTo(CommandSender sender, Profile profile, Function<Profile, String> commandBuilder) {
        ComponentSender.sendTo(
                new XyComponentBuilder("-➩ ", ChatColor.YELLOW).bold(true)
                        .append(profile.getName() + " (").color(ChatColor.GOLD).bold(false)
                        .append(formatPremiumStatus(profile)).color(getPremiumColor(profile))
                        .append(")").color(ChatColor.GOLD)
                        .append(" [Auswählen]").italic(true).color(ChatColor.GOLD)
                        .hintedCommand(commandBuilder.apply(profile)),
                sender
        );
    }

    @Nonnull
    private ChatColor getPremiumColor(Profile profile) {
        return profile.isPremium() ? ChatColor.GREEN : ChatColor.RED;
    }

    @Nonnull
    private String formatPremiumStatus(Profile profile) {
        return profile.isPremium() ? "Premium" : "Cracked";
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }

    public interface Profile {
        boolean isPremium();

        String getName();

        UUID getUniqueId();

        String getLastIp();
    }
}
