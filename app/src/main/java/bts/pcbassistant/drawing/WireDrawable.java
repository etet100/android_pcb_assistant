package bts.pcbassistant.drawing;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.drawing.view.ExtendedCanvas;
import bts.pcbassistant.utils.Helpers;
import bts.pcbassistant.utils.PathEffectHelpers;
import de.lighti.clipper.Paths;

public class WireDrawable extends BaseDrawable implements DimensionDrawable, IOffsetableDrawable, ISelectableDrawable {
    double curve;
    PointF endPoint;
    Layer layer;
    PointF startPoint;
    PathEffectHelpers.Effect style;
    Path path;
    double width;
    boolean selected;
    Arc arc;

    public Path toAndroidPath(de.lighti.clipper.Path path, boolean closed) {
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

    public WireDrawable(PointF pos, Layer layer, PointF endPoint, double width, double curve, String style) {
        this.endPoint = endPoint;
        this.width = width;
        this.curve = curve;
        this.layer = layer;
        this.startPoint = pos;
        this.style = PathEffectHelpers.fromString(style);
        this.path = new Path();

        if (this.curve == 0.0d) {
            //w ten sposób sortowanie punktów będzie poprawne?
            bounds = new RectF(this.startPoint.x, this.startPoint.y, this.startPoint.x, this.startPoint.y);
            bounds.union(this.endPoint.x, this.endPoint.y);

            //clipper.execute(Clipper.ClipType.UNION, paths);


        } else {
            //bounds = new RectF(this.startPoint.x, this.startPoint.y, this.startPoint.x, this.startPoint.y);
            arc = new Arc(this.startPoint, this.endPoint, this.curve);
            arc.addToPath(path);
            this.bounds = arc.getBounds();

            //arc = Helpers.getArc(this.startPoint, this.endPoint, this.curve);
            /*
            RectF ellipseBounds = arc.getOval();
            PointF center = arc.getCenter();
            float a1 = arc.getStartAngle();
            //path.addArc(ellipseBounds, ((Float) arc[0]).floatValue(), (float) this.curve);

            //w ten sposób sortowanie punktów będzie poprawne?
            bounds = new RectF(this.startPoint.x, this.startPoint.y, this.startPoint.x, this.startPoint.y);
            bounds.union(this.endPoint.x, this.endPoint.y);


            //if (a1 < 0) a1 = 360 + a1;
            //Log.d("kat org i nowy, curve", String.format("%.2f %.2f  %.02f", ((Float) arc[0]).floatValue(), a1, curve));
            //Log.d("center", String.format("%.2f %.2f", center.x, center.y));

            int a90;
            if (curve > 0) {
                a90 = (int)(Math.ceil(a1 / 90.0f)) * 90;
            } else {
                a90 = (int)(Math.floor(a1 / 90.0f)) * 90;
            }
            float amax = a1 + (float)curve;
            while (true) {
                //Log.d("max", String.format("%d %.02f", a90, amax));
                if (curve > 0) {
                    if ((float)a90 > amax)
                        break;
                } else {
                    if ((float)a90 < amax)
                        break;
                }

                int a90_ = a90 % 360;
                if (a90_ == 90 || a90_ == -270) {
                    bounds.union(center.x, ellipseBounds.bottom);
                    //Log.d("steps +", String.format("gora %.2f %.2f", center.x, ellipseBounds.top));
                    //Log.d("bounds", bounds.toString());
                } else
                if (a90_ == 180 || a90_ == -180) {
                    bounds.union(ellipseBounds.left, center.y);
                    //Log.d("steps +", String.format("lewy %.2f %.2f", ellipseBounds.left, center.y));
                    //Log.d("bounds", bounds.toString());
                } else
                if (a90_ == 270 || a90_ == -90) {
                    bounds.union(center.x, ellipseBounds.top);
                    //Log.d("steps +", String.format("dol %.2f %.2f", center.x, ellipseBounds.bottom));
                    //Log.d("bounds", bounds.toString());
                } else
                if (a90_ == 0) {
                    bounds.union(ellipseBounds.right, center.y);
                    //Log.d("steps +", String.format("prawy %.2f %.2f", ellipseBounds.right, center.y));
                    //Log.d("bounds", bounds.toString());
                }
                if (curve > 0) {
                    a90 += 90;
                } else {
                    a90 -= 90;
                }
            }
            */
//            Log.d("kat", String.format("%.2f %.2f", ((Float) arc[0]).floatValue(), (float) this.curve));
//            path.computeBounds(bounds, false);
        }
    }

    public WireDrawable(PointF pos, Layer layer, PointF endPoint, double width, double curve) {
        this(pos, layer, endPoint, width, curve, null);
    }

    public void Draw(ExtendedCanvas c) {
        Paint layerPaint = selected?this.layer.getSelectedPaint():this.layer.getPaint();
        layerPaint.setStyle(Style.STROKE);

        DashPathEffect effect = PathEffectHelpers.getDashPathEffect(this.style);
        if (effect != null) {
            layerPaint.setPathEffect(effect);
        }

        if (this.curve != 0.0d) {

            /*
            de.lighti.clipper.Path clipperPath = new de.lighti.clipper.Path();
            clipperPath.add(new de.lighti.clipper.Point.LongPoint((int) (this.startPoint.x * 100.0f), (int) (this.startPoint.y * 100.0f)));
            clipperPath.add(new de.lighti.clipper.Point.LongPoint((int) (this.endPoint.x * 100.0f), (int) (this.endPoint.y * 100.0f)));
            */
/*
            de.lighti.clipper.Path clipperPath = arc.getClipperPath();

            //de.lighti.clipper.ClipperOffset offset = new de.lighti.clipper.ClipperOffset(2, 3);
            //offset.addPath(clipperPath, de.lighti.clipper.Clipper.JoinType.ROUND, de.lighti.clipper.Clipper.EndType.OPEN_ROUND);

            de.lighti.clipper.Paths paths = new de.lighti.clipper.Paths();
            paths.add(clipperPath);
            //Log.d("abc", String.format("%d %d", (int)(this.width * 100.0f), (int)(this.width * 100.0f) + 100));
            //        offset.execute(paths, (this.width>0?((int)(this.width * 50.0f)):0) + 100f);
            //offset.execute(paths, 50);

            for (de.lighti.clipper.Path patha : paths) {
                Path p = this.toAndroidPath(patha, false);
                int col = layerPaint.getColor();
                layerPaint.setColor(Color.GREEN);
                layerPaint.setStrokeWidth(0.5f);
                c.drawPath(p, layerPaint);
                layerPaint.setColor(col);
            }
*/
        }

        if (this.width > 0.0d) {
            layerPaint.setStrokeWidth(c.checkMinStrokeWidth((float)this.width));
            if (this.curve == 0.0d) {
                c.drawLine(this.startPoint.x, this.startPoint.y, this.endPoint.x, this.endPoint.y, layerPaint);
            } else {
                /*
                arc = Helpers.getArc(this.startPoint, this.endPoint, this.curve);
                path = new Path();*/
                //path.addArc((RectF) arc[1], ((Float) arc[0]).floatValue(), (float) this.curve);
                c.drawPath(path, layerPaint);
            }
        } else if (this.curve == 0.0d) {
            c.drawLineFixedWidth(this.startPoint.x, this.startPoint.y, this.endPoint.x, this.endPoint.y, layerPaint, 1.0f);
        } else {
            //arc = Helpers.getArc(this.startPoint, this.endPoint, this.curve);
            //path = new Path();
            //path.addArc((RectF) arc[1], ((Float) arc[0]).floatValue(), (float) this.curve);
            c.drawPathFixedWidth(path, layerPaint, 1.0f);
        }
        layerPaint.setPathEffect(null);
/*
        if (this.bounds != null) {
            c.drawRect(bounds, layerPaint);
        }
*/
    }

    public RectF getDimension() {
        return bounds;
    }

    @Override
    public Paths offset(int offsetDistance) {
        de.lighti.clipper.Path clipperPath;
        if (this.curve != 0.0d) {
            clipperPath = arc.getClipperPath();
        } else {
            clipperPath = new de.lighti.clipper.Path();
            clipperPath.add(new de.lighti.clipper.Point.LongPoint((int) (this.startPoint.x * 100.0f), (int) (this.startPoint.y * 100.0f)));
            clipperPath.add(new de.lighti.clipper.Point.LongPoint((int) (this.endPoint.x * 100.0f), (int) (this.endPoint.y * 100.0f)));
        }

        de.lighti.clipper.ClipperOffset offset = new de.lighti.clipper.ClipperOffset(2, 3);
        offset.addPath(clipperPath, de.lighti.clipper.Clipper.JoinType.ROUND, de.lighti.clipper.Clipper.EndType.OPEN_ROUND);

        de.lighti.clipper.Paths paths = new de.lighti.clipper.Paths();
        //Log.d("abc", String.format("%d %d", (int)(this.width * 100.0f), (int)(this.width * 100.0f) + 100));
        offset.execute(paths, (this.width>0?((int)(this.width * 50.0f)):0) + (0.40d * 100.0f));
        return paths;
//        offset.execute(paths, 50);

        /*List<Path> pathsa = new ArrayList<>();
        for (de.lighti.clipper.Path patha : paths) {
            pathsa.add(this.toAndroidPath(patha, true));
        }
        return pathsa;*/
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
