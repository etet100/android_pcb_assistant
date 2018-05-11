package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import java.util.LinkedList;
import java.util.List;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.PolygonDrawable;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.Vertex;
import bts.pcbassistant.utils.Helpers;

public class PolygonTemplate extends Template {
    private List<Vertex> vertices;
    private float width;

    public PolygonTemplate(Layer layer, float width) {
        super(new PointF(0.0f, 0.0f), layer);
        this.vertices = new LinkedList();
        this.width = width;
    }

    public void Add(Vertex v) {
        this.vertices.add(v);
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            PolygonDrawable polygon = new PolygonDrawable(this.layer, this.width);
            for (Vertex v : this.vertices) {
                polygon.add(new Vertex(Helpers.moveRotatePoint(v, center, rot), v.getCurve()));
            }
            this.layer.addDrawable(polygon);
        }
        return null;
    }
}
