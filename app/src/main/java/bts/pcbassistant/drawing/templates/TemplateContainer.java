package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.Iterator;
import java.util.LinkedList;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.Rotation;

public class TemplateContainer extends LinkedList<Template> implements ITemplate {
    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        Iterator it = iterator();
        while (it.hasNext()) {
            ((Template) it.next()).AddCopyToLayer(layerManager, center, rot);
        }
        return null;
    }

    public RectF ExtendBounds(RectF rect, PointF center, Rotation rot) {
        return rect;
    }
}
