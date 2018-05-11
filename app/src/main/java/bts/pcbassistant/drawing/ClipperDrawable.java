package bts.pcbassistant.drawing;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

import bts.pcbassistant.BuildConfiguration;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.view.ExtendedCanvas;
import de.lighti.clipper.Paths;

/**
 * Created by a on 2017-08-12.
 */

public class ClipperDrawable extends BaseDrawable {

    private Path toAndroidPath(de.lighti.clipper.Path path, boolean closed) {
        boolean first = true;
        android.graphics.Path androidPath = new android.graphics.Path();
        for (de.lighti.clipper.Point.LongPoint p : path) {
            if (first)
                androidPath.moveTo((float)p.getX() * 0.01f, (float)p.getY() * 0.01f);
            else
                androidPath.lineTo((float)p.getX() * 0.01f, (float)p.getY() * 0.01f);
            first = false;
        }
        if (closed)
            androidPath.lineTo((float)path.get(0).getX() * 0.01f, (float)path.get(0).getY() * 0.01f);
        return androidPath;
    }

    private List<Path> paths;

    public ClipperDrawable(Paths paths) {
        this.paths = new ArrayList<>();
        for (de.lighti.clipper.Path p : paths) {
            this.paths.add(this.toAndroidPath(p, true));
        }
    }

    @Override

    public void Draw(ExtendedCanvas extendedCanvas) {
        if (BuildConfiguration.CALCULATE_OFFSETS) {
            Paint paint = new Paint();
            paint.setColor(Color.MAGENTA);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(extendedCanvas.checkMinStrokeWidth(0));
            for (Path p : this.paths) {
                extendedCanvas.drawPath(p, paint);
            }
        }
    }
}
