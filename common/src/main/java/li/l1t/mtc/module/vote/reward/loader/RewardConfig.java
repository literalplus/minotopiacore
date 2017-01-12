/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
