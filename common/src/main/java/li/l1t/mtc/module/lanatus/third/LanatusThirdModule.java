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

package li.l1t.mtc.module.lanatus.third;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.MTCModuleAdapter;
import li.l1t.mtc.module.lanatus.third.listener.PostPurchaseCommandExecutionListener;
import li.l1t.mtc.module.lanatus.third.product.SqlThirdProductRepository;
import li.l1t.mtc.module.lanatus.third.product.ThirdProductRepository;

/**
 * A module that allows third-party products to be bought via Lanatus by executing commands.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-05-12
 */
public class LanatusThirdModule extends MTCModuleAdapter {
    public static final String NAME = "LanatusThird";
    public static final String MODULE_NAME = "mtc-la3";
    @InjectMe
    private SqlThirdProductRepository thirdProductRepository;

    public LanatusThirdModule() {
        super(NAME, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerListener(new PostPurchaseCommandExecutionListener(this));
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        if (forced) {
            thirdProductRepository.clearCache();
        }
    }

    public ThirdProductRepository thirdProducts() {
        return thirdProductRepository;
    }
}
