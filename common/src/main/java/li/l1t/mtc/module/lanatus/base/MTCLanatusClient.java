/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.base;

import li.l1t.common.sql.sane.SaneSql;
import li.l1t.lanatus.sql.SqlLanatusClient;
import li.l1t.mtc.api.module.inject.InjectMe;

/**
 * MTC extension of the SQL Lanatus Client, compatible with Dependency Injection.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-25
 */
public class MTCLanatusClient extends SqlLanatusClient {
    @InjectMe
    public MTCLanatusClient(SaneSql sql) {
        super(sql, "mtc-common");
    }
}
