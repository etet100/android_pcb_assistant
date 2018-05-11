package bts.pcbassistant.drawing;

import android.graphics.PointF;

import bts.pcbassistant.drawing.view.ExtendedCanvas;

public class CrossDrawable extends BaseDrawable {
    private Layer layer;
    private PointF pos;
    private float size;
    private float width;

    public CrossDrawable(PointF pos, float width, float size, Layer layer) {
        this.pos = pos;
        this.width = width;
        this.size = size;
        this.layer = layer;
    }

    public void Draw(ExtendedCanvas c) {
        if (this.layer != null) {
            c.drawCrossFixedWidth(this.pos, this.layer.getPaint(), this.size, this.width);
        }
    }
}
