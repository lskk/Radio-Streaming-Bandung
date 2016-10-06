package org.pptik.radiostreaming.util;

/**
 * Created by hynra on 6/1/16.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class PictureFormatTransform {

    /**
     * drawableToBitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmapToDrawable
     *
     * @param bitmap
     * @param context
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap, Context context) {
        Resources res = context.getResources();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(res, bitmap);
        Drawable drawable = (Drawable) bitmapDrawable;
        return drawable;
    }
}