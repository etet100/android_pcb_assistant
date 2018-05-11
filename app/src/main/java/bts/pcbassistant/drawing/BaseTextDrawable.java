package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;

import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.view.ExtendedCanvas;


public abstract class BaseTextDrawable extends BaseDrawable {
    AlignType align;
    Layer layer;
    PointF pos;
    Rotation rot;
    float size;
    int ratio;
    String text;
    String[] lines;
    String font;
    float width;
    float fontRatio;
    boolean multiline;
    Typeface typeface;

    public static Typeface vectorTypeface;
    public static Typeface vectorTypeface15;
    public static Typeface vectorTypeface20;

    protected static final float LINE_RATIO = 84.0f;
    protected static final float SCALE_DOWN_RATIO = 0.018f;

    public BaseTextDrawable(PointF pos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot, AlignType align) {
        this.rot = rot;
        this.align = align;
        this.ratio = ratio;
        this.layer = layer;
        this.font = font;
        this.size = size;
        this.pos = pos;
        this.width = width;
        this.text = text;
        this.lines = text.split("\n");
        this.multiline = this.lines.length > 1;

        if (font.equals("vector")) {
            if (ratio >= 20)
                this.typeface = vectorTypeface20;
            else
            if (ratio >= 15)
                this.typeface = vectorTypeface15;
            else
                this.typeface = vectorTypeface;
            this.fontRatio = 100.0f;
        } else {
            this.typeface = Typeface.DEFAULT;
            this.fontRatio = 80.0f;
        }
    }

    public abstract void Draw(ExtendedCanvas c);

    protected void drawCross(ExtendedCanvas c, Paint paint) {
        if (this.text.length() > 0) {
            c.drawLinesFixedWidth(new float[]{0.0f, -0.25f, 0.0f, 0.25f, -0.25f, 0.0f, 0.25f, 0.0f}, paint, 1.0f);
        }
    }

}
