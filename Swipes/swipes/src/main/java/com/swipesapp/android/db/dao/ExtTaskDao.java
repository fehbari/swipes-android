package com.swipesapp.android.db.dao;

import android.util.Log;

import com.swipesapp.android.db.DaoSession;
import com.swipesapp.android.db.Task;
import com.swipesapp.android.db.TaskDao;

import org.apache.commons.collections4.comparators.NullComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Extended DAO for tasks, allowing custom DB operations.
 *
 * @author Felipe Bari
 */
public class ExtTaskDao {

    private static ExtTaskDao sInstance;
    private TaskDao mDao;

    private ExtTaskDao(DaoSession daoSession) {
        mDao = daoSession.getTaskDao();
    }

    private static final String LOG_TAG = ExtTaskDao.class.getSimpleName();

    public static ExtTaskDao getInstance(DaoSession daoSession) {
        if (sInstance == null) {
            sInstance = new ExtTaskDao(daoSession);
        }
        return sInstance;
    }

    public TaskDao getDao() {
        return mDao;
    }

    public Task selectTask(Long id) {
        return mDao.queryBuilder().where(TaskDao.Properties.Id.eq(id)).unique();
    }

    public Task selectTask(String tempId) {
        List<Task> tasks = mDao.queryBuilder().where(mDao.queryBuilder().or(TaskDao.Properties.TempId.eq(tempId),
                TaskDao.Properties.ObjectId.eq(tempId))).list();

        if (tasks != null && tasks.size() > 1) {
            // TODO: Log analytics event so we know when duplicates are being created.
            Log.w(LOG_TAG, "Duplicate found with tempId" + tempId);
        }

        return tasks != null ? tasks.get(0) : null;
    }

    public List<Task> listAllTasks() {
        return mDao.queryBuilder().list();
    }

    public List<Task> listScheduledTasks() {
        List<Task> tasks = mDao.queryBuilder().where(mDao.queryBuilder().or(TaskDao.Properties.Schedule.gt(new Date()),
                TaskDao.Properties.Schedule.isNull()), TaskDao.Properties.Deleted.eq(false), TaskDao.Properties.ParentLocalId.isNull())
                .orderAsc(TaskDao.Properties.Schedule).orderDesc(TaskDao.Properties.CreatedAt).list();

        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task, Task task2) {
                return new NullComparator<Date>(true).compare(task.getSchedule(), task2.getSchedule());
            }
        });

        return tasks;
    }

    public List<Task> listFocusedTasks() {
        return mDao.queryBuilder().where(TaskDao.Properties.Schedule.lt(new Date()), TaskDao.Properties.CompletionDate.isNull(),
                TaskDao.Properties.Deleted.eq(false), TaskDao.Properties.ParentLocalId.isNull()).orderAsc(TaskDao.Properties.Order)
                .orderDesc(TaskDao.Properties.CreatedAt).list();
    }

    public List<Task> listCompletedTasks() {
        return mDao.queryBuilder().where(TaskDao.Properties.CompletionDate.isNotNull(), TaskDao.Properties.Deleted.eq(false),
                TaskDao.Properties.ParentLocalId.isNull()).orderDesc(TaskDao.Properties.CompletionDate).list();
    }

    public List<Task> listSubtasksForTask(String objectId) {
        return mDao.queryBuilder().where(TaskDao.Properties.ParentLocalId.eq(objectId), TaskDao.Properties.Deleted.eq(false))
                .orderAsc(TaskDao.Properties.Order).orderAsc(TaskDao.Properties.CreatedAt).list();
    }

}
