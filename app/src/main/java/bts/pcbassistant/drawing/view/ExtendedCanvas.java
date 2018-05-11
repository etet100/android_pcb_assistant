package bts.pcbassistant.drawing.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;

import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.utils.MetricsHelpers;

public class ExtendedCanvas extends Canvas {

    float unitStrokeWidth;
    float minStrokeWidth;

    public ExtendedCanvas() {
        //w niektórych przypadkach nie jest używane translateRotate!
        //unitStrokeWidth = (1.0f / getMatrix().mapRadius(1.0f));
    }

    public float checkMinStrokeWidth(float width) {
        return width> minStrokeWidth ?width: minStrokeWidth;
    }

    public void translateRotate(PointF pos, Rotation rot) {
        save();
        translate(pos.x, pos.y);
        if (rot.isMirrored()) {
            scale(-1.0f, 1.0f);
        }
        rotate(rot.getAngle());

        //w niektórych przypadkach nie jest używane translateRotate!
        unitStrokeWidth = (1.0f / getMatrix().mapRadius(1.0f));
        //minimalna grubość linii zależna od DPI
        minStrokeWidth = unitStrokeWidth * MetricsHelpers.getMinStrokeWidth(); // 2 px minimum?
    }

    public void drawLineFixedWidth(float startX, float startY, float stopX, float stopY, Paint paint, float width) {
        paint.setStrokeWidth(unitStrokeWidth * width);
        super.drawLine(startX, startY, stopX, stopY, paint);
    }

    public void drawLinesFixedWidth(float[] pts, Paint paint, float width) {
        paint.setStrokeWidth(unitStrokeWidth * width);
        super.drawLines(pts, paint);
    }

    public void drawPathFixedWidth(Path path, Paint paint, float width) {
        paint.setStrokeWidth(unitStrokeWidth * width);
        super.drawPath(path, paint);
    }

    public void drawCrossFixedWidth(PointF center, Paint paint, float size, float width) {
        drawLinesFixedWidth(new float[]{center.x - size, center.y, center.x + size, center.y, center.x, center.y - size, center.x, center.y + size}, paint, width);
    }

    public float calcMultilineWidth(String[] lines, Paint paint) {
        //licz szerokość tekstu
        int textWidth = 0;
        Rect bounds = new Rect();
        for (int l = 0; l < lines.length; l++) {
            paint.getTextBounds(lines[l], 0, lines[l].length(), bounds);
            textWidth = Math.max(textWidth, bounds.width());
        }
        return textWidth;
    }

    public void drawMultilineText(String[] lines, float x, float y, Paint paint, float lineHeight) {
        //od pierwszego do ostatniego - pod krzyżykiem
        for (int l = 0; l < lines.length; l++) {
            drawText(lines[l], x, y, paint);
            y += lineHeight;
        }
    }

}
