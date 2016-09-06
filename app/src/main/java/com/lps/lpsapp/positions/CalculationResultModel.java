package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dle on 17.08.2016.
 */
public class CalculationResultModel extends ArrayList<CalculationResult> {

    public Region clip;

    public Point2D getPoint1() {
        Point2D result = new Point2D(0, 0);
        if (this.size() == 0) {
            return null;
        }

        for (CalculationResult point : this) {
            result.x += point.point.x;
            result.y += point.point.y;
        }

        result.x = result.x / this.size();
        result.y = result.y / this.size();
        this.analyse();
        return result;
    }

    private void analyse() {
        List<CalculationResult> list = new ArrayList<>();
        for(CalculationResult cr:this) {
            if(cr.groupKey.contains(1)) {
                list.add(cr);
            }
        }

        String s = "";
    }

    public Point2D getPoint() {
        Point2D result = new Point2D(0, 0);
        if (this.size() == 0) {
            return null;
        }
        boolean res = false;
        Path first = null;
        for (CalculationResult point : this) {
            if(first == null) {
                first = point.path;
            }
            else {
                res = first.op(point.path, Path.Op.INTERSECT);
            }

        }

        if(res)
        {
            Region reg = new Region();
            reg.setPath(first,clip);
            Rect b = new Rect();
            reg.getBounds(b);

            if(reg.isEmpty()) {
                Path p = first;
            }
            else {
                Path p = first;
            }

        }

        return result;
    }
}
