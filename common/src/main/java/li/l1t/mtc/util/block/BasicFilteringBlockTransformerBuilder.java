/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import org.bukkit.block.Block;

import java.util.function.Predicate;

/**
 * A build for filtering block transformers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class BasicFilteringBlockTransformerBuilder extends AbstractBlockTransformerBuilder
        <BasicFilteringBlockTransformer, BasicFilteringBlockTransformerBuilder> {
    private Predicate<Block> filter;

    @Override
    public BasicFilteringBlockTransformer build() {
        BasicFilteringBlockTransformer instance = super.build();
        instance.setFilter(filter == null ? any -> true : filter);
        return instance;
    }

    public BasicFilteringBlockTransformerBuilder withFilter(Predicate<Block> filter) {
        this.filter = filter;
        return self();
    }

    @Override
    protected BasicFilteringBlockTransformer newInstance() {
        return new BasicFilteringBlockTransformer(firstBoundary, secondBoundary);
    }

    @Override
    protected BasicFilteringBlockTransformerBuilder self() {
        return this;
    }
}
