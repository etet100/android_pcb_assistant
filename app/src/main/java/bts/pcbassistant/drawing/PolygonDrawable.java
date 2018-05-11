package bts.pcbassistant.drawing;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.Iterator;
import java.util.LinkedList;

import bts.pcbassistant.drawing.view.ExtendedCanvas;
import bts.pcbassistant.utils.Helpers;

public class PolygonDrawable extends BaseDrawable { //extends LinkedList<Vertex>, implements IDrawable {
    private Layer layer;
    private float width;
    LinkedList<Vertex> list;

    public PolygonDrawable(Layer layer, float width) {
        this.layer = layer;
        this.width = width;
        this.list = new LinkedList<Vertex>();
    }

    public void add(Vertex v) {
        list.add(v);
    }

    public void Draw(ExtendedCanvas c) {
        Paint paint = this.layer.getPaint();
        Path path = new Path();
        boolean first = true;
        Vertex last = null;
        Iterator it = list.iterator(); // ZMIANA
        while (it.hasNext()) {
            Vertex v = (Vertex) it.next();
            if (first) {
                path.moveTo(v.x, v.y);
                first = false;
            } else if (last.getCurve() == 0.0f) {
                path.lineTo(v.x, v.y);
            } else {
                Arc arc = new Arc(last, v, (double) last.getCurve());
                //Object[] arc = Helpers.getArc(last, v, (double) last.getCurve());
                arc.addToPath(path);
                //path.addArc((RectF) arc[1], ((Float) arc[0]).floatValue(), last.getCurve());
                //path.moveTo(v.x, v.y);
            }
            last = v;
        }
        path.close();
        if (this.layer.getNumber() == 1 || this.layer.getNumber() == 16 || this.layer.getNumber() >= 91) {
            paint.setStyle(Style.STROKE);
            paint.setStrokeCap(Cap.BUTT);
            paint.setPathEffect(new DashPathEffect(new float[]{1.0f, 1.0f}, 0.0f));
            paint.setStrokeWidth(this.width);
            c.drawPath(path, paint);
        } else {
            paint.setStyle(Style.FILL);
            c.drawPath(path, paint);
            paint.setStyle(Style.STROKE);
            c.drawPath(path, paint);
        }
        paint.setStrokeCap(Cap.ROUND);
        paint.setPathEffect(null);
    }

    public void AddToLayer() {
        this.layer.addDrawable(this);
    }
}
