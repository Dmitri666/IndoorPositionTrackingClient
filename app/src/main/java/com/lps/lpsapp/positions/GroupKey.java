package com.lps.lpsapp.positions;

/**
 * Created by dle on 21.06.2016.
 */
public class GroupKey {
    public int key1;
    public int key2;
    public int key3;

    public GroupKey() {

    }

    public GroupKey(int key1) {
        this.key1 = key1;
    }

    public GroupKey(int key1,int key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    public GroupKey(int key1,int key2,int key3) {
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
    }
}
