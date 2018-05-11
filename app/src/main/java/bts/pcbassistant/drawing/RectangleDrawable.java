package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import bts.pcbassistant.drawing.templates.SizeF;
import bts.pcbassistant.drawing.view.ExtendedCanvas;

/*
wersja bez wypełnienie - sama ramka
 */

public class RectangleDrawable extends BaseDrawable {
    protected PointF center;
    protected Layer layer;
    protected Rotation rotation;
    protected SizeF size;

    public RectangleDrawable(PointF pos, SizeF size, Layer layer, Rotation rotation) {
        this.center = pos;
        this.size = size;
        this.rotation = rotation;
        this.layer = layer;
    }

    public void Draw(ExtendedCanvas c) {
        c.translateRotate(this.center, this.rotation);
        Paint paint = this.layer.getPaint();
        paint.setStyle(Style.STROKE);
        c.drawRect((-this.size.width()) / 2.0f, (-this.size.height()) / 2.0f, this.size.width() / 2.0f, this.size.height() / 2.0f, paint);
        c.restore();
    }
}
