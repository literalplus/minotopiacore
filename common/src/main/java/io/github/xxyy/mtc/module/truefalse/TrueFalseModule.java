package io.github.xxyy.mtc.module.truefalse;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.module.ConfigurableMTCModule;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The root of all evil caused by the True-False event minigame thing.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 4.9.14
 */
public class TrueFalseModule extends ConfigurableMTCModule {
    public static final String MAGIC_WAND_NAME = "§3T/F Boundary Wand Melon";
    public static final Material MAGIC_WAND_MATERIAL = Material.MELON;
    public static final String NAME = "TrueFalse";
    private static final String SPAWN_PATH = "spawn";
    private static final String QUESTION_PATH = "questions";
    private static final String BOUNDARY_1_PATH = "boundaries.first";
    private static final String BOUNDARY_2_PATH = "boundaries.second";
    public static final String ADMIN_PERMISSION = "mtc.truefalse.admin";
    private List<TrueFalseQuestion> questions = new ArrayList<>();
    private XyLocation firstBoundary;
    private XyLocation secondBoundary;
    private XyLocation spawn;

    private TrueFalseGame game;
    List<UUID> boundarySessions = new ArrayList<>();

    public TrueFalseModule() {
        super(NAME, "modules/truefalse.conf.yml", ClearCacheBehaviour.SAVE);
        ConfigurationSerialization.registerClass(XyLocation.class); //Ensure the class is loaded - double call doesn't hurt (https://github.com/SpigotMC/Spigot-API/blob/master/src/main/java/org/bukkit/configuration/serialization/ConfigurationSerialization.java#L218)
        ConfigurationSerialization.registerClass(TrueFalseQuestion.class);
    }

    @Override
    public void enable(MTC plugin) {
        super.enable(plugin);

        plugin.getCommand("wahrfalsch").setExecutor(new CommandTrueFalse(this));
        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        spawn = (XyLocation) configuration.get(SPAWN_PATH);
        firstBoundary = (XyLocation) configuration.get(BOUNDARY_1_PATH);
        secondBoundary = (XyLocation) configuration.get(BOUNDARY_2_PATH);
        questions = (List<TrueFalseQuestion>) configuration.getList(QUESTION_PATH, questions);
    }

    public boolean hasQuestion() {
        return !questions.isEmpty();
    }

    public TrueFalseQuestion consumeQuestion() {
        if (hasQuestion()) {
            return questions.remove(0);
        }
        return null;
    }

    public XyLocation getSpawn() {
        return spawn;
    }

    public List<TrueFalseQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TrueFalseQuestion> questions) {
        this.questions = questions;
        configuration.set(QUESTION_PATH, questions);
        save();
    }

    public XyLocation getFirstBoundary() {
        return firstBoundary;
    }

    public void setFirstBoundary(XyLocation firstBoundary) {
        this.firstBoundary = firstBoundary;
        configuration.set(BOUNDARY_1_PATH, firstBoundary);
        save();
    }

    public XyLocation getSecondBoundary() {
        return secondBoundary;
    }

    public void setSecondBoundary(XyLocation secondBoundary) {
        this.secondBoundary = secondBoundary;
        configuration.set(BOUNDARY_2_PATH, secondBoundary);
        save();
    }

    public void setSpawn(XyLocation spawn) {
        this.spawn = spawn;
        configuration.set(SPAWN_PATH, spawn);
        save();
    }

    public boolean isGameOpen() {
        return game != null && game.getState().equals(TrueFalseGame.State.TELEPORT);
    }

    public TrueFalseGame getGame() {
        return game;
    }

    public void setGame(TrueFalseGame game) {
        this.game = game;
    }

    private class EventListener implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent evt) {
            ItemStack item = evt.getPlayer().getItemInHand();
            if (!boundarySessions.contains(evt.getPlayer().getUniqueId()) ||
                    (evt.getAction() != Action.RIGHT_CLICK_BLOCK && evt.getAction() !=  Action.LEFT_CLICK_BLOCK) ||
                    item == null || item.getType() != MAGIC_WAND_MATERIAL) {
                return;
            }

            ItemMeta meta = item.getItemMeta();
            if (item.hasItemMeta() && meta.hasDisplayName() &&
                    meta.getDisplayName().equals(MAGIC_WAND_NAME)) {
                if (evt.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    setSecondBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
                    evt.getPlayer().sendMessage("§aZweiter Eckpunkt gesetzt!");
                    evt.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                } else {
                    setFirstBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
                    evt.getPlayer().sendMessage("§aErster Eckpunkt gesetzt!");
                }

                evt.setCancelled(true);
            }
        }
    }
}
