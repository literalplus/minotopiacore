/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

/**
 * A task for transforming a large amount of blocks in conjunction with a BlockTransformer.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BlockTransformTask extends AbstractTransformTask {
    private final BasicBlockTransformer blockTransformer;

    public BlockTransformTask(BasicBlockTransformer blockTransformer) {
        this.blockTransformer = blockTransformer;
    }

    @Override
    public void run() {
        boolean done = blockTransformer.continueIteration();
        if (done) {
            setDoneAndCancel();
        }
    }

}
