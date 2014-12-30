/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat.cmdspy;

import org.bukkit.entity.Player;

import java.util.function.BiPredicate;

/**
 * A simple implementation of CommandSpyFilter.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public abstract class SimpleCommandSpyFilter implements CommandSpyFilter {
    private final BiPredicate<String, Player> predicate;

    public SimpleCommandSpyFilter(BiPredicate<String, Player> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean matches(String command, Player sender) {
        return predicate.test(command, sender);
    }

}
