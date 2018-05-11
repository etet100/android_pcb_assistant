package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.drawing.view.ExtendedCanvas;

public class CircleDrawable extends BaseDrawable implements DimensionDrawable {
    boolean fill;
    Layer layer;
    PointF pos;
    float radius;
    float width;

    public CircleDrawable(PointF pos, Layer layer, float radius, float width) {
        this.fill = false;
        this.radius = radius;
        this.pos = pos;
        this.layer = layer;
        this.width = width;

        bounds = new RectF(
                pos.x - radius, pos.y - radius,
                pos.x + radius, pos.y + radius
        );
    }

    public CircleDrawable(PointF pos, Layer layer, float radius, float width, boolean fill) {
        this(pos, layer, radius, width);
        this.fill = fill;
    }

    public void Draw(ExtendedCanvas c) {
        if (this.layer == null)
            return;

        Paint paint = this.layer.getPaint();
        if (this.fill) {
            paint.setStyle(Style.FILL);
        } else {
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(this.width);
        }
        c.drawCircle(this.pos.x, this.pos.y, this.radius, paint);
    }

    @Override
    public RectF getDimension() {
        return bounds;
    }
}
