package com.e3lue.us.model;

/**
 * Created by Leo on 2017/4/26.
 */

import com.e3lue.us.db.operate.Tablename;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 数据库对应的pojo类，注意一下三点
 * 1、填写表的名称 @DatabaseTable
 * 2、填写表中持久化项的 @DatabaseField 还可使顺便设置其属性
 * 3、保留一个无参的构造函数
 */
//表名称
@DatabaseTable(tableName = Tablename.name)
public class BaseMenu implements Comparable<BaseMenu> {
    // 主键 id 自增长
    @DatabaseField(generatedId = true)
    private int id;
    // 映射
    @DatabaseField(canBeNull = false)
    private String MobileName;
    // 不为空
    @DatabaseField(canBeNull = false)
    private String EnglishName;
    @DatabaseField(defaultValue = "")
    private String MobileIcon;
    private Integer MobileSortId;
    private int MobileM;
    /**
     * @return the id
     */
    public int getId()
    {
        return this.id;
    }
    public BaseMenu()
    {
        // ORMLite 需要一个无参构造
    }
    /**
     * @param id the id to set
     */
    public void setId(int id)
    {
        this.id = id;
    }
    public String getMobileIcon() {
        return MobileIcon;
    }

    public void setMobileIcon(String mobileIcon) {
        MobileIcon = mobileIcon;
    }

    public String getMobileName() {
        return MobileName;
    }

    public void setMobileName(String mobileName) {
        MobileName = mobileName;
    }

    public String getEnglishName() {
        return EnglishName;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }

    public Integer getMobileSortId() {
        return MobileSortId;
    }

    public void setMobileSortId(Integer mobileSortId) {
        MobileSortId = mobileSortId;
    }

    public int getMobileM() {
        return MobileM;
    }

    public void setMobileM(int mobileM) {
        MobileM = mobileM;
    }

    public int compareTo(BaseMenu arg0) {
        return this.getMobileSortId().compareTo(arg0.getMobileSortId());
    }

}
