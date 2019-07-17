package com.nasduck.bannerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.nasduck.lib.loader.ImageLoader;

public class ResourceImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (path instanceof Drawable) {
            imageView.setImageDrawable((Drawable) path);
        } else if (path instanceof Bitmap){
            imageView.setImageBitmap((Bitmap) path);
        }
    }
}
