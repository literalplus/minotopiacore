package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Shows additional information and proposed actions about/with the according home when its hologram is clicked.
 *
 * @author Janmm14
 */
@RequiredArgsConstructor
public class HomeInfoTouchHandler implements TouchHandler {

    @NonNull
    private final Home home;
    @NonNull
    private final Set<UUID> plrsToShow;

    @Override
    public void onTouch(Player player) {
        if (!plrsToShow.contains(player.getUniqueId())) {
            return;
        }//@formatter:off
        BaseComponent[] components = new ComponentBuilder("Home options: ")
            .color(ChatColor.GOLD)
            .append("[TELEPORT]")
            .color(ChatColor.GREEN)
            .event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Klicken für ")
                    .color(ChatColor.GOLD)
                    .append("/homeutil tp " + home.getEssentialsDataUser().getUuid() + ' ' + home.getName())
                    .color(ChatColor.RED)
                    .create()))
            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                "/homeutil tp " + home.getEssentialsDataUser().getUuid() + ' ' + home.getName()))

            .append(" ").reset()
            .append("[ALLE HOMES DES USERS]")
            .color(ChatColor.GOLD)
            .event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Klicken für ")
                    .color(ChatColor.GOLD)
                    .append("/home " + home.getEssentialsDataUser().getLastName() + ":") //that works also if the user is offline
                    .color(ChatColor.RED)
                    .create()))
            .event(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/home " + home.getEssentialsDataUser().getLastName() + ":"))

            .append(" ").reset()
            .append("[LÖSCHEN]")
            .color(ChatColor.RED)
            .event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Klicken für ")
                    .color(ChatColor.GOLD)
                    .append("/homeutil delete " + home.getEssentialsDataUser().getUuid() + ' ' + home.getName())
                    .color(ChatColor.RED)
                    .create()))
            .event(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                "/homeutil delete " + home.getEssentialsDataUser().getUuid() + ' ' + home.getName()))

            .append(" ").reset()
            .append("[USERNAME]")
            .color(ChatColor.GOLD)
            .event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Füge den Usernamen in den Chat ein: ")
                    .color(ChatColor.GOLD)
                    .append(home.getEssentialsDataUser().getLastName())
                    .color(ChatColor.GREEN)
                    .create()))
            .event(
                new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, home.getEssentialsDataUser().getLastName()))

            .append(" ").reset()
            .append("[UUID]")
            .color(ChatColor.GOLD)
            .event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Füge die User-UUID in den Chat ein: ")
                    .color(ChatColor.GOLD)
                    .append(home.getEssentialsDataUser().getUuid().toString())
                    .color(ChatColor.GREEN)
                    .create()))
            .event(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                home.getEssentialsDataUser().getUuid().toString()
            )).create();//@formatter:on
        player.spigot().sendMessage(components);
    }
}
