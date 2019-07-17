package com.nasduck.bannerview;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nasduck.lib.loader.ImageLoader;

public class GlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {

        Glide.with(context)
                .load(path)
                .placeholder(R.drawable.ic_phaceholder)
                .into(imageView);
    }
}
