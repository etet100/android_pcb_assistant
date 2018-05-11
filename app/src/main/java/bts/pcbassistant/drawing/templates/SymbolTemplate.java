package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.CrossDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;

public class SymbolTemplate extends PackageTemplate {
    public SymbolTemplate(String name) {
        super(name);
    }

    protected void addPositionCross(LayerManager layerManager, PointF center, Rotation rot) {
        Layer layer = layerManager.getLayer(94);
        layer.addDrawable(new CrossDrawable(center, 1.0f, 0.4f, layer));
    }
}
