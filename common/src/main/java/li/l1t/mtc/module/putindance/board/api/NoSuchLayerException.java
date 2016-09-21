/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.board.api;

import li.l1t.mtc.api.exception.InternalException;

/**
 * Thrown if a layer does not exist in a board.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public class NoSuchLayerException extends InternalException {
    public NoSuchLayerException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "PutinDance: Spielfeldlayer nicht gefunden: ";
    }
}
