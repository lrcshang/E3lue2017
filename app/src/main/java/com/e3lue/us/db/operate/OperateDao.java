package com.e3lue.us.db.operate;

import android.app.Activity;
import android.content.Context;

import com.e3lue.us.db.DatabaseHelper;
import com.e3lue.us.model.BaseMenu;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Enzate on 2017/7/7.
 */

public class OperateDao {
    private DatabaseHelper databaseHelper = null;
    private RuntimeExceptionDao<BaseMenu, Integer> mMenusDAO;
    Context context;
    public OperateDao( Context context){
        this.context =context;
        mMenusDAO=getHelper().getUserDataDao();
    }
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager
                    .getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }
    /**
     * 插入值测试
     */
    public void insertTest(List<BaseMenu> menus)
    {
        BaseMenu baseMenu;
        for (int i = 0; i < menus.size(); i++)
        {
            baseMenu = menus.get(i);
            mMenusDAO.createIfNotExists(baseMenu);
        }
    }

    /**
     * 更新
     *
     * @param baseMenu 待更新的user
     */
    public void update(BaseMenu baseMenu)
    {mMenusDAO.updateId(baseMenu, baseMenu.getId());
        mMenusDAO.createOrUpdate(baseMenu);
        // mUserDAO.update(user);
    }

    /**
     * 按照指定的id 与 username 删除一项
     *
     * @param
     * @param username
     * @return 删除成功返回true ，失败返回false
     */
    private int delete(String username)
    {
        try
        {
            // 删除指定的信息，类似delete User where 'id' = id ;
            DeleteBuilder<BaseMenu, Integer> deleteBuilder = mMenusDAO.deleteBuilder();
//            deleteBuilder.where().eq("username", username);

            return deleteBuilder.delete();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 按照id查询user
     *
     * @param
     * @return
     */
    public BaseMenu search(String username)
    {
        try
        {
            // 查询的query 返回值是一个列表
            // 类似 select * from User where 'MobileName' = username;
            List<BaseMenu> users = mMenusDAO.queryBuilder().where().eq("MobileName", username).query();
            if (users.size() > 0)
                return users.get(0);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除全部
     */
    public void deleteAll()
    {
        mMenusDAO.delete(queryAll());
    }

    /**
     * 查询所有的
     */
    public List<BaseMenu> queryAll()
    {
        List<BaseMenu> users = mMenusDAO.queryForAll();
        return users;
    }

    /**
     * 显示所有的
     */
    public List<BaseMenu> display()
    {
        List<BaseMenu> menus = queryAll();
        for (BaseMenu baseMenu : menus)
        {
//            mTextView.append(user.toString());
        }
        return menus;
    }
}
