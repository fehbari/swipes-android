package com.swipesapp.android.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.swipesapp.android.db.Task;
import com.swipesapp.android.db.Tag;
import com.swipesapp.android.db.Attachment;
import com.swipesapp.android.db.TaskTag;
import com.swipesapp.android.db.TaskSync;
import com.swipesapp.android.db.TagSync;

import com.swipesapp.android.db.TaskDao;
import com.swipesapp.android.db.TagDao;
import com.swipesapp.android.db.AttachmentDao;
import com.swipesapp.android.db.TaskTagDao;
import com.swipesapp.android.db.TaskSyncDao;
import com.swipesapp.android.db.TagSyncDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig taskDaoConfig;
    private final DaoConfig tagDaoConfig;
    private final DaoConfig attachmentDaoConfig;
    private final DaoConfig taskTagDaoConfig;
    private final DaoConfig taskSyncDaoConfig;
    private final DaoConfig tagSyncDaoConfig;

    private final TaskDao taskDao;
    private final TagDao tagDao;
    private final AttachmentDao attachmentDao;
    private final TaskTagDao taskTagDao;
    private final TaskSyncDao taskSyncDao;
    private final TagSyncDao tagSyncDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        taskDaoConfig = daoConfigMap.get(TaskDao.class).clone();
        taskDaoConfig.initIdentityScope(type);

        tagDaoConfig = daoConfigMap.get(TagDao.class).clone();
        tagDaoConfig.initIdentityScope(type);

        attachmentDaoConfig = daoConfigMap.get(AttachmentDao.class).clone();
        attachmentDaoConfig.initIdentityScope(type);

        taskTagDaoConfig = daoConfigMap.get(TaskTagDao.class).clone();
        taskTagDaoConfig.initIdentityScope(type);

        taskSyncDaoConfig = daoConfigMap.get(TaskSyncDao.class).clone();
        taskSyncDaoConfig.initIdentityScope(type);

        tagSyncDaoConfig = daoConfigMap.get(TagSyncDao.class).clone();
        tagSyncDaoConfig.initIdentityScope(type);

        taskDao = new TaskDao(taskDaoConfig, this);
        tagDao = new TagDao(tagDaoConfig, this);
        attachmentDao = new AttachmentDao(attachmentDaoConfig, this);
        taskTagDao = new TaskTagDao(taskTagDaoConfig, this);
        taskSyncDao = new TaskSyncDao(taskSyncDaoConfig, this);
        tagSyncDao = new TagSyncDao(tagSyncDaoConfig, this);

        registerDao(Task.class, taskDao);
        registerDao(Tag.class, tagDao);
        registerDao(Attachment.class, attachmentDao);
        registerDao(TaskTag.class, taskTagDao);
        registerDao(TaskSync.class, taskSyncDao);
        registerDao(TagSync.class, tagSyncDao);
    }
    
    public void clear() {
        taskDaoConfig.getIdentityScope().clear();
        tagDaoConfig.getIdentityScope().clear();
        attachmentDaoConfig.getIdentityScope().clear();
        taskTagDaoConfig.getIdentityScope().clear();
        taskSyncDaoConfig.getIdentityScope().clear();
        tagSyncDaoConfig.getIdentityScope().clear();
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public AttachmentDao getAttachmentDao() {
        return attachmentDao;
    }

    public TaskTagDao getTaskTagDao() {
        return taskTagDao;
    }

    public TaskSyncDao getTaskSyncDao() {
        return taskSyncDao;
    }

    public TagSyncDao getTagSyncDao() {
        return tagSyncDao;
    }

}
