package com.nasduck.lib.anim;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 效果：
 * 左滑时当前页面左滑消失，下一个页面渐变上浮
 * 右滑时当前页面渐变下沉，下一个页面从左边划入
 */

public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1) {
            view.setAlpha(0);
        } else if (position <= 0) {
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);
        } else if (position <= 1) {
            view.setAlpha(1 - position);
            view.setTranslationX(pageWidth * -position);
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else {
            view.setAlpha(0);
        }
    }
}
