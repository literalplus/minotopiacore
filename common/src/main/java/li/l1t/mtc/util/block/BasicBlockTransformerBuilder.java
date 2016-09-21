/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

/**
 * Builds basic block transformers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class BasicBlockTransformerBuilder extends AbstractBlockTransformerBuilder
        <BlockTransformer, BasicBlockTransformerBuilder> {
    @Override
    protected BasicBlockTransformer newInstance() {
        return new BasicBlockTransformer(firstBoundary, secondBoundary);
    }

    @Override
    protected BasicBlockTransformerBuilder self() {
        return this;
    }
}
