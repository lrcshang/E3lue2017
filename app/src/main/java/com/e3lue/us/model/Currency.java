package com.e3lue.us.model;

/**
 * Created by Leo on 2017/5/5.
 */

public enum Currency {
    RMB("人民币", 1000), USD("美元", 2000), HK("港币", 3000);

    private Currency(String name, int index) {
        this.name = name;
        this.index = index;
    }

    private String name;
    private int index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
