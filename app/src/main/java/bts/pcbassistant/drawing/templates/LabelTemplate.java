package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.EagleAlign;
import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.LabelDrawable5;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.NoSpinTextDrawable5;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.SchematicTextDrawable5;
import bts.pcbassistant.drawing.SpinTextDrawable5;
import bts.pcbassistant.utils.Helpers;

public class LabelTemplate extends Template {
    Rotation rotation;
    float size;
    String text;
    String font;
    int ratio;
    float width;
    boolean xref;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LabelTemplate(PointF relPos, Layer layer, String font, float size, int ratio, Rotation rot, boolean xref) {
        super(relPos, layer);
        this.rotation = rot;
        this.font = font;
        this.size = size;
        this.ratio = ratio;
        this.xref = xref;
    }

    public void AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot, String newText) {
        if (this.layer != null) {
            if (xref) {
                layer.addDrawable(new LabelDrawable5(Helpers.moveRotatePoint(this.relPosition, center, rot), this.layer, newText, this.font, this.size, this.ratio, this.width, Rotation.add(rot, this.rotation)));
            } else {
                layer.addDrawable(new SchematicTextDrawable5(Helpers.moveRotatePoint(this.relPosition, center, rot), this.layer, newText, this.font, this.size, this.ratio, this.width, Rotation.add(rot, this.rotation), EagleAlign.getAlign("bottom-left")));
            }
        }
    }

    @Override
    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF pointF, Rotation rotation) {
        this.AddCopyToLayer(layerManager, pointF, rotation, text);
        return null;
    }
}
