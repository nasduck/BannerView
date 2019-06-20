package com.nasduck.lib.anim;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 效果：
 * 3D切换效果
 */

public class ThreeDCubeTransformer implements ViewPager.PageTransformer {
    private float baseRotate = 90.0f;

    @Override
    public void transformPage(View view, float position) {
        if (position < -1) {
            view.setPivotX(view.getMeasuredWidth());
            view.setPivotY(view.getMeasuredHeight() * 0.5f);
            view.setRotationY(0);
        } else if (position <= 0) {
            view.setPivotX(view.getMeasuredWidth());
            view.setPivotY(view.getMeasuredHeight() * 0.5f);
            view.setRotationY(baseRotate * position);
        } else if (position <= 1) {
            view.setPivotX(0);
            view.setPivotY(view.getMeasuredHeight() * 0.5f);
            view.setRotationY(baseRotate * position);
        } else {
            view.setPivotX(view.getMeasuredWidth());
            view.setPivotY(view.getMeasuredHeight() * 0.5f);
            view.setRotationY(0);
        }
    }
}

