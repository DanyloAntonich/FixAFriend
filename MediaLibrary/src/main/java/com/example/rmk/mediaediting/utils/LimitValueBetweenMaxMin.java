package com.example.rmk.mediaediting.utils;

/**
 * Created by RMK on 11/11/2015.
 */
public class LimitValueBetweenMaxMin {
    public static float ensureRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}
