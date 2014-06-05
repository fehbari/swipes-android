package com.swipesapp.android.db;

import com.swipesapp.android.db.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table TASK_TAG.
 */
public class TaskTag {

    private long taskId;
    private long tagId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TaskTagDao myDao;

    private Task task;
    private Long task__resolvedKey;

    private Tag tag;
    private Long tag__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public TaskTag() {
    }

    public TaskTag(long taskId, long tagId) {
        this.taskId = taskId;
        this.tagId = tagId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTaskTagDao() : null;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    /** To-one relationship, resolved on first access. */
    public Task getTask() {
        long __key = this.taskId;
        if (task__resolvedKey == null || !task__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TaskDao targetDao = daoSession.getTaskDao();
            Task taskNew = targetDao.load(__key);
            synchronized (this) {
                task = taskNew;
            	task__resolvedKey = __key;
            }
        }
        return task;
    }

    public void setTask(Task task) {
        if (task == null) {
            throw new DaoException("To-one property 'taskId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.task = task;
            taskId = task.getId();
            task__resolvedKey = taskId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Tag getTag() {
        long __key = this.tagId;
        if (tag__resolvedKey == null || !tag__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TagDao targetDao = daoSession.getTagDao();
            Tag tagNew = targetDao.load(__key);
            synchronized (this) {
                tag = tagNew;
            	tag__resolvedKey = __key;
            }
        }
        return tag;
    }

    public void setTag(Tag tag) {
        if (tag == null) {
            throw new DaoException("To-one property 'tagId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.tag = tag;
            tagId = tag.getId();
            tag__resolvedKey = tagId;
        }
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
