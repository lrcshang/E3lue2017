/**
 * @author zhoushengtao
 * @since 2013-7-16 下午7:09:08
 */

package com.e3lue.us.db;

import com.e3lue.us.model.BaseMenu;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String TAG = "DatabaseHelper";
    // 数据库名称
    private static final String DATABASE_NAME = "UserMenusOrmlite.db";
    // 数据库version
    private static final int DATABASE_VERSION = 1;

    private Dao<BaseMenu, Integer> userDao = null;
    private RuntimeExceptionDao<BaseMenu, Integer> userRuntimeDao = null;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
       // 可以用配置文件来生成 数据表，有点繁琐，不喜欢用
       // super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * @param context
     * @param databaseName
     * @param factory
     * @param databaseVersion
     */
    public DatabaseHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion)
    {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource)
    {
        try
        {
            //建立User表
            TableUtils.createTable(connectionSource, BaseMenu.class);
            //初始化DAO
            userDao = getUserDao();
            userRuntimeDao = getUserDataDao();
        }
        catch (SQLException e)
        {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        try
        {
            TableUtils.dropTable(connectionSource, BaseMenu.class, true);
        }
        catch (SQLException e)
        {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    /**
     * @return
     * @throws SQLException
     */
    private Dao<BaseMenu, Integer> getUserDao() throws SQLException
    {
        if (userDao == null)
            userDao = getDao(BaseMenu.class);
        return userDao;
    }

    public RuntimeExceptionDao<BaseMenu, Integer> getUserDataDao()
    {
        if (userRuntimeDao == null)
        {
            userRuntimeDao = getRuntimeExceptionDao(BaseMenu.class);
        }
        return userRuntimeDao;
    }
    
    /**
     * 释放 DAO
     */
    @Override
    public void close() {
        super.close();
        userRuntimeDao = null;
    }

}
