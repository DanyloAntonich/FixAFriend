package com.han.downloadmanager;

import com.han.utils.Constant;

import java.util.ArrayList;

/**
 * Created by hic on 11/5/2015.
 */
public class DrawQueueManager {
    int Max = Constant.MAX_THREAD_COUNT;
    int currentThreadCount;
    ArrayList<String> arrayList;
    public DrawQueueManager(){
        arrayList = new ArrayList<String>();
        currentThreadCount = 0;
    }
    public void insert(int position){
        arrayList.add(String.valueOf(position));
    }
    public int getPosition(){
//        if(currentThreadCount >= Max){
//            return -1;
//        }
        if(arrayList.size() > 0){
            String str = arrayList.get(0);
            for(String string : arrayList){
                if(str.equals(string)){
//                    currentThreadCount ++;
                    arrayList.remove(string);

                }
            }

            return Integer.parseInt(str);
        }else {
            return -1;
        }
    }
    public void decreaseCurrentThreadCount(){
        this.currentThreadCount --;
    }
}
