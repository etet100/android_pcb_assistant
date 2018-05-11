package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.FilledRectangleDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.utils.Helpers;

public class FilledRectangleTemplate extends Template {
    protected Rotation rotation;
    protected SizeF size;

    public FilledRectangleTemplate(PointF center, SizeF size, Layer layer, Rotation rotation) {
        super(center, layer);
        this.rotation = rotation;
        this.size = size;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            this.layer.addDrawable(new FilledRectangleDrawable(Helpers.moveRotatePoint(this.relPosition, center, rot), this.size, this.layer, Rotation.add(rot, this.rotation)));
        }
        return null;
    }
}
