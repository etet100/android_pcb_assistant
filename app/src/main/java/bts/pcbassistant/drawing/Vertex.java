package bts.pcbassistant.drawing;

import android.graphics.PointF;

public class Vertex extends PointF {
    private float curve;

    public Vertex(float x, float y, float curve) {
        super(x, y);
        this.curve = curve;
    }

    public Vertex(PointF p, float curve) {
        super(p.x, p.y);
        this.curve = curve;
    }

    public float getCurve() {
        return this.curve;
    }

    public void setCurve(float curve) {
        this.curve = curve;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
