package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.RoundFilledRectangleDrawable;
import bts.pcbassistant.utils.Helpers;

public class RoundFilledRectangleTemplate extends FilledRectangleTemplate {
    protected float roundness;

    public RoundFilledRectangleTemplate(PointF center, SizeF size, Layer layer, Rotation rotation, float roundness) {
        super(center, size, layer, rotation);
        this.roundness = roundness;
    }

    public void AddCopyToLayer(PointF center, Rotation rot) {
        if (this.layer != null) {
            this.layer.addDrawable(new RoundFilledRectangleDrawable(Helpers.moveRotatePoint(this.relPosition, center, rot), this.size, this.layer, Rotation.add(rot, this.rotation), this.roundness));
        }
    }
}
