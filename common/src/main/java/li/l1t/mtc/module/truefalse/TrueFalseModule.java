/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.truefalse;

import com.google.common.base.Preconditions;
import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public static final String ADMIN_PERMISSION = "mtc.truefalse.admin";
    private static final String SPAWN_PATH = "spawn";
    private static final String QUESTION_PATH = "questions";
    private static final String BOUNDARY_1_PATH = "boundaries.first";
    private static final String BOUNDARY_2_PATH = "boundaries.second";
    private final TrueFalseWandListener wandListener = new TrueFalseWandListener(this);
    private TrueFalseExploitListener exploitListener = null;
    private Set<UUID> boundarySessions = new HashSet<>();
    private List<TrueFalseQuestion> questions = new ArrayList<>();
    private XyLocation firstBoundary;
    private XyLocation secondBoundary;
    private XyLocation spawn;
    private TrueFalseGame game;

    public TrueFalseModule() {
        super(NAME, "modules/truefalse.conf.yml", ClearCacheBehaviour.SAVE, false);
        ConfigurationSerialization.registerClass(XyLocation.class); //Ensure the class is loaded - double call doesn't hurt (https://github.com/SpigotMC/Spigot-API/blob/master/src/main/java/org/bukkit/configuration/serialization/ConfigurationSerialization.java#L218)
        ConfigurationSerialization.registerClass(XyLocation.class, "io.github.xxyy.common.misc.XyLocation"); //TODO: temporary
        ConfigurationSerialization.registerClass(TrueFalseQuestion.class);
        ConfigurationSerialization.registerClass(TrueFalseQuestion.class, "io.github.xxyy.mtc.module.truefalse.TrueFalseQuestion");
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        registerCommand(new CommandTrueFalse(this), "wahrfalsch", "wf", "dummefragen");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadImpl() {
        spawn = (XyLocation) configuration.get(SPAWN_PATH);
        firstBoundary = (XyLocation) configuration.get(BOUNDARY_1_PATH);
        secondBoundary = (XyLocation) configuration.get(BOUNDARY_2_PATH);
        questions = (List<TrueFalseQuestion>) configuration.getList(QUESTION_PATH, questions);
    }

    @Override
    public void save() {
        configuration.set(SPAWN_PATH, spawn);
        configuration.set(BOUNDARY_1_PATH, firstBoundary);
        configuration.set(BOUNDARY_2_PATH, secondBoundary);
        configuration.set(QUESTION_PATH, questions);
        super.save();
    }

    public boolean hasQuestion() {
        return !questions.isEmpty();
    }

    public TrueFalseQuestion consumeQuestion() {
        if (hasQuestion()) {
            return questions.remove(0);
        }
        save();
        return null;
    }

    public XyLocation getSpawn() {
        return spawn;
    }

    public void setSpawn(XyLocation spawn) {
        this.spawn = spawn;
        save();
    }

    public List<TrueFalseQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TrueFalseQuestion> questions) {
        this.questions = questions;
        save();
    }

    public XyLocation getFirstBoundary() {
        return firstBoundary;
    }

    public void setFirstBoundary(XyLocation firstBoundary) {
        this.firstBoundary = firstBoundary;
        save();
    }

    public XyLocation getSecondBoundary() {
        return secondBoundary;
    }

    public void setSecondBoundary(XyLocation secondBoundary) {
        this.secondBoundary = secondBoundary;
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

    public void startGame() {
        Preconditions.checkState(game != null, "no game!");
        Preconditions.checkState(game.getState() == TrueFalseGame.State.TELEPORT, "must be in teleport state!");

        if (exploitListener == null) {
            exploitListener = new TrueFalseExploitListener(this);
            getPlugin().getServer().getPluginManager().registerEvents(exploitListener, getPlugin());
        }
        getGame().start();
    }

    public boolean abortGame() {
        Preconditions.checkState(game != null, "no game!");

        if (exploitListener != null) {
            HandlerList.unregisterAll(exploitListener);
            exploitListener = null;
        }
        return getGame().abort();
    }

    public boolean hasBoundarySession(UUID uuid) {
        return boundarySessions.contains(uuid);
    }

    public void startBoundarySession(UUID uuid) {
        if (boundarySessions.isEmpty()) {
            plugin.getServer().getPluginManager().registerEvents(wandListener, plugin);
        }
        boundarySessions.add(uuid);
    }

    public void endBoundarySession(UUID uuid) {
        boundarySessions.remove(uuid);
        if (boundarySessions.isEmpty()) {
            HandlerList.unregisterAll(wandListener);
        }
    }
}
