/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.ui.text;

/**
 * Stores and distributes the protection end message for new players.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class NubOutro extends ChatTextFile {
    private static final String OUTRO_FILE_NAME = "nub_outro.chat.txt";

    @Override
    protected String defaultFileName() {
        return "/modules/nub/" + OUTRO_FILE_NAME;
    }
}
