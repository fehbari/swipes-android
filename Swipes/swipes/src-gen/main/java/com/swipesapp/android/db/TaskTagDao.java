package com.swipesapp.android.db;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.swipesapp.android.db.TaskTag;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table TASK_TAG.
*/
public class TaskTagDao extends AbstractDao<TaskTag, Void> {

    public static final String TABLENAME = "TASK_TAG";

    /**
     * Properties of entity TaskTag.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property TaskId = new Property(0, long.class, "taskId", false, "TASK_ID");
        public final static Property TagId = new Property(1, long.class, "tagId", false, "TAG_ID");
    };

    private DaoSession daoSession;

    private Query<TaskTag> task_TaskTagsQuery;
    private Query<TaskTag> tag_TaskTagsQuery;

    public TaskTagDao(DaoConfig config) {
        super(config);
    }
    
    public TaskTagDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TASK_TAG' (" + //
                "'TASK_ID' INTEGER NOT NULL ," + // 0: taskId
                "'TAG_ID' INTEGER NOT NULL );"); // 1: tagId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TASK_TAG'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TaskTag entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getTaskId());
        stmt.bindLong(2, entity.getTagId());
    }

    @Override
    protected void attachEntity(TaskTag entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public TaskTag readEntity(Cursor cursor, int offset) {
        TaskTag entity = new TaskTag( //
            cursor.getLong(offset + 0), // taskId
            cursor.getLong(offset + 1) // tagId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TaskTag entity, int offset) {
        entity.setTaskId(cursor.getLong(offset + 0));
        entity.setTagId(cursor.getLong(offset + 1));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(TaskTag entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(TaskTag entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "taskTags" to-many relationship of Task. */
    public List<TaskTag> _queryTask_TaskTags(long taskId) {
        synchronized (this) {
            if (task_TaskTagsQuery == null) {
                QueryBuilder<TaskTag> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.TaskId.eq(null));
                task_TaskTagsQuery = queryBuilder.build();
            }
        }
        Query<TaskTag> query = task_TaskTagsQuery.forCurrentThread();
        query.setParameter(0, taskId);
        return query.list();
    }

    /** Internal query to resolve the "taskTags" to-many relationship of Tag. */
    public List<TaskTag> _queryTag_TaskTags(long tagId) {
        synchronized (this) {
            if (tag_TaskTagsQuery == null) {
                QueryBuilder<TaskTag> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.TagId.eq(null));
                tag_TaskTagsQuery = queryBuilder.build();
            }
        }
        Query<TaskTag> query = tag_TaskTagsQuery.forCurrentThread();
        query.setParameter(0, tagId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getTaskDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getTagDao().getAllColumns());
            builder.append(" FROM TASK_TAG T");
            builder.append(" LEFT JOIN TASK T0 ON T.'TASK_ID'=T0.'_id'");
            builder.append(" LEFT JOIN TAG T1 ON T.'TAG_ID'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected TaskTag loadCurrentDeep(Cursor cursor, boolean lock) {
        TaskTag entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Task task = loadCurrentOther(daoSession.getTaskDao(), cursor, offset);
         if(task != null) {
            entity.setTask(task);
        }
        offset += daoSession.getTaskDao().getAllColumns().length;

        Tag tag = loadCurrentOther(daoSession.getTagDao(), cursor, offset);
         if(tag != null) {
            entity.setTag(tag);
        }

        return entity;    
    }

    public TaskTag loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<TaskTag> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<TaskTag> list = new ArrayList<TaskTag>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<TaskTag> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<TaskTag> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
