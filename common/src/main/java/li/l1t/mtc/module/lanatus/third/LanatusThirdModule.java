/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
