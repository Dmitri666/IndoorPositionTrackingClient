package com.lps.lpsapp.positions;

import java.util.HashSet;

/**
 * Created by dle on 06.09.2016.
 */
public class TrippleGroup {
    private HashSet<Integer> group;

    public TrippleGroup(int i1, int i2, int i3) {
        this.group = new HashSet<>();
        this.group.add(i1);
        this.group.add(i2);
        this.group.add(i3);
    }

    public HashSet<Integer> getGroupIds() {
        return this.group;
    }

    public boolean equals(TrippleGroup group) {
        return this.group.equals(group);
    }

    public int hashCode() {
        return this.group.hashCode();
    }
}
