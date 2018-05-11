package bts.pcbassistant.drawing;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by a on 2017-08-19.
 */

public class Arc {

    final private RectF oval;
    final private double angle;
    final private double startAngle;
    final private PointF centerPoint;
    final private PointF endPoint;
    final private PointF startPoint;

    public void addToPath(Path path) {
        path.addArc(this.oval, (float)this.startAngle, (float)this.angle);
        path.moveTo(endPoint.x, endPoint.y);
    }

    public Arc(PointF A, PointF B, double angle) {
        this.angle = (float)angle;
        this.endPoint = B;
        this.startPoint = A;

        final double a = Math.toRadians(angle);
        //srodek?
        final double Sx = (double) ((A.x + B.x) / 2.0f);
        final double Sy = (double) ((A.y + B.y) / 2.0f);
        //wymiary boxa obejmującego początek i koniec łuku
        final double VABx = (double) (B.x - A.x);
        final double VABy = (double) (B.y - A.y);
        //odległość między punktami łuku?
        final double s = Math.sqrt((VABx * VABx) + (VABy * VABy));

        final double h = (angle == 180.0d || angle == -180.0d)?0.0d:((0.5d * s) / Math.tan(Math.abs(a) / 2.0d));
        final double factor = (angle > 0.0d)?h:-h;

        final double Mx = Sx - (((-VABy) / s) * factor);
        final double My = Sy - ((VABx / s) * factor);
        final double VMAx = Mx - ((double) A.x);
        final double VMAy = My - ((double) A.y);
        //radius?
        final double r = Math.sqrt((VMAx * VMAx) + (VMAy * VMAy));
        this.oval = new RectF((float) (Mx - r), (float) (My - r), (float) (Mx + r), (float) (My + r)); // out
        double VMBx = ((double) B.x) - Mx;
        this.startAngle = Float.valueOf((float) Math.toDegrees(Math.atan2(((double) B.y) - My, VMBx))); // out
        this.centerPoint = new PointF((float)Mx, (float)My); // out
    }

    public de.lighti.clipper.Path getClipperPath() {
        de.lighti.clipper.Path clipperPath = new de.lighti.clipper.Path();

        double step = Math.toRadians(this.angle / 10.0d);
        double currAngle = 0;
        double startX = endPoint.x - centerPoint.x;
        double startY = endPoint.y - centerPoint.y;
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int) (this.endPoint.x * 100.0f), (int) (this.endPoint.y * 100.0f)));
        for (int i=0; i<10; i++) {
            double x = (startX * Math.cos(currAngle)) - (startY * Math.sin(currAngle));
            double y = (startY * Math.cos(currAngle)) + (startX * Math.sin(currAngle));
            currAngle += step;
            clipperPath.add(new de.lighti.clipper.Point.LongPoint((int) ((x + centerPoint.x) * 100.0f), (int) ((y + centerPoint.y) * 100.0f)));
        }
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int) (this.startPoint.x * 100.0f), (int) (this.startPoint.y * 100.0f)));

        return clipperPath;
    }

    public RectF getBounds() {

        //arc = Helpers.getArc(this.startPoint, this.end, this.curve);
        //RectF ovaellipseBounds = arc.getOval();
        //PointF center = arc.getCenter();
        //float a1 = arc.getStartAngle();
        //path.addArc(ellipseBounds, ((Float) arc[0]).floatValue(), (float) this.curve);

        //w ten sposób sortowanie punktów będzie poprawne?
        RectF bounds = new RectF(this.startPoint.x, this.startPoint.y, this.startPoint.x, this.startPoint.y);
        bounds.union(this.endPoint.x, this.endPoint.y);

        //if (a1 < 0) a1 = 360 + a1;
        //Log.d("kat org i nowy, curve", String.format("%.2f %.2f  %.02f", ((Float) arc[0]).floatValue(), a1, curve));
        //Log.d("center", String.format("%.2f %.2f", center.x, center.y));

        int a90;
        if (this.angle > 0) {
            a90 = (int)(Math.ceil(this.startAngle / 90.0f)) * 90;
        } else {
            a90 = (int)(Math.floor(this.startAngle / 90.0f)) * 90;
        }
        float amax = (float)this.startAngle + (float)this.angle;
        while (true) {
            //Log.d("max", String.format("%d %.02f", a90, amax));
            if (this.angle > 0) {
                if ((float)a90 > amax)
                    break;
            } else {
                if ((float)a90 < amax)
                    break;
            }

            int a90_ = a90 % 360;
            if (a90_ == 90 || a90_ == -270) {
                bounds.union(this.centerPoint.x, this.oval.bottom);
                //Log.d("steps +", String.format("gora %.2f %.2f", center.x, ellipseBounds.top));
                //Log.d("bounds", bounds.toString());
            } else
            if (a90_ == 180 || a90_ == -180) {
                bounds.union(this.oval.left, this.centerPoint.y);
                //Log.d("steps +", String.format("lewy %.2f %.2f", ellipseBounds.left, center.y));
                //Log.d("bounds", bounds.toString());
            } else
            if (a90_ == 270 || a90_ == -90) {
                bounds.union(this.centerPoint.x, this.oval.top);
                //Log.d("steps +", String.format("dol %.2f %.2f", center.x, ellipseBounds.bottom));
                //Log.d("bounds", bounds.toString());
            } else
            if (a90_ == 0) {
                bounds.union(this.oval.right, this.centerPoint.y);
                //Log.d("steps +", String.format("prawy %.2f %.2f", ellipseBounds.right, center.y));
                //Log.d("bounds", bounds.toString());
            }
            if (this.angle > 0) {
                a90 += 90;
            } else {
                a90 -= 90;
            }
        }

        return bounds;
    }

}
