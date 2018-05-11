package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.view.ExtendedCanvas;


public class SpinTextDrawable5 extends BaseTextDrawable {

    public SpinTextDrawable5(PointF pos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot, AlignType align) {
        super(pos, layer, text, font, size, ratio, width, rot, align);
    }

    public void Draw(ExtendedCanvas c) {
        c.translateRotate(this.pos, this.rot);
        c.scale(1.0f, -1.0f);
        Paint paint = this.layer.getPaint();
        paint.setTypeface(this.typeface);
        paint.setLinearText(true);
        paint.setTextAlign(EagleAlign.getPaintAlign(this.align));
        paint.setTextSize(this.fontRatio * this.size);
        paint.setStyle(Style.FILL);
        FontMetrics m = paint.getFontMetrics();
        c.save();
        //c.scale(1.0f, -1.0f);
        c.scale(SCALE_DOWN_RATIO, SCALE_DOWN_RATIO);//(((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getRotatedVerticalAlign(this.align, this.rot));

        if (this.multiline) {
            float y = (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getVerticalAlign(this.align);
            //od ostatniego do pierwszego
            for (int l = this.lines.length-1; l>=0; l--) {
                c.drawText(this.lines[l], 0.0f, y, paint);
                y -= (LINE_RATIO * this.size);
            }
        } else {
            c.drawText(this.text, 0.0f, (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getVerticalAlign(this.align), paint);
        }
        c.restore();
        if (this.text.length() > 0) {
            c.drawLinesFixedWidth(new float[]{0.0f, -0.25f, 0.0f, 0.25f, -0.25f, 0.0f, 0.25f, 0.0f}, paint, 1.0f);
        }
        c.restore();
    }
}
