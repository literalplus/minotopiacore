/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.category;

import li.l1t.lanatus.shop.api.Category;

import java.util.UUID;

/**
 * Represents a shop category backed by a SQL database.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-17-11
 */
public class SqlCategory implements Category {
    private final UUID uniqueId;
    private String iconName;
    private String displayName;
    private String description;

    public SqlCategory(UUID uniqueId, String iconName, String displayName, String description) {
        this.uniqueId = uniqueId;
        this.iconName = iconName;
        this.displayName = displayName;
        this.description = description;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getIconName() {
        return iconName;
    }

    @Override
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
