package com.example.varia.wallpapers.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.booking.rtlviewpager.RtlViewPager;

/**
 * Created by varia on 03.11.2017.
 */

public class DisableViewPager extends RtlViewPager {

    private boolean enabled;

    public DisableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.enabled) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(this.enabled)
            return super.onInterceptTouchEvent(ev);

        return false;
    }

    public void setPagingEnabled(boolean enabled){
        this.enabled = enabled;
    }
}
