package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.utils.Helpers;
import bts.pcbassistant.utils.Units;
import bts.pcbassistant.utils.Units.Unit;

public class ViaTemplate extends PadTemplate {
    protected static float rlMaxViaBottom;
    protected static float rlMaxViaTop;
    protected static float rlMinViaBottom;
    protected static float rlMinViaTop;
    protected static float rvViaBottom;
    protected static float rvViaTop;

    static {
        rvViaTop = 0.25f;
        rvViaBottom = 0.25f;
        rlMinViaTop = Units.convertToMm(8.0f, Unit.Mil);
        rlMaxViaTop = Units.convertToMm(20.0f, Unit.Mil);
        rlMinViaBottom = Units.convertToMm(8.0f, Unit.Mil);
        rlMaxViaBottom = Units.convertToMm(20.0f, Unit.Mil);
    }

    public ViaTemplate(LayerManager layerManager, PointF pos, double drill, double dimension, Type type) {
        super(layerManager, pos, drill, dimension, type);
    }

    public ViaTemplate(LayerManager layerManager, PointF pos, double drill, Type type, Rotation mRot) {
        super(layerManager, pos, drill, type, mRot);
    }

    public ViaTemplate(LayerManager layerManager, PointF pos, double drill, Type type) {
        super(layerManager, pos, drill, type);
    }

    public ViaTemplate(LayerManager layerManager, PointF pos, Layer layer, double drill, double dimension, Type type, Rotation mRot) {
        super(layerManager, pos, layer, drill, dimension, type, mRot);
    }

    protected float getPadSize(LayerManager layerManager, double drillSize, double dimension) {
        if (dimension > 0.0d) {
            return (float) dimension;
        }
        int shownLayers = 1;
        if (layerManager.isShown(1)) {
            if (layerManager.isShown(16)) {
                shownLayers = 3;
            }
        } else if (layerManager.isShown(16)) {
            shownLayers = 2;
        }
        double relativeSize = Helpers.selectValue((double) rvViaTop, (double) rvViaBottom, shownLayers);
        double maxSize = Helpers.selectValue((double) rlMaxViaTop, (double) rlMaxViaBottom, shownLayers);
        return (float) ((2.0d * Math.max(Math.min(drillSize * relativeSize, maxSize), Helpers.selectValue((double) rlMinViaTop, (double) rlMinViaBottom, shownLayers))) + drillSize);
    }
}
