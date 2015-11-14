package io.github.xxyy.mtc.hook.pex;

import java.util.Collection;

/**
 * Represents a group from the Permissionsex plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-14
 */
public interface PexGroup {
    /**
     * @return the name of this group
     */
    String getName();

    /**
     * @return this group's chat prefix
     */
    String getPrefix();

    /**
     * @return a collection of this group's members
     */
    Collection<PexUser> getUsers();

    /**
     * Gets an option's boolean value for this group in a specific world.
     *
     * @param name  the name of the option to check
     * @param world the world, or null to check for global options
     * @param def   the default value, returned if the option is not set
     * @return the boolean option with given name or the default if is not set
     */
    boolean getOptionBoolean(String name, String world, boolean def);

    /**
     * Gets an option's integer value for this group in a specific world.
     *
     * @param name  the name of the option to check
     * @param world the world, or null to check for global options
     * @param def   the default value, returned if the option is not set
     * @return the integer option with given name or the default if is not set
     */
    int getOptionInteger(String name, String world, int def);
}
