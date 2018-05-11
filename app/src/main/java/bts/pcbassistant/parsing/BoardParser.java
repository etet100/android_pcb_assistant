package bts.pcbassistant.parsing;

import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Keep;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bts.pcbassistant.BuildConfiguration;
import bts.pcbassistant.data.BoardDataSource;
import bts.pcbassistant.data.BoardLibrary;
import bts.pcbassistant.data.ContactRef;
import bts.pcbassistant.data.EagleDataSource;
import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.data.Parser;
import bts.pcbassistant.data.Part;
import bts.pcbassistant.data.PartsManager;
import bts.pcbassistant.data.Signal;
import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.EagleAlign;
import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.PadDrawable;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.Vertex;
import bts.pcbassistant.drawing.WireDrawable;
import bts.pcbassistant.drawing.templates.CircleTemplate;
import bts.pcbassistant.drawing.templates.FilledRectangleTemplate;
import bts.pcbassistant.drawing.templates.PackageTemplate;
import bts.pcbassistant.drawing.templates.PadTemplate;
import bts.pcbassistant.drawing.templates.PolygonTemplate;
import bts.pcbassistant.drawing.templates.SizeF;
import bts.pcbassistant.drawing.templates.SmdTemplate;
import bts.pcbassistant.drawing.templates.Template;
import bts.pcbassistant.drawing.templates.TextTemplate;
import bts.pcbassistant.drawing.templates.ViaTemplate;
import bts.pcbassistant.drawing.templates.WireTemplate;
import bts.pcbassistant.drawing.ClipperDrawable;
import bts.pcbassistant.utils.Helpers;
import de.lighti.clipper.Clipper;
import de.lighti.clipper.DefaultClipper;
import de.lighti.clipper.Paths;

/* wersja nie statyczna */

public class BoardParser extends Parser {
    //private File file;
    private List<Layer> layersList;
    private LayerManager layerManager;
    private PartsManager partsManager;
    private Map<String, BoardLibrary> libsMap;

    Paths[] paths;

    public final EagleDataSource.TYPE sourceType = EagleDataSource.TYPE.Board;

    public native Template Native(String input);

    public BoardParser(LayerManager layerManager, PartsManager partsManager) {
        //this.file = file;
        this.layerManager = layerManager;
        this.partsManager = partsManager;

        if (BuildConfiguration.CALCULATE_OFFSETS) {
            this.paths = new Paths[]{
                    new Paths(),
                    new Paths()
            };
        }
    }

    /*
    public boolean parse() {
        String s = this.file.getName().toLowerCase();
        boolean b = this.file.getName().toLowerCase().endsWith(".brd");
        if (!this.file.getName().toLowerCase().endsWith(".brd")) {
            return false;
        }
        try {
            FileInputStream istr = new FileInputStream(this.file);
            this.parser = Xml.newPullParser();
            this.parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            this.parser.setInput(istr, "utf-8");
            this.parser.nextTag();
            this.layers = parseLayers();
            layerManager.generateLayers(this.layers);
            parsePlain();
            this.libs = parseLibs();
            parseElements();
            parseSignals();
            return true;
        } catch (IOException e) {
            error();
            e.printStackTrace();
            return false;
        } catch (XmlPullParserException e2) {
            error();
            e2.printStackTrace();
            return false;
        }
    }
    */

    //Context c, final AssetInputStreamWithCallbacksHandler callback
    public boolean parseStream(InputStream istr) {
        try {
            super.parseStream(istr);

            this.layersList = parseLayers();
            layerManager.generateLayers(this.layersList);
            parsePlain();
            this.libsMap = parseLibs();
            parseElements();
            parseSignals();

            if (BuildConfiguration.CALCULATE_OFFSETS) {
                Paths outPaths = new Paths();

                Clipper clipper = new DefaultClipper();
                clipper.addPaths(this.paths[0], Clipper.PolyType.SUBJECT, true);
                clipper.execute(Clipper.ClipType.UNION, outPaths, Clipper.PolyFillType.NON_ZERO, Clipper.PolyFillType.POSITIVE);
                layerManager.getLayer(20).addDrawable(new ClipperDrawable(outPaths));

                clipper.clear();

                clipper.addPaths(this.paths[1], Clipper.PolyType.SUBJECT, true);
                clipper.execute(Clipper.ClipType.UNION, outPaths, Clipper.PolyFillType.NON_ZERO, Clipper.PolyFillType.POSITIVE);
                layerManager.getLayer(20).addDrawable(new ClipperDrawable(outPaths));
            }

            return true;
        } catch (IOException e) {
            Log.e("BoardParser", e.getMessage());
            e.printStackTrace();
            logError();
            return false;
        } catch (XmlPullParserException e2) {
            Log.e("BoardParser", e2.getMessage());
            e2.printStackTrace();
            logError();
            return false;
        }
    }

    private List<Layer> parseLayers() throws XmlPullParserException, IOException {
        ArrayList<Layer> layerList = new ArrayList();
        skipTo("layers");
        do {
            if (this.parser.getEventType() == XmlPullParser.END_TAG && this.parser.getName().equals("layers")) {
                break;
            } else if (this.parser.getEventType() == XmlPullParser.START_TAG && this.parser.getName().equals("layer")) {
                int number = getIntegerValue("number");
                String layerName = getStringValue("name");
                int color = getIntegerValue("color");
                boolean layerVisible = getYesNoValueDef("visible", true);
                Layer res = new Layer(layerManager, number, color, layerName);
                res.setVisible(layerVisible);
                layerList.add(res);
            }
        } while (this.parser.next() != 1);
        return layerList;
    }

    private void parseElements() throws XmlPullParserException, IOException {
        skipTo("elements");
        boolean waitForName = false;
        boolean waitForValue = false;
        String smashedName = null;
        String smashedValue = null;
        Part part = null;
        do {
            if (this.parser.getEventType() != XmlPullParser.END_TAG || !this.parser.getName().equals("elements")) {
                if (this.parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = this.parser.getName();
                    float x, y;
                    String rot;
                    if (name.equals("element")) {
                        BoardLibrary eLib;
                        PackageTemplate ePkg;
                        String library = getStringValue("library").toLowerCase();
                        String pkg = getStringValue("package");
                        String pName = getStringValueDef("name", "");
                        String pValue = getStringValueDef("value", "");
                        rot = getStringValueDef("rot", "R0");
                        x = getFloatValue("x");
                        y = getFloatValue("y");
                        boolean isSmashed = getYesNoValueDef("smashed", false); //String smashed = getLCStringValueDef("smashed", "no");

                        part = partsManager.get(
                                EagleDataSource.TYPE.Board,
                                library,
                                pName,
                                pValue
                        );
                        layerManager.beginPart(part);

//                        Log.d("test", String.format("%s %s %s %s", library, pkg, pName, pValue));

                        eLib = (BoardLibrary) this.libsMap.get(library);
                        if (eLib != null) {
                            ePkg = (PackageTemplate) eLib.get(pkg);
                            if (ePkg != null) {
                                if (!isSmashed) {
                                    //dodaj od razu
                                    ePkg.AddCopyToLayer(layerManager, new PointF(x, y), Rotation.getRotation(rot), pName, pValue);
                                } else {
                                    //czekaj na name i value
                                    waitForName = true;
                                    waitForValue = true;
                                    smashedName = pName;
                                    smashedValue = pValue;
                                    ePkg.AddCopyToLayer(layerManager,new PointF(x, y), Rotation.getRotation(rot), "", "");
                                }
                            }
                        }

                        /*
                        RectF bounds = part.getBounds(EagleDataSource.TYPE.Board);
                        if (bounds != null) {
                            Log.d("dim2", String.format("%.02f %.02f %.02f %.02f", bounds.left, bounds.top, bounds.right, bounds.bottom));
                            layerManager.getLayer(1).addDrawable(new CircleDrawable(
                                    new PointF(bounds.left, bounds.top),
                                    layerManager.getLayer(1),
                                    0.2f,
                                    0.2f
                            ));
                            layerManager.getLayer(1).addDrawable(new CircleDrawable(
                                    new PointF(bounds.left, bounds.bottom),
                                    layerManager.getLayer(1),
                                    0.2f,
                                    0.2f
                            ));
                            layerManager.getLayer(1).addDrawable(new CircleDrawable(
                                    new PointF(bounds.right, bounds.top),
                                    layerManager.getLayer(1),
                                    0.2f,
                                    0.2f
                            ));
                            layerManager.getLayer(1).addDrawable(new CircleDrawable(
                                    new PointF(bounds.right, bounds.bottom),
                                    layerManager.getLayer(1),
                                    0.2f,
                                    0.2f
                            ));
                        }
                        */
                        /* //narysuj obrys elementu

                        RectF bounds = part.getBounds(EagleDataSource.TYPE.Board);
                        if (bounds != null) {
                            layerManager.getLayer(20).addDrawable(new RectangleDrawable(
                                    new PointF(bounds.centerX(), bounds.centerY()),
                                    new SizeF(bounds.width(), bounds.height()),
                                    layerManager.getLayer(20),
                                    new Rotation(0.0f, false)
                            ));
                        }
                         */
                        layerManager.commitPart();

                    } else {
                        if (name.equals("attribute") && (waitForName || waitForValue)) {
                            String attrName = getStringValue("name");
                            if (attrName.equalsIgnoreCase("name") || attrName.equalsIgnoreCase("value")) {
                                String str;
                                boolean isName = attrName.equalsIgnoreCase("name");
                                x = getFloatValue("x");
                                y = getFloatValue("y");
                                float size = getFloatValue("size");
                                int layer = getIntegerValue("layer");
                                rot = getStringValueDef("rot", "R0");
                                Rotation rotation = Rotation.getRotation(rot);
                                String font = getStringValueDef("font", "default");
                                int ratio = getIntegerValueDef("ratio", 8);
                                String align = getStringValueDef("align", "bottom-left");
                                AlignType alignType = EagleAlign.getAlign(align);
                                PointF pointF = new PointF(x, y);
                                Layer layer2 = layerManager.getLayer(layer);
                                str = isName?smashedName:smashedValue;
                                new TextTemplate(
                                        pointF,
                                        layer2,
                                        str,
                                        font,
                                        size,
                                        ratio,
                                        1.0f,
                                        rotation,
                                        alignType,
                                        true
                                ).AddCopyToLayer(
                                        layerManager,
                                        new PointF(0.0f, 0.0f),
                                        Rotation.ZERO
                                );
                                if (isName) {
                                    waitForName = false;
                                } else {
                                    waitForValue = false;
                                }
                            }
                        }
                    }
                }
            } else {
                layerManager.commitPart();
                return;
            }
        } while (this.parser.next() != 1);
        layerManager.commitPart();
    }

    private Map<String, BoardLibrary> parseLibs() throws XmlPullParserException, IOException {
        skipTo("libraries");
        Map<String, BoardLibrary> retVal = new HashMap();
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("libraries")) {
                break;
            } else if (this.parser.getEventType() == 2 && this.parser.getName().equals("library")) {
                BoardLibrary lib = parseLib();
                if (lib != null) {
                    retVal.put(lib.getName(), lib);
                }
            }
        } while (this.parser.next() != 1);
        return retVal;
    }

    private BoardLibrary parseLib() throws XmlPullParserException, IOException {
        skipTo("library");
        BoardLibrary retVal = null;
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("library")) {
                break;
            } else if (this.parser.getEventType() == 2) {
                String name = this.parser.getName();
                if (name.equals("library")) {
                    retVal = new BoardLibrary(this.parser.getAttributeValue(nameSpace, "name").toLowerCase());
                } else if (retVal != null && name.equals("package")) {
                    PackageTemplate pkg = parsePackage();
                    if (pkg != null) {
                        retVal.put(pkg.getName(), pkg);
                    }
                }
            }
        } while (this.parser.next() != 1);
        return retVal;
    }

    private PackageTemplate parsePackage() throws XmlPullParserException, IOException {
        skipTo("package");
        PackageTemplate retVal = null;
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("package")) {
                break;
            } else if (this.parser.getEventType() == 2) {
                String name = this.parser.getName();
                if (name.equals("package")) {
                    retVal = new PackageTemplate(this.parser.getAttributeValue(nameSpace, "name"));
                } else if (retVal != null) {
                    //NATIVE
                    Template t = this.Native(name);
                    if (t != null)
                        retVal.add(t);
                    /*
                    if (name.equals("wire")) {
                        retVal.add(readWire());
                    } else if (name.equals("smd")) {
                        retVal.add(readSmd());
                    } else if (name.equals("pad")) {
                        retVal.add(readPad());
                    } else if (name.equals("circle")) {
                        retVal.add(readCircle());
                    } else if (name.equals("rectangle")) {
                        retVal.add(readRectangle());
                    } else if (name.equals("polygon")) {
                        retVal.add(parsePolygon());
                    } else if (name.equals("text")) {
                        retVal.add(readText());
                    }*/
                }
            }
        } while (this.parser.next() != 1);
        return retVal;
    }

    private void parsePlain() throws XmlPullParserException, IOException {
        skipTo("plain");
        requireStart("plain");
        while (this.parser.next() != 1) {
            if (this.parser.getEventType() != 3 || !this.parser.getName().equals("plain")) {
                if (this.parser.getEventType() == 2) {
                    String name = this.parser.getName();
                    Template result = null;
                    //NATIVE
                    result = this.Native(name);
/*
                    if (name.equals("wire")) {
                        result = readWire();
                    } else if (name.equals("smd")) {
                        result = readSmd();
                    } else if (name.equals("pad")) {
                        result = readPad();
                    } else if (name.equals("circle")) {
                        result = readCircle();
                    } else if (name.equals("rectangle")) {
                        result = readRectangle();
                    } else if (name.equals("text")) {
                        result = readText();
                    }*/
                    if (result != null) {
                        result.AddCopyToLayer(layerManager, new PointF(0.0f, 0.0f), Rotation.ZERO);
                    }
                }
            } else {
                return;
            }
        }
    }

    private void parseSignals() throws XmlPullParserException, IOException {
        skipTo("signals");
        requireStart("signals");
        Signal signal = null;
        while (this.parser.next() != XmlPullParser.END_DOCUMENT) {
            if (this.parser.getEventType() != XmlPullParser.END_TAG || !this.parser.getName().equals("signals")) {
                if (this.parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = this.parser.getName();
                    Template result = null;

                    if (name.equals("signal")) {
                        signal = ((BoardDataSource) layerManager.getDataSource()).addSignal(this.getStringValue("name"));
                    } else if (name.equals("contactref")) {
                        ContactRef cref = readContactRef();
                        if (cref != null)
                            signal.addContactRef(cref);
                    } else
                        //NATIVE
                        result = this.Native(name);
/*
                    if (name.equals("signal")) {
                        signal = ((BoardDataSource)layerManager.getDataSource()).addSignal(this.getStringValue("name"));
                    } else if (name.equals("wire")) {
                        result = readWire();
                    } else if (name.equals("via")) {
                        result = readVia();
                    } else if (name.equals("polygon")) {
                        result = parsePolygon();
                    } else if (name.equals("contactref")) {
                        readContactRef(signal);
                    }
*/
                    if (result != null) {
                        BaseDrawable drawable = result.AddCopyToLayer(layerManager, new PointF(0.0f, 0.0f), Rotation.ZERO);
                        if (BuildConfiguration.SHOW_CONNECTED_SIGNALS && drawable instanceof WireDrawable) {
                            signal.addDrawable(drawable);
                        }
                        if (BuildConfiguration.CALCULATE_OFFSETS) {
                            if (drawable instanceof WireDrawable) {
                                Paths paths = ((WireDrawable) drawable).offset(3);
                                switch (result.getLayer().getNumber()) {
                                    case 1:
                                        this.paths[0].addAll(paths);
                                        break;
                                    case 16:
                                        this.paths[1].addAll(paths);
                                        break;
                                    case 20:
                                        this.paths[0].addAll(paths);
                                        this.paths[1].addAll(paths);
                                        break;
                                }
                            }
                            if (drawable instanceof PadDrawable) {
                                Paths paths = ((PadDrawable) drawable).offset(3);
                                switch (result.getLayer().getNumber()) {
                                    case 1:
                                    case 16:
                                        this.paths[0].addAll(paths);
                                        this.paths[1].addAll(paths);
                                        break;
                                }
                            }
                        }
                    }
                } else if (this.parser.getEventType() == XmlPullParser.END_TAG && this.parser.getName().equals("signal")) {
                    signal = null;
                }
            } else {
                return;
            }
        }
    }

    @Keep
    private PolygonTemplate parsePolygon() throws XmlPullParserException, IOException {
        requireStart("polygon");
        int layer = getIntegerValue("layer");
        Vertex first = null;
        PolygonTemplate retVal = new PolygonTemplate(layerManager.getLayer(layer), getFloatValue("width"));
        while (this.parser.next() != 1 && (this.parser.getEventType() != 3 || !this.parser.getName().equals("polygon"))) {
            if (this.parser.getEventType() == 2 && this.parser.getName().equals("vertex")) {
                float x = getFloatValue("x");
                float y = getFloatValue("y");
                float curveVal = getFloatValueDef("curve", 0.0f);
                Vertex newVertex = new Vertex(x, y, -curveVal);
                if (first == null) {
                    first = newVertex;
                }
                retVal.Add(newVertex);
            }
        }
        if (first != null) {
            retVal.Add(first);
        }
        return retVal;
    }

    @Keep
    private ContactRef readContactRef() throws XmlPullParserException, IOException {
        requireStart("contactref");
        try {
            String element = getStringValueDef("element", "");
            if (!element.isEmpty())
                return new ContactRef(element);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
        }
        return null;
    }

    @Keep
    private WireTemplate readWire() throws XmlPullParserException, IOException {
        requireStart("wire");
        try {
            float x1 = getFloatValue("x1");
            float x2 = getFloatValue("x2");
            float y1 = getFloatValue("y1");
            float y2 = getFloatValue("y2");
            float width = getFloatValue("width");
            if (width == 0.0f) {
                width = -1.0f;
            }
            int layer = getIntegerValue("layer");
            String curve = getStringValue("curve");
            String style = getStringValue("style");
            if (curve == null) {
                return new WireTemplate(
                        new PointF(x1, y1),
                        getLayer(layer),
                        new PointF(x2, y2),
                        (double) width,
                        0.0d,
                        style
                );
            }
            return new WireTemplate(
                    new PointF(x1, y1),
                    getLayer(layer),
                    new PointF(x2, y2),
                    (double) width,
                    (double) (-Float.parseFloat(curve)),
                    style
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    @Keep
    private SmdTemplate readSmd() throws XmlPullParserException, IOException {
        requireStart("smd");
        try {
            float x = getFloatValue("x");
            float y = getFloatValue("y");
            float dx = getFloatValue("dx");
            float dy = getFloatValue("dy");
            float roundness = getFloatValueDef("roundness", 0.0f);
            int layer = getIntegerValue("layer");
            String rot = getStringValueDef("rot", "R0");
            return new SmdTemplate(
                    new PointF(x, y),
                    new SizeF(dx, dy),
                    getLayer(layer),
                    Rotation.getRotation(rot),
                    roundness
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    @Keep
    private TextTemplate readText() throws XmlPullParserException, IOException {
        requireStart("text");
        try {
            float x = getFloatValue("x");
            float y = getFloatValue("y");
            float size = getFloatValue("size");
            int layer = getIntegerValue("layer");
            Rotation rotation = Rotation.getRotation(getStringValueDef("rot", "R0"));
            String align = getStringValueDef("align", "bottom-left");
            String font = getStringValueDef("font", "default");
            int ratio = getIntegerValueDef("ratio", 8);
            AlignType alignType = EagleAlign.getAlign(align);
            String text = "";
            try {
                text = this.parser.nextText();
            } catch (XmlPullParserException e) {
            }
            return new TextTemplate(
                    new PointF(x, y),
                    layerManager.getLayer(layer),
                    text,
                    font,
                    size,
                    ratio,
                    0.5f,
                    rotation,
                    alignType
            );
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            logError();
            return null;
        }
    }

    @Keep
    private PadTemplate readPad() throws XmlPullParserException, IOException {
        requireStart("pad");
        try {
            float x = getFloatValue("x");
            float y = getFloatValue("y");
            double drill = getDoubleValue("drill");
            String shape = getStringValue("shape");
            String rot = getStringValueDef("rot", "R0");
            float sizef =getFloatValueDef("diameter", -1.0f);
            return new PadTemplate(
                    layerManager,
                    new PointF(x, y),
                    getLayer(17),
                    drill,
                    (double) sizef,
                    PadTemplate.getType(shape),
                    Rotation.getRotation(rot)
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    @Keep
    private ViaTemplate readVia() throws XmlPullParserException, IOException {
        requireStart("via");
        try {
            float x = getFloatValue("x");
            float y = getFloatValue("y");
            double drill = getDoubleValue("drill");
            float sizef = getFloatValueDef("diameter", -1.0f);
            String shape = getStringValue("shape");
            String rot = getStringValueDef("rot", "R0");
            return new ViaTemplate(
                    layerManager,
                    new PointF(x, y),
                    getLayer(18),
                    drill,
                    (double) sizef,
                    PadTemplate.getType(shape),
                    Rotation.getRotation(rot)
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    @Keep
    private CircleTemplate readCircle() throws XmlPullParserException, IOException {
        requireStart("circle");
        try {
            double radius = getDoubleValue("radius");
            double width = getDoubleValue("width");
            return new CircleTemplate(
                    new PointF(getFloatValue("x"), getFloatValue("y")),
                    getLayer(getIntegerValue("layer")),
                    (float) radius,
                    (float) width
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    @Keep
    private FilledRectangleTemplate readRectangle() throws XmlPullParserException, IOException {
        requireStart("rectangle");
        try {
            float x1 = getFloatValue("x1");
            float y1 = getFloatValue("y1");
            float x2 = getFloatValue("x2");
            float y2 = getFloatValue("y2");
            int layer = getIntegerValue("layer");
            String rot = getStringValueDef("rot", "R0");
            return new FilledRectangleTemplate(
                    Helpers.midPointF(new PointF(x1, y1), new PointF(x2, y2)),
                    new SizeF(new RectF(x1, y1, x2, y2)),
                    layerManager.getLayer(layer),
                    Rotation.getRotation(rot)
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    private static void logError() {
        Log.d("BoardParser", "Board parsing error");
    }

    private Layer getLayer(int layer) {
        return layerManager.getLayer(layer);
    }

    public Map<String, BoardLibrary> getLibraries() {
        return this.libsMap;
    }

    public List<Layer> getLayersList() {
        return this.layersList;
    }
}
