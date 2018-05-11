package bts.pcbassistant.drawing.templates;

import android.graphics.PointF;

import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.CircleDrawable;
import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.SchematicTextDrawable5;
import bts.pcbassistant.drawing.WireDrawable;
import bts.pcbassistant.utils.Helpers;

public class PinTemplate extends Template {
    private static final float clkHeight = 1.0f;
    private static final float textSize = 1.542f;
    private static final float textSpace = 0.25f;
    private Function function;
    private float length;
    private String name;
    private final float radius;
    private Rotation rotation;
    private Visibility visible;
    private final float width;

    public enum Function {
        None,
        Dot,
        Clk,
        DotClk
    }

    public enum Visibility {
        Off,
        Pad,
        Pin,
        Both
    }

    public PinTemplate(PointF relPos, Layer layer, String name, Visibility visible, float length, Function function, Rotation rotation) {
        super(relPos, layer);
        this.width = 0.1524f;
        this.radius = clkHeight;
        this.name = name;
        this.visible = visible;
        this.length = length;
        this.rotation = rotation;
        this.function = function;
    }

    public static float getLength(String input) {
        if (input.equalsIgnoreCase("long")) {
            return 7.62f;
        }
        if (input.equalsIgnoreCase("middle")) {
            return 5.08f;
        }
        if (input.equalsIgnoreCase("short")) {
            return 2.54f;
        }
        return 0.0f;
    }

    public static Visibility getVisibility(String input) {
        if (input.equalsIgnoreCase("off")) {
            return Visibility.Off;
        }
        if (input.equalsIgnoreCase("pad")) {
            return Visibility.Pad;
        }
        if (input.equalsIgnoreCase("both")) {
            return Visibility.Both;
        }
        return Visibility.Pin;
    }

    public static Function getFunction(String input) {
        if (input.equalsIgnoreCase("none")) {
            return Function.None;
        }
        if (input.equalsIgnoreCase("dot")) {
            return Function.Dot;
        }
        if (input.equalsIgnoreCase("clk")) {
            return Function.Clk;
        }
        return Function.DotClk;
    }

    public BaseDrawable AddCopyToLayer(LayerManager layerManager, PointF center, Rotation rot) {
        if (this.layer != null) {
            PointF right;
            /*
            switch (C00581.f8x5c45ab6a[this.function.ordinal()]) {
                case 1: //ClassWriter.COMPUTE_MAXS /*1* /:
                case 2: //ClassWriter.COMPUTE_FRAMES /*2* /:
                    right = MetricsHelpers.rotatePoint(new PointF(this.relPosition.x + this.length, this.relPosition.y), this.relPosition, this.rotation);
                    break;
                default:
                    right = MetricsHelpers.rotatePoint(new PointF((this.relPosition.x + this.length) - 2.0f, this.relPosition.y), this.relPosition, this.rotation);
                    this.layer.addDrawable(new CircleDrawable(MetricsHelpers.moveRotatePoint(MetricsHelpers.rotatePoint(new PointF((this.relPosition.x + this.length) - clkHeight, this.relPosition.y), this.relPosition, this.rotation), center, rot), this.layer, clkHeight, 0.1524f));
                    break;
            }*/

            switch (this.function) {
                case None:
                case Clk:
                    right = Helpers.rotatePoint(new PointF(this.relPosition.x + this.length, this.relPosition.y), this.relPosition, this.rotation);
                    break;
                default:
                    right = Helpers.rotatePoint(new PointF((this.relPosition.x + this.length) - 2.0f, this.relPosition.y), this.relPosition, this.rotation);
                    this.layer.addDrawable(new CircleDrawable(Helpers.moveRotatePoint(Helpers.rotatePoint(new PointF((this.relPosition.x + this.length) - clkHeight, this.relPosition.y), this.relPosition, this.rotation), center, rot), this.layer, clkHeight, 0.1524f));
                    break;
            }

            this.layer.addDrawable(new WireDrawable(Helpers.moveRotatePoint(this.relPosition, center, rot), this.layer, Helpers.moveRotatePoint(right, center, rot), 0.15240000188350677d, 0.0d));
            if (this.function == Function.Clk || this.function == Function.DotClk) {
                PointF top = Helpers.rotatePoint(new PointF(this.relPosition.x + this.length, this.relPosition.y - clkHeight), this.relPosition, this.rotation);
                PointF bottom = Helpers.rotatePoint(new PointF(this.relPosition.x + this.length, this.relPosition.y + clkHeight), this.relPosition, this.rotation);
                PointF clkTip = Helpers.moveRotatePoint(Helpers.rotatePoint(new PointF((this.relPosition.x + this.length) + 2.0f, this.relPosition.y), this.relPosition, this.rotation), center, rot);
                this.layer.addDrawable(new WireDrawable(Helpers.moveRotatePoint(top, center, rot), this.layer, clkTip, 0.15240000188350677d, 0.0d));
                this.layer.addDrawable(new WireDrawable(Helpers.moveRotatePoint(bottom, center, rot), this.layer, clkTip, 0.15240000188350677d, 0.0d));
            }
            if (this.visible == Visibility.Pin || this.visible == Visibility.Both) {
                PointF pinName = Helpers.moveRotatePoint(Helpers.rotatePoint(new PointF(((this.relPosition.x + this.length) + 2.0f) + textSpace, this.relPosition.y), this.relPosition, this.rotation), center, rot);
                this.layer.addDrawable(new SchematicTextDrawable5(pinName, layerManager.getLayer(95), this.name, "default", textSize, 8, 0.5f, Rotation.add(rot, this.rotation), AlignType.CL));
            }
        }
        return null;
    }
}
