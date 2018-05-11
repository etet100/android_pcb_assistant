package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;
import android.graphics.RectF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.Rotation;

public interface ITemplate {
    BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF pointF, Rotation rotation);
    RectF ExtendBounds(RectF rect, PointF center, Rotation rot);
}
