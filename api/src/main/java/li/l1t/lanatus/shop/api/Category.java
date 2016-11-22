/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api;

import li.l1t.common.misc.Identifiable;

/**
 * Represents a category of products that are shown in the Lanatus shop. Note that this concept is exclusive to the shop
 * and has no counterpart in Lanatus. This does not relate to modules.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-16-11
 */
public interface Category extends Identifiable {
    /**
     * @return the string specifying the icon of this category in inventories
     */
    String getIconName();

    void setIconName(String iconName);

    /**
     * @return the human-readable display name used by this category
     */
    String getDisplayName();

    void setDisplayName(String displayName);

    /***
     * @return the human-readable description for this category
     */
    String getDescription();

    void setDescription(String description);
}
