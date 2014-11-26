/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.truefalse;

import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.helper.MTCHelper;

import java.util.List;

/**
 * Manages a game of true/false.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 4.9.14
 */
public class TrueFalseGame {
    private final List<Material> floorMaterials = Lists.newArrayList(Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.CARPET);
    private final TrueFalseModule module;
    private TrueFalseQuestion currentQuestion;
    private State state = State.TELEPORT;

    public TrueFalseGame(TrueFalseModule module) {
        this.module = module;
        Bukkit.broadcastMessage(MTCHelper.loc("XU-tfnew", false));
    }

    public void start() {
        Validate.isTrue(state == State.TELEPORT, "Expected TELEPORT state, got: ", state);
        nextQuestion();
        state = State.WAITING;
    }

    public void addParticipant(Player plr) {
        MTCHelper.sendLoc("XU-tfjoin", plr, false);
        plr.teleport(module.getSpawn());
    }

    public boolean nextQuestion() {
        Validate.isTrue(currentQuestion == null, "Cannot override question!");
        if (module.hasQuestion()) {
            setQuestion(module.consumeQuestion());
            return true;
        }
        CommandHelper.broadcast(MTCHelper.loc("XU-tfnq", false), TrueFalseModule.ADMIN_PERMISSION);
        return false;
    }

    public TrueFalseQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public void setQuestion(TrueFalseQuestion question) {
        Validate.isTrue(currentQuestion == null, "Cannot override question!");
        DyeColor colorToRemove = question.getAnswer() ? DyeColor.RED : DyeColor.GREEN;
        @SuppressWarnings("deprecation") BlockReplacer blockReplacer = new BlockReplacer(
                b -> floorMaterials.contains(b.getType()) && b.getState().getData().getData() == colorToRemove.getWoolData(),
                b -> b.setType(Material.AIR),
                b -> {
                    b.setType(Material.WOOL);
                    ((Wool) b.getState().getData()).setColor(colorToRemove);
                    b.getState().update(true);
                },
                module.getFirstBoundary(), module.getSecondBoundary(), 200
        );

        Bukkit.broadcastMessage(MTCHelper.locArgs("XU-tfqann", "CONSOLE", false, question.getText()));

        module.getPlugin().getServer().getScheduler().runTaskLater(module.getPlugin(), () -> {
            blockReplacer.scheduleTransform(module.getPlugin());
            Bukkit.broadcastMessage(MTCHelper.loc("XU-tf" + (question.getAnswer() ? "true" : "false"), false));
            module.getPlugin().getServer().getScheduler().runTaskLater(module.getPlugin(), () -> {
                blockReplacer.scheduleRevert(module.getPlugin());
                state = State.READY;
                currentQuestion = null;
            }, 5 * 20L);
            state = State.FINISHED;
        }, 15 * 20L);

        currentQuestion = question;
        state = State.WAITING;
    }

    public boolean abort() {
        if(state == State.WAITING) {
            module.getPlugin().getServer().getScheduler().runTaskLater(module.getPlugin(), this::abort, 15 * 20L);
            return false;
        } else {
            Bukkit.broadcastMessage(MTCHelper.loc("XU-tfabort2", false));
            module.setGame(null);
            return true;
        }
    }

    public State getState() {
        return state;
    }

    public enum State {
        TELEPORT, READY, WAITING, FINISHED
    }
}
