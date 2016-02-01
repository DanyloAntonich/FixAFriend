package com.han.customscrollview;

/**
 * Created by hic on 10/30/2015.
 */
public interface ScrollViewListener {
    void onScrollChanged(CustomScrollView scrollView,
                         int x, int y, int oldx, int oldy);
}
