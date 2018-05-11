package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.WireDrawable;
import bts.pcbassistant.utils.Helpers;

public class WireTemplate extends Template {
    double angle;
    PointF endPoint;
    String style;
    double width;

    public WireTemplate(PointF pos, Layer layer, PointF endPoint, double width, double angle, String style) {
        super(pos, layer);
        this.endPoint = endPoint;
        this.width = width;
        this.angle = angle;
        this.style = style;
    }

    public WireDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            WireDrawable wireDrawable = new WireDrawable(Helpers.moveRotatePoint(this.relPosition, center, rot), this.layer, Helpers.moveRotatePoint(this.endPoint, center, rot), this.width, this.angle, this.style);
            this.layer.addDrawable(wireDrawable);
            return wireDrawable;
        }
        return null;
    }

    public RectF ExtendBounds(RectF rect, PointF center, Rotation rot) {
        PointF p1 = Helpers.moveRotatePoint(this.relPosition, center, rot);
        PointF p2 = Helpers.moveRotatePoint(this.endPoint, center, rot);

        rect.union(p1.x, p1.y);
        rect.union(p2.x, p2.y);
        return rect;
    }
}
