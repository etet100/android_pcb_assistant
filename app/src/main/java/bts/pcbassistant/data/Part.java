package bts.pcbassistant.data;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Map;

public class Part {
    String deviceset;
    String library;
    String name;
    String value;

    Bounds boardBounds;
    Bounds schematicBounds;
    Map<String, Bounds> schematicSubbounds;

    public Part(String library, String name, String value) {
        this.library = library;
        this.name = name;
        this.value = value;

        this.boardBounds = new Bounds();
        this.schematicBounds = new Bounds();
        this.schematicSubbounds = new HashMap<>();
    }

    //pusty konstruktor żeby kompilator się odczepił
    public Part() {
    }

    public void addGate(String name) {
        schematicSubbounds.put(name, new Bounds());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLibrary() {
        return this.library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getDeviceset() {
        return this.deviceset;
    }

    public void setDeviceset(String deviceset) {
        this.deviceset = deviceset;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Rect getTransformedBounds(EagleDataSource.TYPE sourceType, Matrix matrix) {
        switch (sourceType) {
            case Board:
                return boardBounds.getTransformed(matrix);
            case Schematic:
                return schematicBounds.getTransformed(matrix);
        }
        return null;
    }

    public Rect getSchematicTransformedBounds(String gate, Matrix matrix) {
        return schematicSubbounds.get(gate).getTransformed(matrix);
    }

    public void extendBounds(EagleDataSource.TYPE sourceType, String gate, PointF p) {
        switch (sourceType) {
            case Board:
                boardBounds.extend(p);
                break;
            case Schematic:
                schematicBounds.extend(p);
                schematicSubbounds.get(gate).extend(p);
                break;
        }
    }

    public void extendBounds(EagleDataSource.TYPE sourceType, PointF p) {
        this.extendBounds(sourceType, "", p);
    }

    //public boolean hasBounds() {
    //    return (bounds != null);
    //}

    public RectF getSchematicSubbounds() {
        if (schematicSubbounds.isEmpty())
            return null;
        for (Bounds b : schematicSubbounds.values())
            return b;
        return null;
    }

    public RectF getBounds(EagleDataSource.TYPE sourceType) {
        switch (sourceType) {
            case Board:
                return boardBounds;
            case Schematic:
                return getSchematicSubbounds();
        }
        return null;
    }

    public Part inSchematicBounds(PointF point) {
        for (Map.Entry<String, Bounds> b : schematicSubbounds.entrySet()) {
            if (b.getValue().contains(point.x, point.y))
                return schematicSubbounds.size()>1? new SubPart(this, b.getKey()) : this;
        }
        return null;
    }

    public Part inBounds(EagleDataSource.TYPE sourceType, PointF point) {
        switch (sourceType) {
            case Board:
                if (boardBounds.contains(point.x, point.y))
                    return this;
                break;
            case Schematic:
                return inSchematicBounds(point);
        }
        return null;
    }

}
