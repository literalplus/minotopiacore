/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import li.l1t.common.misc.XyLocation;
import org.bukkit.block.Block;

import java.util.function.Predicate;

/**
 * A block transformer that only transforms blocks matching a predicate.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BasicFilteringBlockTransformer extends BasicBlockTransformer implements FilteringBlockTransformer {
    private Predicate<Block> filter;

    BasicFilteringBlockTransformer(XyLocation firstBoundary, XyLocation secondBoundary) {
        super(firstBoundary, secondBoundary);
    }

    public void setFilter(Predicate<Block> filter) {
        this.filter = filter;
    }

    @Override
    protected boolean processSingleBlock(Block block) {
        return filter.test(block) && super.processSingleBlock(block);
    }

    @Override
    public Predicate<Block> getSourceFilter() {
        return filter;
    }
}
