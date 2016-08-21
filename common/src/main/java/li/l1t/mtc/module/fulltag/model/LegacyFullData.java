/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.fulltag.model;

import li.l1t.common.lib.com.mojang.api.profiles.Profile;
import li.l1t.common.util.UUIDHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a legacy dataset storing information about a legacy full item.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-19
 */
public class LegacyFullData {
    private final LegacyFullDataRepository repository;
    private final int id;
    private final String senderName;
    private final String receiverName;
    private final UUID currentReceiverId;
    private final String comment;
    private final long createdTimeUnix; //Unix seconds
    private final boolean thorns;
    private final int partId;

    protected LegacyFullData(LegacyFullDataRepository repository, int id, String senderName, String receiverName,
                             UUID currentReceiverId, String comment, long createdTimeUnix, boolean thorns, int partId) {
        this.repository = repository;
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.currentReceiverId = currentReceiverId;
        this.comment = comment;
        this.createdTimeUnix = createdTimeUnix;
        this.thorns = thorns;
        this.partId = partId;
    }

    public int getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public long getCreatedTimeUnix() {
        return createdTimeUnix;
    }

    public boolean isThorns() {
        return thorns;
    }

    public String getComment() {
        return comment;
    }

    /**
     * Converts this data into the current version of the full data storage format. Note that this
     * does indeed create the returned data in the current database, but does not delete this legacy
     * data.
     *
     * @param repository the repository to create the new data in
     * @return the created data
     * @throws IllegalStateException if this data is invalid or a database error occurs
     */
    public FullData toFullData(FullDataRepository repository) throws IllegalStateException {
        UUID senderId = findUUID(senderName, "Sender"); //checks for null and throws ISE
        if (partId < 0 || partId >= FullPart.values().length) {
            throw new IllegalStateException("Invalid part id: " + partId);
        }

        return repository.create(
                String.format("FullReturn{create=%s,sender=%s, rec=%s, comm=%s}",
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
                        senderName, receiverName, comment),
                senderId,
                currentReceiverId,
                FullPart.values()[partId],
                thorns
        );
    }

    private UUID findUUID(String userName, String errorIdentifier) throws IllegalStateException {
        Profile senderProfile = repository.getProfileRepository()
                .findProfileAtTime(senderName, createdTimeUnix);
        if (senderProfile == null) {
//            throw new IllegalStateException(String.format("%s is unknown to Mojang: %s @ %d",
//                    errorIdentifier, senderName, createdTimeUnix));
            return UUIDHelper.getOfflineUUID(userName);
        }
        return senderProfile.getUniqueId();
    }

    public int getPartId() {
        return partId;
    }

    /**
     * @return a humand-readable representation of this item's part id or an error message if it is
     * invalid
     */
    public String getPartName() {
        if (partId < 0 || partId >= FullPart.values().length) {
            return "??:" + partId;
        } else {
            return FullPart.values()[partId].getAlias();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LegacyFullData that = (LegacyFullData) o;

        return id == that.id && (comment != null ? comment.equals(that.comment) : that.comment == null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LegacyFullData{" +
                "id=" + id +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", comment='" + comment + '\'' +
                ", thorns=" + thorns +
                ", partId=" + partId +
                ", currentReceiverId=" + currentReceiverId +
                ", createdTimeUnix=" + createdTimeUnix +
                '}';
    }
}
