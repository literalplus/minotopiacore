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

package li.l1t.mtc.util.checklist;

import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.checklist.Checklist;
import li.l1t.common.checklist.renderer.AbstractRenderer;
import li.l1t.common.checklist.renderer.CheckmarkBasedRenderer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * A checklist renderer that also displays a clickable button to complete the item. Quick-and-dirty
 * implementation over existing API that needs a redesign.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-11
 */
public class LinkChecklistRenderer extends AbstractRenderer {
    private final CheckmarkBasedRenderer proxy;

    public LinkChecklistRenderer(CheckmarkBasedRenderer proxy) {
        this.proxy = proxy;
    }

    @Override
    public StringBuilder render(StringBuilder sb, Checklist.Item item) {
        return proxy.render(sb, item);
    }

    public void renderTo(CommandSender sender, Checklist checklist) {
        checklist.getItems().stream()
                .forEach(item -> renderSingle(sender, item));
    }

    public void renderSingle(CommandSender sender, Checklist.Item item) {
        if (item instanceof ClickableChecklistItem) {
            ComponentSender.sendTo(((ClickableChecklistItem) item).render(this), sender);
        } else {
            ComponentSender.sendTo(
                    new XyComponentBuilder(render(new StringBuilder(), item).toString())
                            .color(item.isChecked() ? ChatColor.DARK_GREEN : ChatColor.GRAY),
                    sender
            );
        }
    }
}
