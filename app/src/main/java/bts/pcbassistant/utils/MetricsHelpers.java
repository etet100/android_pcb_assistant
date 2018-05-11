package bts.pcbassistant.utils;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

public class MetricsHelpers {

    private static DisplayMetrics dm = null;
    private static float minStrokeWidth;

    public static void initDisplayMetrics(Display d) {
        dm = new DisplayMetrics();
        d.getMetrics(dm);
        //minimalna grubość linii zależna od DPI
        //170 = 1
        //480 = 2.5
        minStrokeWidth = 1 + ((dm.densityDpi - 170) * 0.003f); //0.00483871f);
    }

    public static float dpToPixels(float dpSize) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);
    }

    public static int cmToPixelsX(float dpSize) {
        //dm.xdpi = 2.56;
        //x = dpSize
        return Math.round(dpSize * dm.xdpi / 2.56f);
    }

    public static int cmToPixelsY(float dpSize) {
        //dm.ydpi = 2.56;
        //y = dpSize
        return Math.round(dpSize * dm.ydpi / 2.56f);
    }

    //minimalna grubość linii zależna od DPI
    public static float getMinStrokeWidth() {
        return minStrokeWidth;
    }
}
