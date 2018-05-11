package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.CircleDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.utils.Helpers;

public class JunctionTemplate extends Template {
    private final float junctionRadius;

    public JunctionTemplate(PointF relPos, Layer layer) {
        super(relPos, layer);
        this.junctionRadius = 0.5f;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            this.layer.addDrawable(new CircleDrawable(Helpers.movePointF(this.relPosition, center.x, center.y), this.layer, 0.5f, 0.0f, true));
        }
        return null;
    }
}
