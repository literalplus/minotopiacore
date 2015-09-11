package io.github.xxyy.mtc.module.showhomes;

import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * Shows additional information and proposed actions for the associated home when its hologram is clicked.
 *
 * @author Janmm14
 */
public class HomeInfoTouchHandler implements TouchHandler {

    @NotNull
    private final Home home;
    @NotNull
    private final Set<UUID> plrsToShow;

    public HomeInfoTouchHandler(@NotNull Home home, @NotNull Set<UUID> plrsToShow) {
        this.home = home;
        this.plrsToShow = plrsToShow;
    }

    @Override
    public void onTouch(Player player) {
        if (!plrsToShow.contains(player.getUniqueId())) {
            return;
        }//@formatter:off
        BaseComponent[] components = new ComponentBuilder("Home options: ")
                .color(GOLD)
                .append("[TELEPORT]")
                .color(GREEN)
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Klicken für ")
                                .color(GOLD)
                                .append("/homeutil tp " + home.getEssentialsPlayerData().getUuid() + ' ' + home.getName())
                                .color(RED)
                                .create()))
                .event(new ClickEvent(Action.SUGGEST_COMMAND,
                        "/jhu tp " + home.getEssentialsPlayerData().getUuid() + ' ' + home.getName()))

                .append(" ", FormatRetention.NONE)
                .append("[ALLE HOMES DES USERS]")
                .color(GOLD)
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Klicken für ")
                                .color(GOLD)
                                .append("/home " + home.getEssentialsPlayerData().getLastName() + ":") //that works also if the user is offline
                                .color(RED)
                                .create()))
                .event(new ClickEvent(Action.RUN_COMMAND, "/home " + home.getEssentialsPlayerData().getLastName() + ":"))

                .append(" ", FormatRetention.NONE)
                .append("[LÖSCHEN]")
                .color(RED)
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Klicken für ")
                                .color(GOLD)
                                .append("/homeutil delete " + home.getEssentialsPlayerData().getUuid() + ' ' + home.getName())
                                .color(RED)
                                .create()))
                .event(new ClickEvent(Action.SUGGEST_COMMAND,
                        "/jhu delete " + home.getEssentialsPlayerData().getUuid() + ' ' + home.getName()))

                .append(" ", FormatRetention.NONE)
                .append("[USERNAME]")
                .color(GOLD)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Benutzernamen kopieren: ")
                                .color(GOLD)
                                .append(home.getEssentialsPlayerData().getLastName())
                                .color(GREEN)
                                .create()))
                .event(new ClickEvent(Action.SUGGEST_COMMAND, home.getEssentialsPlayerData().getLastName()))

                .append(" ", FormatRetention.NONE)
                .append("[UUID]")
                .color(GOLD)
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("UUID kopieren: ")
                                .color(GOLD)
                                .append(home.getEssentialsPlayerData().getUuid().toString())
                                .color(GREEN)
                                .create()))
                .event(new ClickEvent(Action.SUGGEST_COMMAND, home.getEssentialsPlayerData().getUuid().toString()
                )).create();//@formatter:on
        player.spigot().sendMessage(components);
    }
}
