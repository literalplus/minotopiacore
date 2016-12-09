/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub;

import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.nub.ui.text.NubIntro;
import li.l1t.mtc.module.nub.ui.text.NubOutro;
import li.l1t.mtc.yaml.ManagedConfiguration;

/**
 * Handles the configuration for the N.u.b. module.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-08
 */
public class NubConfig {
    private static final String PROTECTION_LENGTH_PATH = "protection-duration-minutes";
    private int protectionDurationMinutes = 30;
    private final NubIntro intro;
    private final NubOutro outro;

    @InjectMe
    public NubConfig(NubIntro intro, NubOutro outro) {
        this.intro = intro;
        this.outro = outro;
    }

    void loadFrom(NubModule module) {
        ManagedConfiguration config = module.getConfiguration();
        config.options().copyDefaults(true);
        config.addDefault(PROTECTION_LENGTH_PATH, protectionDurationMinutes);
        protectionDurationMinutes = config.getInt(PROTECTION_LENGTH_PATH);
        module.save();
        intro.tryLoadFromDefaultLocation(module.getPlugin());
        outro.tryLoadFromDefaultLocation(module.getPlugin());
    }

    public int getProtectionDurationMinutes() {
        return protectionDurationMinutes;
    }

    public NubIntro getIntro() {
        return intro;
    }

    public NubOutro getOutro() {
        return outro;
    }
}
