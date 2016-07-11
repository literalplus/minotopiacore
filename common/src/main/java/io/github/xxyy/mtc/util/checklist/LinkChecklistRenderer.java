/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.util.checklist;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.common.checklist.Checklist;
import io.github.xxyy.common.checklist.renderer.AbstractRenderer;
import io.github.xxyy.common.checklist.renderer.CheckmarkBasedRenderer;

/**
 * A checklist renderer that also displays a clickable button to complete
 * the item. Quick-and-dirty implementation over existing API that needs a redesign.
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
