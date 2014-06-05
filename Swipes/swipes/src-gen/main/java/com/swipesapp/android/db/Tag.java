package com.swipesapp.android.db;

import java.util.List;
import com.swipesapp.android.db.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table TAG.
 */
public class Tag {

    private Long id;
    private String objectId;
    private String tempId;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;
    private String title;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TagDao myDao;

    private List<TaskTag> taskTags;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Tag() {
    }

    public Tag(Long id) {
        this.id = id;
    }

    public Tag(Long id, String objectId, String tempId, java.util.Date createdAt, java.util.Date updatedAt, String title) {
        this.id = id;
        this.objectId = objectId;
        this.tempId = tempId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.title = title;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTagDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<TaskTag> getTaskTags() {
        if (taskTags == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TaskTagDao targetDao = daoSession.getTaskTagDao();
            List<TaskTag> taskTagsNew = targetDao._queryTag_TaskTags(id);
            synchronized (this) {
                if(taskTags == null) {
                    taskTags = taskTagsNew;
                }
            }
        }
        return taskTags;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTaskTags() {
        taskTags = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
