package com.swipesapp.android.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.swipesapp.android.db.TagSync;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table TAG_SYNC.
*/
public class TagSyncDao extends AbstractDao<TagSync, Long> {

    public static final String TABLENAME = "TAG_SYNC";

    /**
     * Properties of entity TagSync.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property TempId = new Property(2, String.class, "tempId", false, "TEMP_ID");
        public final static Property CreatedAt = new Property(3, String.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(4, String.class, "updatedAt", false, "UPDATED_AT");
        public final static Property Title = new Property(5, String.class, "title", false, "TITLE");
    };


    public TagSyncDao(DaoConfig config) {
        super(config);
    }
    
    public TagSyncDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TAG_SYNC' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'OBJECT_ID' TEXT," + // 1: objectId
                "'TEMP_ID' TEXT," + // 2: tempId
                "'CREATED_AT' TEXT," + // 3: createdAt
                "'UPDATED_AT' TEXT," + // 4: updatedAt
                "'TITLE' TEXT);"); // 5: title
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TAG_SYNC'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TagSync entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(2, objectId);
        }
 
        String tempId = entity.getTempId();
        if (tempId != null) {
            stmt.bindString(3, tempId);
        }
 
        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(4, createdAt);
        }
 
        String updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindString(5, updatedAt);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TagSync readEntity(Cursor cursor, int offset) {
        TagSync entity = new TagSync( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // tempId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // createdAt
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // updatedAt
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // title
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TagSync entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTempId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCreatedAt(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUpdatedAt(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTitle(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TagSync entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TagSync entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
