package com.swipesapp.android.sync.gson;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Gson mapping class for tag fields.
 *
 * @author Felipe Bari
 */
public class GsonTag {

    @Expose
    private String objectId;
    @Expose
    private String tempId;
    @Expose
    private String title;
    @Expose
    private Boolean deleted;

    // Sync dates.
    @Expose(serialize = false)
    private String createdAt;
    @Expose(serialize = false)
    private String updatedAt;

    // Local dates.
    private Long id;
    private Date localCreatedAt;
    private Date localUpdatedAt;

    /**
     * Returns a GsonTag object for use with the local database.
     *
     * @return GsonTag object.
     */
    public static GsonTag gsonForLocal(Long id, String objectId, String tempId, Date localCreatedAt, Date localUpdatedAt, String title) {
        GsonTag tag = new GsonTag();
        tag.id = id;
        tag.objectId = objectId;
        tag.tempId = tempId;
        tag.localCreatedAt = localCreatedAt;
        tag.localUpdatedAt = localUpdatedAt;
        tag.title = title;

        return tag;
    }

    /**
     * Returns a GsonTag object for use with syncing.
     *
     * @return GsonTag object.
     */
    public static GsonTag gsonForSync(Long id, String objectId, String tempId, String createdAt, String updatedAt, String title, Boolean deleted) {
        GsonTag tag = new GsonTag();
        tag.id = id;
        tag.objectId = objectId;
        tag.tempId = tempId;
        tag.createdAt = createdAt;
        tag.updatedAt = updatedAt;
        tag.title = title;
        tag.deleted = deleted;

        return tag;
    }

    public Long getId() {
        return id;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getTempId() {
        return tempId;
    }

    public void setLocalCreatedAt(Date localCreatedAt) {
        this.localCreatedAt = localCreatedAt;
    }

    public Date getLocalCreatedAt() {
        return localCreatedAt;
    }

    public void setLocalUpdatedAt(Date localUpdatedAt) {
        this.localUpdatedAt = localUpdatedAt;
    }

    public Date getLocalUpdatedAt() {
        return localUpdatedAt;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

}
