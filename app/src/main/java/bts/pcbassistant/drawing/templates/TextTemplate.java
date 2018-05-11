package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.NoSpinTextDrawable5;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.SpinTextDrawable5;
import bts.pcbassistant.utils.Helpers;

public class TextTemplate extends Template {
    AlignType align;
    boolean readable;
    Rotation rotation;
    float size;
    String text;
    String font;
    int ratio;
    float width;

    public TextTemplate(PointF relPos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot, AlignType align) {
        this(relPos, layer, text, font, size, ratio, width, rot, align, !rot.isSpin());
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextTemplate(PointF relPos, Layer layer, String text, String font, float size, int ratio, float width, Rotation rot, AlignType align, boolean readable) {
        super(relPos, layer);
        this.text = text;
        this.rotation = rot;
        this.font = font;
        this.align = align;
        this.size = size;
        this.ratio = ratio;
        this.width = width;
        boolean z = readable || !rot.isSpin();
        this.readable = z;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        AddCopyToLayer(layerManager, center, rot, this.text);
/*
        if (this.layer != null) {
            Layer newLayer = this.layer;
            if (rot.isMirrored() && (this.layer.getNumber() == 25 || this.layer.getNumber() == 51 || this.layer.getNumber() == 27)) {
                newLayer = layerManager.getLayer(this.layer.getNumber() + 1);
            }
            if (this.readable) {
                newLayer.addDrawable(new NoSpinTextDrawable5(Helpers.moveRotatePoint(this.relPosition, center, rot), newLayer, this.text, this.font, this.size, this.ratio, this.width, Rotation.add(rot, this.rotation), this.align));
            } else {
                newLayer.addDrawable(new SpinTextDrawable5(Helpers.moveRotatePoint(this.relPosition, center, rot), newLayer, this.text, this.font, this.size, this.ratio, this.width, Rotation.add(rot, this.rotation), this.align));
            }
        }
        */
        return null;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot, String newText) {
        if (this.layer != null) {
            Layer newLayer = this.layer;
            if (rot.isMirrored() && (this.layer.getNumber() == 25 || this.layer.getNumber() == 51 || this.layer.getNumber() == 27)) {
                newLayer = layerManager.getLayer(this.layer.getNumber() + 1);
            }
            if (this.readable) {
                newLayer.addDrawable(new NoSpinTextDrawable5(Helpers.moveRotatePoint(this.relPosition, center, rot), newLayer, newText, this.font, this.size, this.ratio, this.width, Rotation.add(rot, this.rotation), this.align));
            } else {
                newLayer.addDrawable(new SpinTextDrawable5(Helpers.moveRotatePoint(this.relPosition, center, rot), newLayer, newText, this.font, this.size, this.ratio, this.width, Rotation.add(rot, this.rotation), this.align));
            }
        }
        return null;
    }
}
