package com.e3lue.us.model;

import com.e3lue.us.R;
import com.kanade.treeadapter.RvTree;

/**
 * Created by Leo on 2017/5/31.
 */

public class BaseOrganize implements RvTree {
    private int ID;
    private String Name;
    private int ParentID;
    private int Sort;
    private String ChargeLeader;
    private String Leader;
    private int Depth;

    public int getDepth() {
        return Depth;
    }

    public void setDepth(int depth) {
        Depth = depth;
        level = depth;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
        id = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
        title = name;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
        pid = parentID;
    }

    public int getSort() {
        return Sort;
    }

    public void setSort(int sort) {
        Sort = sort;
    }

    public String getChargeLeader() {
        return ChargeLeader;
    }

    public void setChargeLeader(String chargeLeader) {
        ChargeLeader = chargeLeader;
    }

    public String getLeader() {
        return Leader;
    }

    public void setLeader(String leader) {
        Leader = leader;
    }

    //tree
    private int id;
    private int pid;
    private int level;
    private String title;
    private int resId;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getPid() {
        return pid;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    @Override
    public int getImageResId() {
        return R.drawable.ic_detail;
    }
}
