/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.util.checklist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;

import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.common.checklist.Checklist;
import io.github.xxyy.common.checklist.ChecklistEvaluator;
import io.github.xxyy.lib.intellij_annotations.NotNull;

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

    public ClickableChecklistItem(@NotNull String description, @NotNull ChecklistEvaluator evaluator) {
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
