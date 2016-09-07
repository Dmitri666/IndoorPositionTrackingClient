package com.lps.lpsapp.positions;

import java.util.TreeSet;

/**
 * Created by dle on 06.09.2016.
 */
public class TrippleGroup {
    private TreeSet<Integer> group;

    public TrippleGroup(int i1, int i2, int i3) {
        this.group = new TreeSet<>();
        this.group.add(i1);
        this.group.add(i2);
        this.group.add(i3);
    }

    public TreeSet<Integer> getGroupIds() {
        return this.group;
    }

    public boolean equals(TrippleGroup group) {
        return this.group.equals(group);
    }

    public int hashCode() {
        return this.group.hashCode();
    }
}
