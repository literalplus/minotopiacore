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

package li.l1t.mtc.helper;

import li.l1t.mtc.MTC;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


public final class LaterMessageHelper {
    public static final File STORAGE_FILE = new File("plugins/" + MTC.instance().getName() + "/laterMessages.stor.yml");
    public static YamlConfiguration STORAGE_YAML = YamlConfiguration.loadConfiguration(LaterMessageHelper.STORAGE_FILE);

    private LaterMessageHelper() {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void addMessage(String senderName, String type, int type2, String msg, boolean delOnFirstRead, boolean sendMTCPrefix) {
        int i = 0;
        while (LaterMessageHelper.STORAGE_YAML.contains(senderName + "." + i)) {
            i++;
        }
        LaterMessageHelper.STORAGE_YAML.set(senderName + "." + i + ".type", type);
        LaterMessageHelper.STORAGE_YAML.set(senderName + "." + i + ".type2", type2);
        LaterMessageHelper.STORAGE_YAML.set(senderName + "." + i + ".message", msg);
        LaterMessageHelper.STORAGE_YAML.set(senderName + "." + i + ".deleteonfirstread", delOnFirstRead);
        LaterMessageHelper.STORAGE_YAML.set(senderName + "." + i + ".prefix", sendMTCPrefix);
        LaterMessageHelper.save();
    }

    public static boolean hasMessages(String senderName) {
        return LaterMessageHelper.STORAGE_YAML.contains(senderName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void removeType(String senderName, String type, int type2) {
        if (!LaterMessageHelper.STORAGE_YAML.contains(senderName)) {
            return;
        }
        boolean anyLeft = false;
        int i = 0;
        while (LaterMessageHelper.STORAGE_YAML.contains(senderName + "." + i)) {
            if (!type.equalsIgnoreCase(LaterMessageHelper.STORAGE_YAML.getString(senderName + "." + i + ".type", "NOOO"))) {
                anyLeft = true;
                continue;
            }
            if (type2 != LaterMessageHelper.STORAGE_YAML.getInt(senderName + "." + i + ".type2", -1234)) {
                anyLeft = true;
                continue;
            }
            LaterMessageHelper.STORAGE_YAML.set(senderName + "." + i, null);
        }
        if (!anyLeft) {
            LaterMessageHelper.STORAGE_YAML.set(senderName, null);
        }
        LaterMessageHelper.save();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sends all pending messages and, if specified, deletes them.
     *
     * @param sender CommandSender to send to
     * @return If any messages got printed
     */
    public static boolean sendMessages(CommandSender sender) {
        String senderName = sender.getName();
        if (!LaterMessageHelper.STORAGE_YAML.contains(senderName)) {
            return false;
        }
        int i = 0;
        boolean anyLeft = false;
        if (!LaterMessageHelper.STORAGE_YAML.contains(senderName + "." + i)) {
            return false;
        }
        while (LaterMessageHelper.STORAGE_YAML.contains(senderName + "." + i)) {
            MTCHelper.sendLoc(LaterMessageHelper.STORAGE_YAML.getString(senderName + "." + i + ".message", "XU-undefined"), sender, LaterMessageHelper.STORAGE_YAML.getBoolean(senderName + "." + i + ".prefix", true));
            if (LaterMessageHelper.STORAGE_YAML.getBoolean(senderName + "." + i + ".deleteonfirstread", false)) {
                LaterMessageHelper.STORAGE_YAML.set(senderName + "." + i, null);
            } else {
                anyLeft = true;
            }
            i++;
        }
        if (!anyLeft) {
            LaterMessageHelper.STORAGE_YAML.set(senderName, null);
        }
        LaterMessageHelper.save();
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void save() {
        try {
            LaterMessageHelper.STORAGE_YAML.save(LaterMessageHelper.STORAGE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            //oh well
        }
    }

}
/*
xxyy98:
  0:
    type: <String> (i.e. C 4 clan)
    type2: <int> (i.e. 1 4 removedmsg)
    message: <String> (will be localized!)
    deleteonfirstread: <boolean>
    prefix: <boolean>
*/
