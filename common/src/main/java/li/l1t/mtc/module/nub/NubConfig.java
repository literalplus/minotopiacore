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
