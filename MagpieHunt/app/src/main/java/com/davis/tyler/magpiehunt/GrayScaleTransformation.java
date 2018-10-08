package com.davis.tyler.magpiehunt;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;

import static android.graphics.Bitmap.createBitmap;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Shader.TileMode.REPEAT;

public class GrayScaleTransformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int width, height;
        height = source.getHeight();
        width = source.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(source, 0, 0, paint);
        source.recycle();
        return bmpGrayscale;

    }

    @Override
    public String key() {
        return "grayscaleTransformation()";
    }
}
