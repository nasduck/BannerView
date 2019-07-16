package com.nasduck.lib.transform;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 效果：
 * 切换时，当前页面中心缩小，下一个页面中心放大
 */

public class CenterOfAmplificationTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
        if (position < -1) {

        } else if (position <= 0) {
            view.setTranslationX(-view.getWidth() * position);

            view.setPivotX(view.getWidth() * 0.5f);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setScaleX(1 + position);
            view.setScaleY(1 + position);

            if (position < -0.95f) {
                view.setAlpha(0);
            } else {
                view.setAlpha(1);
            }
        } else if (position <= 1) {
            view.setTranslationX(-view.getWidth() * position);

            view.setPivotX(view.getWidth() * 0.5f);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setScaleX(1 - position);
            view.setScaleY(1 - position);

            if (position > 0.95f) {
                view.setAlpha(0);
            } else {
                view.setAlpha(1);
            }
        }
    }
}
