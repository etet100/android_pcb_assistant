package bts.pcbassistant.drawing.templates;

import android.graphics.RectF;

public class SizeF {
    public final float height;
    public final float width;

    public SizeF(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public SizeF(RectF rect) {
        this(rect.width(), rect.height());
    }

    public float width() {
        return this.width;
    }

    public float height() {
        return this.height;
    }
}
