package bts.pcbassistant.drawing;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.drawing.templates.PadTemplate.Type;
import bts.pcbassistant.drawing.view.ExtendedCanvas;
import bts.pcbassistant.utils.Helpers;
import de.lighti.clipper.Clipper;
import de.lighti.clipper.Paths;

public class PadDrawable extends BaseDrawable implements DimensionDrawable, IOffsetableDrawable {
    Path drawable;
    private Layer layer;
    private PointF pos;
    private Rotation rotation;
    private float size;

    public PadDrawable(PointF pos, Layer layer, double drill, double dimension, Type type, Rotation mRot) {
        this.pos = pos;
        this.rotation = mRot;
        this.layer = layer;
        this.size = (float)dimension;
        generateDrawables(pos, drill, dimension, type, this.rotation);
    }

    private void generateDrawables(PointF posUZ, double drill, double dimension, Type type, Rotation rot) {
        PointF pos = new PointF(0.0f, 0.0f);
        float size = (float) dimension;
        Path path = new Path();
        switch (type) {
            case Square:
                path.addRect(pos.x - (size / 2.0f), pos.y - (size / 2.0f), pos.x + (size / 2.0f), pos.y + (size / 2.0f), Direction.CW);
                break;
            case Round:
                path.addCircle(pos.x, pos.y, size / 2.0f, Direction.CW);
                break;
            case Octagon:
                path.moveTo(pos.x - (size / 4.0f), pos.y - (size / 2.0f));
                path.lineTo(pos.x + (size / 4.0f), pos.y - (size / 2.0f));
                path.lineTo(pos.x + (size / 2.0f), pos.y - (size / 4.0f));
                path.lineTo(pos.x + (size / 2.0f), pos.y + (size / 4.0f));
                path.lineTo(pos.x + (size / 4.0f), pos.y + (size / 2.0f));
                path.lineTo(pos.x - (size / 4.0f), pos.y + (size / 2.0f));
                path.lineTo(pos.x - (size / 2.0f), pos.y + (size / 4.0f));
                path.lineTo(pos.x - (size / 2.0f), pos.y - (size / 4.0f));
                path.close();
                break;
            case Long:
                path.addRoundRect(new RectF(pos.x - size, pos.y - (size / 2.0f), pos.x + size, pos.y + (size / 2.0f)), size / 2.0f, size / 2.0f, Direction.CW);
                break;
            case Offset:
                path.addRoundRect(new RectF(pos.x - (0.5f * size), pos.y - (size / 2.0f), pos.x + (1.5f * size), pos.y + (size / 2.0f)), size / 2.0f, size / 2.0f, Direction.CW);
                break;
        }
        path.addCircle(pos.x, pos.y, (float) (drill / 2.0d), Direction.CCW);
        this.drawable = path;
        //wymiary
        bounds = new RectF(
                posUZ.x - (size / 2.0f), posUZ.y - (size / 2.0f),
                posUZ.x + (size / 2.0f), posUZ.y + (size / 2.0f)
        );
    }

    public void Draw(ExtendedCanvas c) {
        c.translateRotate(this.pos, this.rotation);
        Paint paint = this.layer.getPaint();
        paint.setStyle(Style.FILL);
        c.drawPath(this.drawable, paint);
        c.restore();
    }

    @Override
    public RectF getDimension() {
        return bounds;
    }

    @Override
    public Paths offset(int offsetDistance) {
        de.lighti.clipper.Path clipperPath = new de.lighti.clipper.Path();

        PointF p;

        Rotation rotation = new Rotation(0, false);

        p = Helpers.moveRotatePoint(new PointF(pos.x - (size / 2.0f), pos.y - (size / 1.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

        p = Helpers.moveRotatePoint(new PointF(pos.x + (size / 2.0f), pos.y - (size / 1.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

        p = Helpers.moveRotatePoint(new PointF(pos.x + (size / 1.0f), pos.y - (size / 2.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

        p = Helpers.moveRotatePoint(new PointF(pos.x + (size / 1.0f), pos.y + (size / 2.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

        p = Helpers.moveRotatePoint(new PointF(pos.x + (size / 2.0f), pos.y + (size / 1.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

        p = Helpers.moveRotatePoint(new PointF(pos.x - (size / 2.0f), pos.y + (size / 1.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

        p = Helpers.moveRotatePoint(new PointF(pos.x - (size / 1.0f), pos.y + (size / 2.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

        p = Helpers.moveRotatePoint(new PointF(pos.x - (size / 1.0f), pos.y - (size / 2.0f)), this.pos, rotation);
        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(p.x * 50.0f), (int)(p.y * 50.0f)));

//        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(this.startPoint.x * 100.0f), (int)(this.startPoint.y * 100.0f)));
//        clipperPath.add(new de.lighti.clipper.Point.LongPoint((int)(this.endPoint.x * 100.0f), (int)(this.endPoint.y * 100.0f)));

        de.lighti.clipper.ClipperOffset offset = new de.lighti.clipper.ClipperOffset(2, 3);
        offset.addPath(clipperPath, Clipper.JoinType.SQUARE, Clipper.EndType.CLOSED_POLYGON);

        de.lighti.clipper.Paths paths = new de.lighti.clipper.Paths();
        //Log.d("abc", String.format("%d %d", (int)(this.width * 100.0f), (int)(this.width * 100.0f) + 100));
        offset.execute(paths, (0.15d * 100.0f));
        return paths;
//        offset.execute(paths, 50);

        /*List<Path> pathsa = new ArrayList<>();
        for (de.lighti.clipper.Path patha : paths) {
            pathsa.add(this.toAndroidPath(patha, true));
        }
        return pathsa;*/
    }

}
