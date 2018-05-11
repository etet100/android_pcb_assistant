package bts.pcbassistant.drawing;

import android.util.Log;

public class Rotation {
    public static final Rotation ZERO;
    private float angle;
    private boolean isMirrored;
    private boolean spin;

    static {
        ZERO = new Rotation(0.0f, false);
    }

    public Rotation(float angle, boolean isMirrored) {
        this.angle = angle;
        this.isMirrored = isMirrored;
        this.spin = false;
    }

    public Rotation(float angle, boolean isMirrored, boolean isSpin) {
        this(angle, isMirrored);
        this.spin = isSpin;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean isMirrored() {
        return this.isMirrored;
    }

    public void setMirrored(boolean isMirrored) {
        this.isMirrored = isMirrored;
    }

    public void resetMirrored() {
        setMirrored(false);
    }

    public static Rotation getRotation(String str) {
        try {
            return new Rotation(Float.parseFloat(str.replace("M", "").replace("S", "").replace("R", "")), str.contains("M"), str.contains("S"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Rotation ignoreMirror() {
        return new Rotation(this.getAngle(), false);
    }

    public static Rotation add(Rotation r1, Rotation r2) {
        float resAngle = r1.getAngle() + r2.getAngle();
        while (resAngle < 0.0f) {
            resAngle += 360.0f;
        }
        return new Rotation(resAngle % 360.0f, r1.isMirrored ^ r2.isMirrored);
    }

    public static boolean isMirrored(Rotation rot) {
        return rot.isMirrored();
    }

    public static void RotationTest() {
        Log.d("Test", add(new Rotation(90.0f, false), new Rotation(20.0f, false)).toString());
        Log.d("Test", add(new Rotation(90.0f, true), new Rotation(20.0f, false)).toString());
        Log.d("Test", add(new Rotation(90.0f, false), new Rotation(20.0f, true)).toString());
        Log.d("Test", add(new Rotation(90.0f, true), new Rotation(20.0f, true)).toString());
    }

    public String toString() {
        String prefix = "";
        if (this.isMirrored) {
            prefix = "M";
        }
        if (isSpin()) {
            prefix = prefix + "S";
        }
        return prefix + "R" + this.angle;
    }

    public Rotation ignore180_270() {
        if (getAngle() == 180.0f) {
            return new Rotation(0.0f, isMirrored);
        }
        if (getAngle() == 270.0f) {
            return new Rotation(90.0f, isMirrored);
        }
        return this;
    }

    public boolean is180_270() {
        return this.getAngle() >= 180.0f;
    }
    public boolean is90_180() {
        return (this.getAngle() > 90.0f && this.getAngle() <= 270.0f);
    }
/*
    public Rotation toggleMirrorIf180_270() {
        if (this.is90_180()) {
            return new Rotation(this.getAngle(), !this.isMirrored(), this.isSpin());
        } else
            return this;
    }
*/
    public boolean isSpin() {
        return this.spin;
    }

    public void setSpin(boolean spin) {
        this.spin = spin;
    }

    //poziomo
    public boolean isHorizontal() { return this.getAngle() == 0.0f ||this.getAngle() == 180.0f; }
    //pionowo
    public boolean isVertical() { return this.getAngle() == 90.0f ||this.getAngle() == 270.0f; }
}
