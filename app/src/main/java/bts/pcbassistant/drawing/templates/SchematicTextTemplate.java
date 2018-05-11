package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.SchematicTextDrawable5;
import bts.pcbassistant.utils.Helpers;

public class SchematicTextTemplate extends TextTemplate {
    public SchematicTextTemplate(PointF relPos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot, AlignType align) {
        super(relPos, layer, text, font, size, ratio, width, rot, align, true);
    }

    @Override
    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot, String newText) {
        if (this.layer != null) {
            Layer newLayer = this.layer;
            if (rot.isMirrored() && (this.layer.getNumber() == 25 || this.layer.getNumber() == 51 || this.layer.getNumber() == 27)) {
                newLayer = layerManager.getLayer(this.layer.getNumber() + 1);
            }
            newLayer.addDrawable(new SchematicTextDrawable5(Helpers.moveRotatePoint(this.relPosition, center, rot), newLayer, newText, this.font, this.size, this.ratio, this.width, Rotation.add(rot, this.rotation), this.align));
        }
        return null;
    }
}
