package bts.pcbassistant.data;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by a on 2017-08-05.
 */

public class Bounds extends RectF {

    public enum INBOUNDS {
        No,
        Yes,
        Subpart
    };

    public Rect getTransformed(Matrix matrix) {

        float[] pts = new float[8];

        pts[0] = this.left;
        pts[1] = this.top;
        pts[2] = this.right;
        pts[3] = this.bottom;

        //sprawdzamy wszystkie 4 rogi żeby wyszły poprawnie granice także po obrocie
        pts[4] = this.left;
        pts[5] = this.bottom;
        pts[6] = this.right;
        pts[7] = this.top;

        matrix.mapPoints(pts);

        //twórz granice po transformacji
        Rect b = new Rect((int)pts[0], (int)pts[1], (int)pts[0], (int)pts[1]);
        for (int i=0; i<8; i+=2)
            b.union((int)pts[i], (int)pts[i+1]);

        return b;
    }

    private boolean firstPoint = true;

    public void extend(PointF p) {
        if (firstPoint) {
            this.set(p.x, p.y, p.x, p.y);
            firstPoint = false;
        } else
            this.union(p.x, p.y);
    }

    public boolean inBounds(PointF point) {
        if (this != null)
            return this.contains(point.x, point.y);
        return false;
    }
    
}
