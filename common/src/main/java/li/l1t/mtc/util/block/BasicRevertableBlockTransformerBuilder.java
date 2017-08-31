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

import org.bukkit.block.Block;

import java.util.function.Predicate;

/**
 * A build for filtering block transformers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class BasicRevertableBlockTransformerBuilder extends AbstractBlockTransformerBuilder
        <BasicRevertableBlockTransformer, BasicRevertableBlockTransformerBuilder> {
    private Predicate<Block> filter;

    @Override
    public BasicRevertableBlockTransformer build() {
        BasicRevertableBlockTransformer instance = super.build();
        instance.setFilter(filter);
        return instance;
    }

    public BasicRevertableBlockTransformerBuilder withFilter(Predicate<Block> filter) {
        this.filter = filter;
        return self();
    }

    @Override
    protected BasicRevertableBlockTransformer newInstance() {
        return new BasicRevertableBlockTransformer(firstBoundary, secondBoundary);
    }

    @Override
    protected BasicRevertableBlockTransformerBuilder self() {
        return this;
    }
}
