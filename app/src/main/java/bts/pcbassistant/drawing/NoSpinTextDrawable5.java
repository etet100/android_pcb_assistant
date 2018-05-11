package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.view.ExtendedCanvas;
import bts.pcbassistant.utils.Helpers;

/*

tekst bez SPIN obraca się tak aby był czytelny przy kątach w zakresie 90 do 180 stopni

 */

/* WERSJA ANDROID 5 */
public class NoSpinTextDrawable5 extends BaseTextDrawable {

    public NoSpinTextDrawable5(PointF pos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot, AlignType align) {
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
            float h = (((m.descent - m.ascent) + m.leading) / 2.0f);
            float y = h * EagleAlign.getVerticalAlign(this.align);
            if (rot.is90_180()) {
                //c.save();

                Rect bounds = new Rect();
                int textWidth = 0;
                for (int l = 0; l < this.lines.length; l++) {
                    paint.getTextBounds(this.lines[l], 0, this.lines[l].length(), bounds);
                    textWidth = Math.max(textWidth, bounds.width());
                }
                //Log.d("textWidth", String.format("%s %d", this.text, textWidth));
/*
                c.drawLines(new float[] {
                        0, 0, 0, -this.lines.length*(1.47f * this.size),
                        0, -this.lines.length*(1.47f * this.size), textWidth,-this.lines.length*(1.47f * this.size)
                }, paint
                );*/
                Point mid = new Point(
                        (int)(textWidth / 2.0f),
                        (int)(-this.lines.length*(LINE_RATIO * this.size)/2.0f)
                );
                //c.drawCircle(mid.x, mid.y, 0.6f,paint);

                c.rotate(180.0f, mid.x, y + (mid.y * EagleAlign.getPositiveOrNegativeVerticalAlign(this.align)));
                for (int l = this.lines.length-1; l>=0; l--) {
                    c.drawText(this.lines[l], 0.0f, y, paint);
                    y -= (LINE_RATIO * this.size);
                }
                //c.restore();
            } else {
                if (EagleAlign.isTop(align)) {
                    //od pierwszego do ostatniego - pod krzyżykiem
                    for (int l = 0; l < this.lines.length; l++) {
                        c.drawText(this.lines[l], 0.0f, y, paint);
                        y += (LINE_RATIO * this.size);
                    }
                } else {
                    //od ostatniego do pierwszego - nad krzyżykiem
                    for (int l = this.lines.length - 1; l >= 0; l--) {
                        c.drawText(this.lines[l], 0.0f, y, paint);
                        y -= (LINE_RATIO * this.size);
                    }
                }
            }
            /*
            for (int l = 0; l < this.lines.length; l++) {
                if (rot.is90_180()) {
                    c.save();
                    Rect bounds = new Rect();
                    paint.getTextBounds(this.lines[l], 0, this.lines[l].length(), bounds);
                    Point mid = Helpers.midPoint(bounds);
                    c.drawCircle(mid.x, y + (mid.y * EagleAlign.getPositiveOrNegativeVerticalAlign(this.align)), 0.2f,paint);
                    c.rotate(180.0f, mid.x, y + (mid.y * EagleAlign.getPositiveOrNegativeVerticalAlign(this.align)));
                    //c.drawText(this.lines[l], 0.0f, y, paint);
                    c.restore();
                } else {
                    c.drawText(this.lines[l], 0.0f, y, paint);
                }
                y -= (1.47f * this.size);
            }
            */

            /*
            float y = (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getVerticalAlign(this.align);
            //od ostatniego do pierwszego
            for (int l = this.lines.length-1; l>=0; l--) {
                c.drawText(this.lines[l], 0.0f, y, paint);
                y -= (8.4f * this.size);
            }*/
        } else {
            if (rot.is90_180()) {
                c.save();
                Rect bounds = new Rect();
                paint.getTextBounds(this.text, 0, this.text.length(), bounds);
                Point mid = Helpers.midPoint(bounds);
                c.rotate(180.0f, mid.x, mid.y * EagleAlign.getPositiveOrNegativeVerticalAlign(this.align));
                c.drawText(this.text, 0.0f, (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getVerticalAlign(this.align), paint);
                c.restore();
            } else {
                c.drawText(this.text, 0.0f, (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getVerticalAlign(this.align), paint);
            }
        }
        c.restore();
        if (this.text.length() > 0) {
            c.drawLinesFixedWidth(new float[]{0.0f, -0.25f, 0.0f, 0.25f, -0.25f, 0.0f, 0.25f, 0.0f}, paint, 1.0f);
        }
        c.restore();
    }

        /*
    public void Draw(ExtendedCanvas c) {
        Paint paint = this.layer.getPaint();
        paint.setLinearText(true);
        paint.setTextAlign(EagleAlign.getRotatedPaintAlign(this.align, this.rot));
        paint.setStyle(Style.FILL);
        setFont(paint, this.font, this.ratio);
        paint.setTextSize(10 * this.size);
        c.translateRotate(this.pos, Rotation.ignoreMirror(Rotation.ignore180_270(this.rot)));
        FontMetrics m = paint.getFontMetrics();
        c.save();
        c.scale(1.0f, -1.0f);
        c.scale(0.18f, 0.18f, 0.0f, 0.0f);//(((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getRotatedVerticalAlign(this.align, this.rot));

        if (this.multiline) {
            float y = (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getRotatedVerticalAlign(this.align, this.rot);
            for (int l = 0; l < this.lines.length; l++) {
                c.drawText(this.lines[l], 0.0f, y, paint);
                y-=(8.5 * this.size);
            }
        } else {
            c.drawText(this.text, 0.0f, (((m.descent - m.ascent) + m.leading) / 2.0f) * EagleAlign.getRotatedVerticalAlign(this.align, this.rot), paint);
        }
        c.restore();
        if (this.text.length() > 0) {
            c.drawLinesFixedWidth(new float[]{0.0f, -0.25f, 0.0f, 0.25f, -0.25f, 0.0f, 0.25f, 0.0f}, paint, 1.0f);
        }
        c.restore();
    }
        */
}
