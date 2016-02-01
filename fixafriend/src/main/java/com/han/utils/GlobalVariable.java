package com.han.utils;

import android.content.SharedPreferences;

/**
 * Created by hic on 2015-11-18.
 */
public class GlobalVariable {
    public  int getValue(SharedPreferences preferences, String key){
        int value = preferences.getInt(key, 0);
//        return preferences.getInt(key, 0);
        return value;
    }
    public   void  setValue(SharedPreferences preferences, String key, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public  void increaseValue(SharedPreferences preferences, String key){
        int currentValue = preferences.getInt(key, 0);
        currentValue ++;
        setValue(preferences, key, currentValue);
    }
    public  void decreaseValue(SharedPreferences preferences, String key){
        int currentValue = preferences.getInt(key, 0);

        if(currentValue > 0){
            currentValue --;
            setValue(preferences, key, currentValue);
        }

    }
}
