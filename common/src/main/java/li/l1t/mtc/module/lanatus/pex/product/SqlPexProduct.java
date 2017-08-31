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

package li.l1t.mtc.module.lanatus.pex.product;

import li.l1t.mtc.module.lanatus.base.product.AbstractProductMetadata;

import java.util.List;
import java.util.UUID;

/**
 * Represents a product's Lanatus-PEx specific metadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class SqlPexProduct extends AbstractProductMetadata implements PexProduct {
    private final List<String> commands;
    private String initialRank;
    private String targetRank;

    public SqlPexProduct(UUID productId, List<String> commands, String initialRank, String targetRank) {
        super(productId);
        this.commands = commands;
        this.initialRank = initialRank;
        this.targetRank = targetRank;
    }

    @Override
    public String getInitialRank() {
        return initialRank;
    }

    public void setInitialRank(String initialRank) {
        this.initialRank = initialRank;
    }

    @Override
    public String getTargetRank() {
        return targetRank;
    }

    public void setTargetRank(String targetRank) {
        this.targetRank = targetRank;
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }
}
