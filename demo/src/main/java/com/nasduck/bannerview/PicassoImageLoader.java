package com.nasduck.bannerview;

import android.content.Context;
import android.widget.ImageView;

import com.nasduck.lib.loader.ImageLoader;
import com.squareup.picasso.Picasso;

public class PicassoImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Picasso.get().load((String) path).into(imageView);
    }
}
