package bts.pcbassistant.data;

import android.graphics.Color;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.view.ExtendedCanvas;

public class LayerManager {
    private static int[] colorBoard;
    private static int[] colorSchematic;
    private Map<Integer, Layer> layers;

    private Part part;
    private String partGate;

    public enum DrawingMode {
        DEFAULT,
        BLIND_SIDE_HIDDEN,
        BLIND_SIDE_TRANSPARENT
    }

    /*
    public List<Part> getParts() {
        return null;//parts;
    }
    */

    public EagleDataSource getDataSource() {
        return dataSource;
    }

    //public List<Part> parts;
    private EagleDataSource dataSource;

    public LayerManager(EagleDataSource dataSource) {
        layers = new HashMap();
        //parts = new ArrayList<>();
        this.dataSource = dataSource;
    }

    public EagleDataSource.TYPE getDataSourceType() {
        return this.dataSource.getType();
    }

    public void setSelectedPart(Part selectedPart) {
        this.selectedPart = selectedPart;
    }

    public Part selectedPart = null;

    static {
        colorBoard = new int[]{Color.rgb(0, 0, 0), Color.rgb(35, 35, 141), Color.rgb(35, 141, 35), Color.rgb(35, 141, 141), Color.rgb(141, 35, 35), Color.rgb(141, 35, 141), Color.rgb(141, 141, 35), Color.rgb(141, 141, 141), Color.rgb(39, 39, 39), Color.rgb(0, 0, 180), Color.rgb(0, 180, 0), Color.rgb(0, 180, 180), Color.rgb(180, 0, 0), Color.rgb(180, 0, 180), Color.rgb(180, 180, 0), Color.rgb(180, 180, 180)};
        colorSchematic = new int[]{Color.rgb(255, 255, 255), Color.rgb(75, 75, 165), Color.rgb(75, 165, 75), Color.rgb(75, 165, 165), Color.rgb(165, 75, 75), Color.rgb(165, 75, 165), Color.rgb(165, 165, 75), Color.rgb(165, 165, 165), Color.rgb(230, 230, 230), Color.rgb(75, 75, 255), Color.rgb(75, 255, 75), Color.rgb(75, 255, 255), Color.rgb(255, 75, 75), Color.rgb(255, 75, 255), Color.rgb(255, 255, 75), Color.rgb(75, 75, 75)};
    }

    public void generateLayers(List<Layer> layerTemplates) {
        layers.clear();
        switch (dataSource.getType()) {
            case Board:
                for (Layer layer : layerTemplates) {
                    if (isGeneratedBoard(layer) && layer.getColor() < colorBoard.length) {
                        layer.setColor(colorBoard[layer.getColor()]);
                        layers.put(Integer.valueOf(layer.getNumber()), layer);
                    }
                }
                break;
            case Schematic:
                for (Layer layer2 : layerTemplates) {
                    if (isGeneratedSchematic(layer2) && layer2.getColor() < colorSchematic.length) {
                        layer2.setColor(colorSchematic[layer2.getColor()]);
                        layers.put(Integer.valueOf(layer2.getNumber()), layer2);
                    }
                }
                break;
        }
    }

    private boolean isGeneratedSchematic(Layer layer) {
        return layer.getNumber() >= 91;
    }

    private boolean isGeneratedBoard(Layer layer) {
        return true;
    }

    public Layer getLayer(int number) {
        if (layers.size() == 0) {
            return null;
        }
        return layers.get(Integer.valueOf(number));
    }

    public Map<Integer, Layer> getLayers() {
        if (layers.size() == 0) {
            return null;
        }
        return layers;
    }

    public boolean isShown(int number) {
        return getLayer(number).isShown();
    }

    final int[] boardLayersNormal = {
        Layer.BLAYER_BDOCU, //bDocu
        Layer.BLAYER_BNAMES, //bNames
        Layer.BLAYER_BVALUES, //bValues
        Layer.BLAYER_BPLACE, //bPlace
        Layer.BLAYER_BOTTOM, //Bottom
        -2,
        Layer.BLAYER_TOP,  //Top
        Layer.BLAYER_TPLACE, //tPlace
        -1,
        Layer.BLAYER_TVALUES, //tValues
        Layer.BLAYER_TNAMES, //tNames
        Layer.BLAYER_TDOCU, //tDocu
        Layer.BLAYER_PADS, //Pads
        Layer.BLAYER_VIAS, //Vias
        Layer.BLAYER_UNROUTED, //Unrouted
        Layer.BLAYER_DIMENSION //Dimension
    };

    final int[] boardLayersFlipped = {
            Layer.BLAYER_TDOCU, //tDocu
            Layer.BLAYER_TNAMES, //tNames
            Layer.BLAYER_TVALUES, //tValues
            Layer.BLAYER_TPLACE, //tPlace
            Layer.BLAYER_TOP,  //Top
            -2,
            Layer.BLAYER_BOTTOM, //Bottom
            Layer.BLAYER_BPLACE, //bPlace
            -1,
            Layer.BLAYER_BVALUES, //bValues
            Layer.BLAYER_BNAMES, //bNames
            Layer.BLAYER_BDOCU, //bDocu
            Layer.BLAYER_PADS, //Pads
            Layer.BLAYER_VIAS, //Vias
            Layer.BLAYER_UNROUTED, //Unrouted
            Layer.BLAYER_DIMENSION //Dimension
    };

    public void draw(ExtendedCanvas c, DrawingMode drawingMode) {
        if (layers != null && !layers.isEmpty()) {

            switch (dataSource.getType()) {
                case Board:
                   // c.drawColor(colorBoard[0]);
                    //rysujemy tył czy przód?
                    boolean hiddenSide = true;
                    if (!dataSource.isFlipped()) {
                        for (int i : boardLayersNormal) {
                            if (i == -1) {
                                for (Layer layer : layers.values()) {
                                    int n = layer.getNumber();
                                    if (!(n == 52 || n == 22 || n == 16 || n == 26 || n == 27 || n == 25 || n == 28 || n == 1 || n == 21 || n == 51 || n == 17 || n == 18 || n == 19 || n == 20)) {
                                        layer.Draw(c, drawingMode, hiddenSide);
                                    }
                                }
                            } else if (i == -2) {
                                hiddenSide = false;
                            } else {
                                getLayer(i).Draw(c, drawingMode, hiddenSide);
                            }
                        }
                        //odwrotnie
                    } else {
                        for (int i : boardLayersFlipped) {
                            if (i == -1) {
                                for (Layer layer : layers.values()) {
                                    int n = layer.getNumber();
                                    if (!(n == 52 || n == 22 || n == 16 || n == 26 || n == 27 || n == 25 || n == 28 || n == 1 || n == 21 || n == 51 || n == 17 || n == 18 || n == 19 || n == 20)) {
                                        layer.Draw(c, drawingMode, hiddenSide);
                                    }
                                }
                            } else if (i == -2) {
                                hiddenSide = false;
                            } else {
                                getLayer(i).Draw(c, drawingMode, hiddenSide);
                            }
                        }
                        //normalnie
                        /*getLayer(52).Draw(c); //bDocu
                        getLayer(26).Draw(c); //bNames
                        getLayer(28).Draw(c); //bValues
                        getLayer(22).Draw(c); //bPlace
                        getLayer(16).Draw(c); //Bottom
                        getLayer(1).Draw(c);  //Top
                        getLayer(21).Draw(c); //tPlace
                        for (Layer layer : layers.values().rev) {
                            int n = layer.getNumber();
                            if (!(n == 52 || n == 22 || n == 16 || n == 26 || n == 28 || n == 1 || n == 21 || n == 51 || n == 17 || n == 18 || n == 19 || n == 20)) {
                                layer.Draw(c);
                            }
                        }
                        getLayer(51).Draw(c); //tDocu
                        getLayer(17).Draw(c); //Pads
                        getLayer(18).Draw(c); //Vias
                        getLayer(19).Draw(c); //Unrouted
                        getLayer(20).Draw(c); //Dimension*/
                    }
                    break;
                case Schematic:
                  //  c.drawColor(colorSchematic[0]);
                    for (Layer layer2 : layers.values()) {
                        layer2.Draw(c);
                    }
                    break;
            }

        }
    }

    public RectF getImageSize(float zoom) {
        if (getLayer(20) == null) {
            return new RectF(0.0f, 0.0f, 1.0f, 1.0f);
        }
        return getLayer(20).getDimension();
    }

    /*
    funkcja rozpoczynająca transakcję polegająca na przypisywaniu
    obiektu Element wszystkim Drawables
     */
    public void beginPart(Part part_, String gate) {
        part = part_;
        partGate = gate;
    }
    public void beginPart(Part part_) {
        beginPart(part_, "");
    }
    public void commitPart() {
        part = null;
    }

    public Part getCurrentPart() {
        return part;
    }
    public String getCurrentGate() {
        return partGate;
    }
}
