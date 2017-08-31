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

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.common.util.task.ImprovedBukkitRunnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base class for completable tasks that provide a future of a collection of import users.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-08
 */
public abstract class AbstractPexImportTask extends ImprovedBukkitRunnable implements CompletableTask<Collection<PexImportUser>> {
    private final CompletableFuture<Collection<PexImportUser>> future = new CompletableFuture<>();

    @Override
    public CompletableFuture<Collection<PexImportUser>> getFuture() {
        return future;
    }
}
