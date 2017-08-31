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

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.checklist.Checklist;
import li.l1t.common.checklist.ChecklistEvaluator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;

import javax.annotation.Nonnull;

/**
 * An extension of a checklist item that provides additional methods for
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-07-11
 */
public class ClickableChecklistItem extends Checklist.Item {
    private String buttonCaption;
    private ClickEvent.Action action;
    private String argument;

    public ClickableChecklistItem(@Nonnull String description, @Nonnull ChecklistEvaluator evaluator) {
        super(description, evaluator);
    }

    public XyComponentBuilder render(LinkChecklistRenderer renderer) {
        boolean checked = isChecked();
        XyComponentBuilder builder = new XyComponentBuilder(renderer.render(this))
                .color(checked ? ChatColor.DARK_GREEN : ChatColor.GRAY);
        if (!checked) {
            builder.append(" ")
                    .append("[" + buttonCaption + "]", ChatColor.GOLD, ChatColor.UNDERLINE)
                    .event(new ClickEvent(action, argument))
                    .tooltip("Hier klicken, um", "diesen Eintrag", "abzuschließen");
        }

        return builder;
    }

    public String getButtonCaption() {
        return buttonCaption;
    }

    public ClickableChecklistItem withButtonCaption(String buttonCaption) {
        this.buttonCaption = buttonCaption;
        return this;
    }

    public ClickEvent.Action getAction() {
        return action;
    }

    public ClickableChecklistItem withAction(ClickEvent.Action action) {
        this.action = action;
        return this;
    }

    public String getArgument() {
        return argument;
    }

    public ClickableChecklistItem withArgument(String argument) {
        this.argument = argument;
        return this;
    }
}
