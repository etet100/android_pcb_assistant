package bts.pcbassistant.drawing;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.LinkedList;
import java.util.List;

import bts.pcbassistant.BuildConfig;
import bts.pcbassistant.data.EagleDataSource;
import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.data.Part;
import bts.pcbassistant.drawing.view.ExtendedCanvas;
import bts.pcbassistant.utils.RectUtils;
import de.lighti.clipper.Path;

public class Layer implements Comparable<Layer> {
    private static Layer background;
    private int color;
    private List<IDrawable> drawables;
    private boolean isShown;
    private String name;
    private int number;
    private Paint paint;
    private Paint selectedPaint;
    private LayerManager layerManager;
    private EagleDataSource.TYPE dataSourceType;

    final public static int BLAYER_TOP = 1;
    final public static int BLAYER_BOTTOM = 16;
    final public static int BLAYER_PADS = 17;
    final public static int BLAYER_VIAS = 18;
    final public static int BLAYER_BPLACE = 22;
    final public static int BLAYER_TPLACE = 21;
    final public static int BLAYER_BVALUES = 28;
    final public static int BLAYER_TVALUES = 27;
    final public static int BLAYER_BNAMES = 26;
    final public static int BLAYER_TNAMES = 25;
    final public static int BLAYER_DIMENSION = 20;
    final public static int BLAYER_UNROUTED = 19;
    final public static int BLAYER_BDOCU = 52;
    final public static int BLAYER_TDOCU = 51;

    static {
        background = null;
    }

    public Layer(LayerManager layerManager, int number, int color, String name) {
        this.drawables = new LinkedList();
        this.isShown = false;
        this.paint = null;
        this.name = name;
        this.color = color;
        this.number = number;
        this.layerManager = layerManager;
        this.dataSourceType = layerManager.getDataSourceType();
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //https://stackoverflow.com/questions/4928772/using-color-and-color-darker-in-android
    public static int makeColorLighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1.0f - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color)));// * (1.0f - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1.0f - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public Paint getPaint() {
        if (this.paint == null) {
            this.paint = new Paint();
            this.paint.setColor(this.color);
            this.paint.setStyle(Style.FILL);
            this.paint.setStrokeCap(Cap.ROUND);
            this.paint.setAntiAlias(true);
        }
        return this.paint;
    }

    public Paint getSelectedPaint() {
        if (this.selectedPaint == null) {
            this.selectedPaint = new Paint(this.getPaint());
            switch (this.number) {
                case Layer.BLAYER_TOP:
                    this.selectedPaint.setColor(Color.rgb(255, 148, 0));
                    break;
                case Layer.BLAYER_BOTTOM:
                    this.selectedPaint.setColor(Color.rgb(7, 234, 255));
                    break;
                default:
                    this.selectedPaint.setColor(Layer.makeColorLighter(this.color, 0.5f));
                    break;
            }
        }
        return this.selectedPaint;
    }

    public void Draw(ExtendedCanvas c) {
        if (this.isShown) {
            for (IDrawable drawable : this.drawables) {
                drawable.Draw(c);
            }
        }
    }

    public void Draw(ExtendedCanvas c, LayerManager.DrawingMode drawingMode, boolean hidden) {
        switch (drawingMode) {
            case BLIND_SIDE_HIDDEN:
                if (hidden) return;
                break;
            case BLIND_SIDE_TRANSPARENT:
                this.getPaint().setAlpha(hidden?100:255);
                this.getSelectedPaint().setAlpha(hidden?100:255);
                break;
            default:
                this.getPaint().setAlpha(255);
                this.getSelectedPaint().setAlpha(255);
        }
        if (this.isShown) {
            for (IDrawable drawable : this.drawables) {
                drawable.Draw(c);
            }
        }
    }

    /*
    public static Layer getBackground() {
        if (background == null) {
            background = new Layer(
                    0,
                    Color.rgb(MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK, MotionEventCompat.ACTION_MASK),
                    "Background",
                    this.layerManager
            );
        }
        return background;
    }
    */

    public void addDrawable(BaseDrawable drawable) {
        Part part = layerManager.getCurrentPart();
        String partGate = layerManager.getCurrentGate();
        if (part != null && drawable instanceof DimensionDrawable) {

            RectF rect = ((DimensionDrawable) drawable).getDimension();

            //Log.d("dim1",String.format("%.2f %.2f %.2f %.2f", rect.left, rect.top, rect.right, rect.bottom));

            rect = RectUtils.expand(rect, 2, 2);

            if (rect != null) {
                part.extendBounds(dataSourceType, partGate, new PointF(rect.left, rect.top));
                part.extendBounds(dataSourceType, partGate, new PointF(rect.right, rect.bottom));
            }
/*
            rect = RectUtils.expand(rect, 1, 1);
            CircleDrawable circle;

            circle = new CircleDrawable(
                    new PointF(rect.left, rect.top),
                    Layermanager.getLayer(1),
                    0.2f,
                    0.3f
            );
            this.drawables.add(circle);

            part.extendBounds(new PointF(rect.left, rect.top));

            circle = new CircleDrawable(
                    new PointF(rect.right, rect.bottom),
                    Layermanager.getLayer(2),
                    0.3f,
                    0.3f
            );
            this.drawables.add(circle);

            part.extendBounds(new PointF(rect.right, rect.bottom));

            PointF p = MetricsHelpers.midPointF(rect.left, rect.top, rect.right, rect.bottom);
            FilledRectangleDrawable drawable2 = new FilledRectangleDrawable(
                    p,
                    new SizeF(rect.width(), rect.height()),
                    Layermanager.getLayer(1),
                    new Rotation(0, false)
            );
            this.drawables.add(drawable2);
*/
            /*drawable.setPart(
                    Layermanager.getCurrentPart()
            );*/
        }
        this.drawables.add(drawable);
    }

    public boolean isShown() {
        return this.isShown;
    }

    public boolean isEmpty() {
        return (drawables.size() == 0);
    }

    public RectF getDimension() {
        RectF bounds = null;//new RectF(0, 0, 100, 100);
        /*
        boolean hasElements = false;
        float left = 0.0f;
        float right = Float.MIN_VALUE;
        float top = 0.0f;
        float bottom = Float.MIN_VALUE;
        */
        for (IDrawable dr : this.drawables) {
            if (dr instanceof DimensionDrawable) {
                RectF dimension = ((DimensionDrawable) dr).getDimension();
                if (dimension != null) {
                    if (BuildConfig.DEBUG) {
                        if (dimension.right < dimension.left || dimension.bottom < dimension.top) {
                            throw new AssertionError();
                        }
                    }
                    if (bounds == null) {
                        //tworząc w ten sposób dodajemy automatycznie punkt 0,0
                        //czy każda płytka musi zaczynać się w 0,0?
                        //TODO sprawdzić procedurę liczenia wymiarów warstwy
                        bounds = new RectF(dimension);
                        bounds.union(0.0f,0.0f);
                    } else {
                        bounds.union(dimension.left, dimension.top);
                        bounds.union(dimension.right, dimension.bottom);
                    }
                    /*
                    left = Math.min(dimension.left, left);
                    right = Math.max(dimension.right, right);
                    top = Math.min(dimension.top, top);
                    bottom = Math.max(dimension.bottom, bottom);
                    hasElements = true;
                    */
                }
            }
        }
        /*
        if (hasElements) {
            return new RectF(left, top, right, bottom);
        } else
            return new RectF(0.0f, 0.0f, 100.0f, 100.0f);
            */
        //nie znaleziono elementów? domyślnie 0,0,100,100 - ale czemu?
        return (bounds != null)?bounds:new RectF(0.0f, 0.0f, 100.0f, 100.0f);
    }

    public int getNumber() {
        return this.number;
    }

    public String toString() {
        return this.name + " (" + this.number + ")";
    }

    public void setVisible(boolean checked) {
        this.isShown = checked;
    }

    public int compareTo(Layer layer) {
        return new Integer(this.number).compareTo(new Integer(layer.number));
    }
}
