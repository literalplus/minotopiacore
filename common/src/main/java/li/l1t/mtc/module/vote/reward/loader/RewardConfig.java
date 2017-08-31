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

package li.l1t.mtc.module.vote.reward.loader;

import li.l1t.mtc.module.vote.api.reward.Reward;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Manages a single file containing reward configuration.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-12
 */
public class RewardConfig extends ManagedConfiguration {
    private static final String REWARDS_PATH = "rewards";
    private final String serviceName;
    private List<Reward> rewards = Collections.emptyList();

    protected RewardConfig(File file, String serviceName) {
        super(file);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        super.loadFromString(contents);
        rewards = getListChecked(REWARDS_PATH, Reward.class);
    }

    @Override
    public String saveToString() {
        set(REWARDS_PATH, rewards);
        return super.saveToString();
    }

    public List<Reward> getRewards() {
        return rewards;
    }
}
