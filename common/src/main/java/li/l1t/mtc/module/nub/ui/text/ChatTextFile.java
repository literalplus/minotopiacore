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

package li.l1t.mtc.module.nub.ui.text;

import li.l1t.common.util.StringHelper;
import li.l1t.mtc.api.chat.ChatConstants;
import li.l1t.mtc.api.chat.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract base class for text files storing long formatted chat messages.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public abstract class ChatTextFile {
    private List<String> lines = Collections.emptyList();

    public File defaultFile(Plugin plugin) {
        return new File(plugin.getDataFolder() + "/" + defaultFileName());
    }

    protected abstract String defaultFileName();

    public boolean tryLoadFromDefaultLocation(Plugin plugin) {
        try {
            loadFromDefaultLocation(plugin);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadFromDefaultLocation(Plugin plugin) throws IOException {
        File file = defaultFile(plugin);
        if (!file.exists()) {
            plugin.saveResource(defaultFileName(), false);
        }
        load(file);
    }

    public void load(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            this.lines = reader.lines()
                    .map(ChatConstants::convertCustomColorCodes)
                    .map(StringHelper::translateAlternateColorCodes)
                    .map(MessageType.RESULT_LINE::format)
                    .collect(Collectors.toList());
        }
    }

    public List<String> getLines() {
        return lines;
    }

    public void sendTo(CommandSender sender, int duration) {
        lines.stream()
                .map(line -> replaceMacros(sender, line, duration))
                .forEach(sender::sendMessage);
    }

    private String replaceMacros(CommandSender sender, String line, int duration) {
        return line
                .replaceAll("\\$name", sender.getName())
                .replaceAll("\\$duration", String.valueOf(duration));
    }
}
