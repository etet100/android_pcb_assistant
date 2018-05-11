package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;

public abstract class Template implements ITemplate {
    protected Layer layer;
    protected PointF relPosition;

    public Template(PointF relPos, Layer layer) {
        this.relPosition = relPos;
        this.layer = layer;
    }

    public Layer getLayer() {
        return this.layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    //DODANE
    public void setText(String s) { }

    public RectF ExtendBounds(RectF rect, PointF center, Rotation rot) {
        return rect;
    }
}
