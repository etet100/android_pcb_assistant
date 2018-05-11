package bts.pcbassistant.drawing.templates;

import android.graphics.Path;
import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.IDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.PadDrawable;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.utils.Helpers;
import bts.pcbassistant.utils.Units;
import bts.pcbassistant.utils.Units.Unit;

public class PadTemplate extends Template {
    protected static float rlMaxPadBottom;
    protected static float rlMaxPadTop;
    protected static float rlMinPadBottom;
    protected static float rlMinPadTop;
    protected static float rvPadBottom;
    protected static float rvPadTop;
    private double dimension;
    Path drawable;
    private double drill;
    IDrawable hole;
    private Rotation rotation;
    private Type type;

    public enum Type {
        Square,
        Round,
        Octagon,
        Long,
        Offset
    }

    static {
        rvPadTop = 0.25f;
        rvPadBottom = 0.25f;
        rlMinPadTop = Units.convertToMm(10.0f, Unit.Mil);
        rlMaxPadTop = Units.convertToMm(20.0f, Unit.Mil);
        rlMinPadBottom = Units.convertToMm(10.0f, Unit.Mil);
        rlMaxPadBottom = Units.convertToMm(20.0f, Unit.Mil);
    }

    public PadTemplate(LayerManager layerManager, PointF pos, Layer layer, double drill, double dimension, Type type, Rotation mRot) {
        super(pos, layer);
        this.type = type;
        this.drill = drill;
        this.rotation = mRot;
        this.dimension = (double) getPadSize(layerManager, drill, dimension);
    }

    public PadTemplate(LayerManager layerManager, PointF pos, double drill, double dimension, Type type) {
        this(layerManager, pos, layerManager.getLayer(17), drill, dimension, type, new Rotation(0.0f, false));
    }

    public PadTemplate(LayerManager layerManager, PointF pos, double drill, Type type) {
        this(layerManager, pos, layerManager.getLayer(17), drill, -1.0d, type, new Rotation(0.0f, false));
    }

    public PadTemplate(LayerManager layerManager, PointF pos, double drill, Type type, Rotation mRot) {
        this(layerManager, pos, layerManager.getLayer(17), drill, -1.0d, type, mRot);
    }

    public static Type getType(String str) {
        if (str == null) {
            return Type.Round;
        }
        if (str.equalsIgnoreCase("square")) {
            return Type.Square;
        }
        if (str.equalsIgnoreCase("octagon")) {
            return Type.Octagon;
        }
        if (str.equalsIgnoreCase("offset")) {
            return Type.Offset;
        }
        if (str.equalsIgnoreCase("round")) {
            return Type.Round;
        }
        return Type.Long;
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
        double relativeSize = Helpers.selectValue((double) rvPadTop, (double) rvPadBottom, shownLayers);
        double maxSize = Helpers.selectValue((double) rlMaxPadTop, (double) rlMaxPadBottom, shownLayers);
        return (float) ((2.0d * Math.max(Math.min(drillSize * relativeSize, maxSize), Helpers.selectValue((double) rlMinPadTop, (double) rlMinPadBottom, shownLayers))) + drillSize);
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            BaseDrawable drawable = new PadDrawable(Helpers.moveRotatePoint(this.relPosition, center, rot), this.layer, this.drill, this.dimension, this.type, Rotation.add(rot, this.rotation));
            this.layer.addDrawable(drawable);
            return drawable;
        }
        return null;
    }
}
