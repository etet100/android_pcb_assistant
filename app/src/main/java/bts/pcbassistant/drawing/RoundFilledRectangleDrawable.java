package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.drawing.templates.SizeF;
import bts.pcbassistant.drawing.view.ExtendedCanvas;

public class RoundFilledRectangleDrawable extends FilledRectangleDrawable {
    protected float roundness;

    public RoundFilledRectangleDrawable(PointF pos, SizeF size, Layer layer, Rotation rotation, float roundness) {
        super(pos, size, layer, rotation);
        this.roundness = roundness;
    }

    public void Draw(ExtendedCanvas c) {
        c.translateRotate(this.center, this.rotation);
        Paint paint = this.layer.getPaint();
        paint.setStyle(Style.FILL);
        if (this.roundness <= 0.0f) {
            c.drawRect((-this.size.width()) / 2.0f, (-this.size.height()) / 2.0f, this.size.width() / 2.0f, this.size.height() / 2.0f, paint);
        } else {
            float drawRoundness = Math.min(this.size.width, this.size.height) * (this.roundness / 200.0f);
            c.drawRoundRect(new RectF((-this.size.width) / 2.0f, (-this.size.height) / 2.0f, this.size.width / 2.0f, this.size.height / 2.0f), drawRoundness, drawRoundness, paint);
        }
        c.restore();
    }
}
