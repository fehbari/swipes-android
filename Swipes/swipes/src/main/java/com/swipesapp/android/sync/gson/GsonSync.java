package com.swipesapp.android.sync.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Gson mapping class for "sync" endpoint.
 *
 * @author Fernanda Bari
 */
public class GsonSync {

    // Request fields.
    @Expose(deserialize = false)
    private String sessionToken;

    @Expose(deserialize = false)
    private String platform;

    @Expose(deserialize = false)
    private String version;

    @Expose(deserialize = false)
    private Boolean changesOnly;

    @Expose(deserialize = false)
    private String lastUpdate;

    @Expose(deserialize = false)
    private String syncId;

    @Expose(deserialize = false)
    private GsonObjects objects;

    // Response fields.
    @Expose(serialize = false)
    private String serverTime;

    @Expose(serialize = false)
    private String updateTime;

    @SerializedName("ToDo")
    @Expose(serialize = false)
    private List<GsonTask> tasks;

    @SerializedName("Tag")
    @Expose(serialize = false)
    private List<GsonTag> tags;

    public GsonSync(String sessionToken, String platform, String version, Boolean changesOnly, String lastUpdate, String syncId, GsonObjects objects) {
        this.sessionToken = sessionToken;
        this.platform = platform;
        this.version = version;
        this.changesOnly = changesOnly;
        this.lastUpdate = lastUpdate;
        this.syncId = syncId;
        this.objects = objects;
    }

    public String getServerTime() {
        return serverTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public List<GsonTask> getTasks() {
        return tasks;
    }

    public List<GsonTag> getTags() {
        return tags;
    }

}
