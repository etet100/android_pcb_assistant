package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.drawing.templates.SizeF;
import bts.pcbassistant.drawing.view.ExtendedCanvas;
import bts.pcbassistant.utils.Helpers;

public class FilledRectangleDrawable extends BaseDrawable implements DimensionDrawable {
    protected PointF center;
    protected Layer layer;
    protected Rotation rotation;
    protected SizeF size;

    public FilledRectangleDrawable(PointF pos, SizeF size, Layer layer, Rotation rotation) {
        this.center = pos;
        this.size = size;
        this.rotation = rotation;
        this.layer = layer;

        //wymiary
        float w_ = this.size.width() / 2.0f;
        float h_ = this.size.height() / 2.0f;
        float[] points = new float[] {
                - w_, - h_,
                + w_, - h_,
                + w_, + h_,
                - w_, + h_
        };
        points = Helpers.rotateAndMovePoints(points, pos, rotation);
        bounds = new RectF(pos.x, pos.y, pos.x, pos.y);
        bounds.union(points[0], points[1]);
        bounds.union(points[2], points[3]);
        bounds.union(points[4], points[5]);
        bounds.union(points[6], points[7]);
    }

    public void Draw(ExtendedCanvas c) {
        c.translateRotate(this.center, this.rotation);
        Paint paint = this.layer.getPaint();
        paint.setStyle(Style.FILL);
        c.drawRect((-this.size.width()) / 2.0f, (-this.size.height()) / 2.0f, this.size.width() / 2.0f, this.size.height() / 2.0f, paint);
        c.restore();
    }

    @Override
    public RectF getDimension() {
        return bounds;
    }
}
