/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.util.block;

import com.google.common.base.Preconditions;
import li.l1t.common.misc.XyLocation;
import org.bukkit.block.Block;

import java.util.function.Consumer;

/**
 * Provides a fluent builder interface for block transformers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
abstract class AbstractBlockTransformerBuilder
        <T extends BlockTransformer, B extends AbstractBlockTransformerBuilder<T, B>> {
    protected XyLocation firstBoundary;
    protected XyLocation secondBoundary;
    protected int blocksPerTick = Integer.MAX_VALUE;
    protected Consumer<Block> transformer;

    public AbstractBlockTransformerBuilder() {
    }

    public B withLocations(XyLocation from, XyLocation to) {
        this.firstBoundary = from;
        this.secondBoundary = to;
        return self();
    }

    public B withBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = blocksPerTick;
        return self();
    }

    public B withTransformer(Consumer<Block> transformer) {
        this.transformer = transformer;
        return self();
    }

    public T build() {
        Preconditions.checkState(firstBoundary != null && secondBoundary != null, "need boundary locations");
        Preconditions.checkState(transformer != null, "need transformer");
        T instance = newInstance();
        instance.setBlocksPerTick(blocksPerTick);
        instance.setTransformerFunction(transformer);
        return instance;
    }

    protected abstract T newInstance();

    protected abstract B self();
}
