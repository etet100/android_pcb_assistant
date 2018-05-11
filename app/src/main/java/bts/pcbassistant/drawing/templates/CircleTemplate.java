package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.CircleDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.utils.Helpers;

public class CircleTemplate extends Template {
    float radius;
    float width;

    public CircleTemplate(PointF relPos, Layer layer, float radius, float width) {
        super(relPos, layer);
        this.radius = radius;
        this.width = width;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            this.layer.addDrawable(new CircleDrawable(Helpers.moveRotatePoint(this.relPosition, center, rot), this.layer, this.radius, this.width));
        }
        return null;
    }
}
