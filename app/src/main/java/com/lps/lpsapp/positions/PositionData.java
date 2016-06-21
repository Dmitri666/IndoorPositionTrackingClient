package com.lps.lpsapp.positions;

/**
 * Created by dle on 21.06.2016.
 */
public class PositionData {
    public GroupKey key;
    public PointD position;

    public PositionData(){

    }

    public PositionData(GroupKey key,PointD position){
        this.key = key;
        this.position = position;
    }
}
