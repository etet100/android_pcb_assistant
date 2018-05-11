package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.RoundFilledRectangleDrawable;
import bts.pcbassistant.utils.Helpers;

public class SmdTemplate extends RoundFilledRectangleTemplate {
    public SmdTemplate(PointF center, SizeF size, Layer layer, Rotation rotation, float roundness) {
        super(center, size, layer, rotation, roundness);
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            Layer mLayer = rot.isMirrored() ? layerManager.getLayer(16) : this.layer;
            mLayer.addDrawable(new RoundFilledRectangleDrawable(Helpers.moveRotatePoint(this.relPosition, center, rot), this.size, mLayer, Rotation.add(rot, this.rotation), this.roundness));
        }
        return null;
    }
}
