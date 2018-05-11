package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.view.ExtendedCanvas;

/*

tekst bez SPIN obraca się tak aby był czytelny przy kątach w zakresie 90 do 180 stopni

 */

/* WERSJA ANDROID 5 */
public class SchematicTextDrawable5 extends BaseTextDrawable {

    private boolean mirrored;

    public SchematicTextDrawable5(PointF pos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot, AlignType align) {
        super(pos, layer, text, font, size, ratio, width, rot, align);
        mirrored = rot.isMirrored();
        rot.resetMirrored();
        if (rot.is90_180()) {
            this.align = EagleAlign.invert(this.align);
            rot.setAngle(rot.getAngle() - 180.0f);
        }
        if (mirrored) {
            if (rot.getAngle() == 180.0f || rot.getAngle() == 0.0f) {
                this.align = EagleAlign.mirror(this.align);
            } else {
                this.align = EagleAlign.flip(this.align);
            }
        }
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
            //MULTILINE
            float h = (LINE_RATIO * this.size);//(((m.descent - m.ascent) + m.leading) / 2.0f);
            float y = 0;//-(h * (EagleAlign.getVerticalAlign(this.align) * (this.lines.length+1)));
            //TODO ujednolicić dla innych TextDrawable
            if (EagleAlign.isBottom(align))
                y -= (this.lines.length - 1) * h;
            else
            if (EagleAlign.isTop(align))
                y += h;
            else
                y -= - (h / 2.0f) + ((this.lines.length - 1) * h) / 2.0f;

            float x = 0.0f;
            //popraw tryb przyklejenia do prawej
            if (mirrored) {
                if (paint.getTextAlign() == Paint.Align.RIGHT) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    x -= c.calcMultilineWidth(lines, paint);
                }
            }

            c.drawMultilineText(this.lines, x, y, paint, LINE_RATIO * this.size);
        } else {
            //SINGLELINE
            c.drawText(this.text, 0.0f, (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getVerticalAlign(this.align), paint);
        }
        c.restore();
        if (this.text.length() > 0) {
            c.drawLinesFixedWidth(new float[]{0.0f, -0.25f, 0.0f, 0.25f, -0.25f, 0.0f, 0.25f, 0.0f}, paint, 1.0f);
        }
        c.restore();
    }
}
