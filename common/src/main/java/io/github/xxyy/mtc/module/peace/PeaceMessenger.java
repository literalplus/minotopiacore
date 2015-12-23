package io.github.xxyy.mtc.module.peace;

import io.github.xxyy.common.chat.XyComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Handles peace messages and repeatly sends request notifications
 *
 * @author Janmm14
 */
public class PeaceMessenger {

    @NotNull
    private final PeaceModule module;

    public PeaceMessenger(@NotNull PeaceModule module) {
        this.module = module;
    }

    /**
     * Notifies the initiator the request is sent
     *
     * @param initiator the {@link PeaceInfo} of the one sent the request
     * @param target    the uuid of the one targetted by the peace request
     */
    public void notifyRequestSent(Player initiator, UUID target) {
        String targetName = module.getPlugin().getXLoginHook().getDisplayString(target);

        initiator.spigot().sendMessage((BaseComponent[]) ArrayUtils
            .addAll(
                TextComponent.fromLegacyText(module.getPlugin().getChatPrefix()),
                new XyComponentBuilder("")
                    .append("Du hast eine Friedensanfrage an ", ChatColor.GOLD)
                    .append(targetName, ChatColor.AQUA)
                    .append(" geschickt. ", ChatColor.GOLD)
                    .append("[Zurückziehen]", ChatColor.RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new XyComponentBuilder("")
                        .append("Friedensanfrage zurückziehen?", ChatColor.GOLD)
                        .create()))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/frieden zurückziehen " + target))
                    .create()));
        //TODO implement
    }

    /**
     * Notifies the target that he got a request if it is online
     *
     * @param from   the {@link PeaceInfo} of the one sent the request
     * @param target the uuid of the one targetted by the peace request
     */
    public void notifyRequestGot(UUID from, Player target) {
        String fromName = module.getPlugin().getXLoginHook().getDisplayString(from);

        target.spigot().sendMessage((BaseComponent[]) ArrayUtils
            .addAll(
                TextComponent.fromLegacyText(module.getPlugin().getChatPrefix()),
                new XyComponentBuilder("")
                    .append(fromName, ChatColor.AQUA)
                    .append(" hat dir eine Friedensanfrage geschickt. Annehmen? ", ChatColor.GOLD)
                    .append("[Ja]", ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new XyComponentBuilder("")
                            .append("Klicken, um mit ", ChatColor.GOLD)
                            .append(fromName, ChatColor.AQUA)
                            .append(" Frieden zu schließen.", ChatColor.GOLD)
                            .create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/frieden ja " + from))
                    .append(" ", FormatRetention.NONE)
                    .append("[Nein]", ChatColor.RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new XyComponentBuilder("")
                            .append("Klicken, um ", ChatColor.GOLD)
                            .append("nicht", ChatColor.RED)
                            .append(" mit ", ChatColor.GOLD)
                            .append(fromName, ChatColor.AQUA)
                            .append(" Frieden zu schließen.", ChatColor.GOLD)
                            .create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/frieden nein " + from))
                    .create()));
    }

    @NotNull
    public PeaceModule getModule() {
        return module;
    }
}
