package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;

import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.view.ExtendedCanvas;

/*

tekst bez SPIN obraca się tak aby był czytelny przy kątach w zakresie 90 do 180 stopni

 */

/* WERSJA ANDROID 5 */
public class LabelDrawable5 extends BaseTextDrawable {

    private boolean mirrored;

    public LabelDrawable5(PointF pos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot) {
        super(pos, layer, text, font, size, ratio, width, rot, AlignType.CL);
        mirrored = rot.isMirrored();
        rot.resetMirrored();
        if (rot.is90_180()) {
            this.align = EagleAlign.invert(this.align);
            rot.setAngle(rot.getAngle() - 180.0f);
        }
        if (mirrored) {
            if (rot.isHorizontal()) {
                this.align = EagleAlign.mirror(this.align);
            } else {
                //przy 90 i 270 ten mirror chyba nic nie daje
                //this.align = EagleAlign.flip(this.align);
            }
        }
    }

    private float[] frame = null;
    private float offset;

    public void Draw(ExtendedCanvas c) {
        Paint paint = this.layer.getPaint();
        paint.setTypeface(this.typeface);
        paint.setLinearText(true);
        paint.setTextAlign(EagleAlign.getPaintAlign(this.align));
        paint.setTextSize(this.fontRatio * this.size);

        if (frame == null) {
            Rect bounds = new Rect();
            paint.getTextBounds(this.text, 0, this.text.length(), bounds);
            offset = (bounds.height() * SCALE_DOWN_RATIO * 0.65f);
            float w = bounds.width() * SCALE_DOWN_RATIO + offset;
            if (align == AlignType.CL) {
                if (rot.isHorizontal()) pos.x += offset; else pos.y += offset;
                // od prawej, strzałka w lewo
                frame = new float[] {
                        -offset, 0, 0, offset,
                        0, offset, w, offset,
                        w, offset, w, -offset,
                        w, -offset, 0, -offset,
                        0, -offset, -offset, 0
                };
            } else {
                if (rot.isHorizontal()) pos.x -= offset; else pos.y -= offset;
                // od lewej, strzałka skierowana w prawo
                frame = new float[] {
                        -w, offset, 0, offset,
                        0, offset, offset, 0,
                        offset, 0, 0, -offset,
                        0, -offset, -w, -offset,
                        -w, -offset, -w, offset
                };
                offset *= -1.0f;
            }
        }

        c.translateRotate(this.pos, this.rot);
        c.scale(1.0f, -1.0f);
        paint.setStyle(Style.FILL);
        FontMetrics m = paint.getFontMetrics();
        c.save();
        //c.scale(1.0f, -1.0f);
        c.scale(SCALE_DOWN_RATIO, SCALE_DOWN_RATIO);//(((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getRotatedVerticalAlign(this.align, this.rot));

        if (this.multiline) {
            //MULTILINE
            //nie wspierane !!!
        } else {
            //SINGLELINE
            c.drawText(this.text, 0.0f, (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getVerticalAlign(this.align), paint);
        }
        c.restore();

        paint.setStrokeWidth(0.25f);
        c.drawLines(frame, paint);

        if (this.text.length() > 0) {
            c.drawLinesFixedWidth(new float[]{-offset, -0.5f, -offset, 0.5f, -offset-0.5f, 0.0f, -offset+0.5f, 0.0f}, paint, 1.0f);
        }
        c.restore();
    }
}
