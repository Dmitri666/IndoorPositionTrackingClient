package com.lps.lpsapp.positions;

import java.util.HashSet;

/**
 * Created by dle on 06.09.2016.
 */
public class DubbleGroup {
    private HashSet<Integer> group;

    public DubbleGroup(int i1, int i2) {
        this.group = new HashSet<>();
        this.group.add(i1);
        this.group.add(i2);
    }

    public HashSet<Integer> getGroupIds() {
        return this.group;
    }

    public boolean equals(DubbleGroup group) {
        return this.group.equals(group);
    }

    public int hashCode() {
        return this.group.hashCode();
    }
}
