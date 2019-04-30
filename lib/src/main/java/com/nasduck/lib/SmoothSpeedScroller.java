package com.nasduck.lib;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class SmoothSpeedScroller extends Scroller {

    // 动画时间
    private int mSmoothDuration = 400;

    public SmoothSpeedScroller(Context context) {
        super(context);
    }

    public SmoothSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public SmoothSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mSmoothDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mSmoothDuration);
    }

    public int getSmoothDuration() {
        return mSmoothDuration;
    }

    public void setDuration(int duration) {
        mSmoothDuration = duration;
    }
}
