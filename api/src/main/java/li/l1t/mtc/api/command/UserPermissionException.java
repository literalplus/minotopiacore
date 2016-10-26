/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.command;

import com.google.common.base.Preconditions;
import li.l1t.common.exception.UserException;
import org.bukkit.permissions.Permissible;

/**
 * Thrown if the executor of a command does not have permission for something.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
public class UserPermissionException extends UserException {
    public UserPermissionException(String messagePattern, Object... params) {
        super(messagePattern, params);
    }

    public static void checkPermission(Permissible target, String permission, String messagePattern, Object... params) {
        Preconditions.checkNotNull(target, "target");
        Preconditions.checkNotNull(permission, "permission");
        if (!target.hasPermission(permission)) {
            throw new UserPermissionException(messagePattern, params);
        }
    }

    public static void checkPermission(Permissible target, String permission) {
        checkPermission(target, permission, "Du bist nicht autorisiert, dies zu tun. (%s)", permission);
    }
}
