package com.lps.core.gui;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.ScaleGestureDetector;

/**
        * A utility class for using {@link android.view.ScaleGestureDetector} in a backward-compatible
        * fashion.
        */
public class ScaleGestureDetectorCompat {
    public static boolean synchronizeScale = false;
    /**
     * Disallow instantiation.
     */
    private ScaleGestureDetectorCompat() {

    }

    /**
     * @see android.view.ScaleGestureDetector#getCurrentSpanX()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static float getCurrentSpanX(ScaleGestureDetector scaleGestureDetector) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !synchronizeScale) {
            return scaleGestureDetector.getCurrentSpanX();
        } else {
            return scaleGestureDetector.getCurrentSpan();
        }
    }

    /**
     * @see android.view.ScaleGestureDetector#getCurrentSpanY()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static float getCurrentSpanY(ScaleGestureDetector scaleGestureDetector) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !synchronizeScale) {
            return scaleGestureDetector.getCurrentSpanY();
        } else {
            return scaleGestureDetector.getCurrentSpan();
        }
    }
}

