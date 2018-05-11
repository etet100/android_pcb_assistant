package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import java.util.Iterator;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.CrossDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;

public class PackageTemplate extends TemplateContainer {
    private String name;

    public PackageTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot, String name, String value) {
        addPositionCross(layerManager, center, rot);
        Iterator it = iterator();
        //RectF bounds = new RectF();
        while (it.hasNext()) {
            Template item = (Template) it.next();
            if (item instanceof TextTemplate) {
                TextTemplate text = (TextTemplate) item;
                if (text.text.equalsIgnoreCase(">name")) {
                    text.AddCopyToLayer(layerManager, center, rot, name);
                } else if (text.text.equalsIgnoreCase(">value")) {
                    text.AddCopyToLayer(layerManager, center, rot, value);
                } else {
                    item.AddCopyToLayer(layerManager, center, rot);
                }
            } else {
                //Log.d("test", item.getClass().getName());
                item.AddCopyToLayer(layerManager, center, rot);
                //bounds = item.ExtendBounds(bounds, center, rot);
            }
        }
        /*Layer layer = Layermanager.getLayer(24);//dimensions
        layer.addDrawable(new FilledRectangleDrawable(
            new PointF(bounds.left, bounds.top),
            new SizeF(bounds.width(), bounds.height()),
            layer,
            rot
        ));*/
        return null;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        return AddCopyToLayer(layerManager, center, rot, "", "");
    }

    protected void addPositionCross(LayerManager layerManager, PointF center, Rotation rot) {
        Layer layer;
        if (rot.isMirrored()) {
            layer = layerManager.getLayer(24);
        } else {
            layer = layerManager.getLayer(23);
        }
        layer.addDrawable(new CrossDrawable(center, 1.0f, 0.4f, layer));
    }
}
