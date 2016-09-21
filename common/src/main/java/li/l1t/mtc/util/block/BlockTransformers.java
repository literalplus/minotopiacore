/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

/**
 * Provides static utility methods to build block transformers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class BlockTransformers {
    private BlockTransformers() {

    }

    public static BasicBlockTransformerBuilder basic() {
        return new BasicBlockTransformerBuilder();
    }

    public static BasicFilteringBlockTransformerBuilder filtering() {
        return new BasicFilteringBlockTransformerBuilder();
    }

    public static BasicRevertableBlockTransformerBuilder revertableFiltering() {
        return new BasicRevertableBlockTransformerBuilder();
    }
}
