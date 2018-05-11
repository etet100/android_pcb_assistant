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

import bts.pcbassistant.data.DeviceSet;
import bts.pcbassistant.data.EagleDataSource;
import bts.pcbassistant.data.Gate;
import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.data.Parser;
import bts.pcbassistant.data.Part;
import bts.pcbassistant.data.PartsManager;
import bts.pcbassistant.data.SchematicLibrary;
import bts.pcbassistant.drawing.EagleAlign;
import bts.pcbassistant.drawing.EagleAlign.AlignType;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.Rotation;
import bts.pcbassistant.drawing.Vertex;
import bts.pcbassistant.drawing.templates.CircleTemplate;
import bts.pcbassistant.drawing.templates.FilledRectangleTemplate;
import bts.pcbassistant.drawing.templates.JunctionTemplate;
import bts.pcbassistant.drawing.templates.LabelTemplate;
import bts.pcbassistant.drawing.templates.PadTemplate;
import bts.pcbassistant.drawing.templates.PinTemplate;
import bts.pcbassistant.drawing.templates.PolygonTemplate;
import bts.pcbassistant.drawing.templates.SchematicTextTemplate;
import bts.pcbassistant.drawing.templates.SizeF;
import bts.pcbassistant.drawing.templates.SmdTemplate;
import bts.pcbassistant.drawing.templates.SymbolTemplate;
import bts.pcbassistant.drawing.templates.Template;
import bts.pcbassistant.drawing.templates.TextTemplate;
import bts.pcbassistant.drawing.templates.WireTemplate;
import bts.pcbassistant.utils.Helpers;

public class SchematicParser extends Parser {
    //private File file;
    private List<Layer> layersList;
    private LayerManager layerManager;
    private PartsManager partsManager;
    private Map<String, SchematicLibrary> libsMap;
    //private XmlPullParser parser;

    public native Template Native(String name);

    private class SchematicPart {
        public String getPartDeviceset() {
            return partDeviceset;
        }
        public String getPartLibrary() {
            return partLibrary;
        }
        public String getPartName() {
            return partName;
        }
        public String getPartValue() {
            return partValue;
        }

        private String partDeviceset;
        private String partLibrary;
        private String partName;
        private String partValue;

        public SchematicPart(String partLibrary, String partName, String partValue, String partDeviceset) {
            this.partDeviceset = partDeviceset;
            this.partLibrary = partLibrary;
            this.partName = partName;
            this.partValue = partValue;
        }

        public Part getFromPartsManager() {
            Part p = partsManager.get(
                    EagleDataSource.TYPE.Schematic,
                    partLibrary,
                    partName,
                    partValue
            );
            p.setDeviceset(partDeviceset);
            return p;
        }
    }

    public final EagleDataSource.TYPE sourceType = EagleDataSource.TYPE.Board;

    public SchematicParser(LayerManager layerManager, PartsManager partsManager) {
        //this.file = file;
        this.layerManager = layerManager;
        this.partsManager = partsManager;
    }

    /*
    public boolean parse() {
        if (!this.file.getName().toLowerCase().endsWith(".sch")) {
            return false;
        }
        try {
//            InputStream istr = InputStreamWithCallbacks.getStream(this, c, this.file);// c.getAssets().open(this.file.getName());

            FileInputStream istr = new FileInputStream(this.file);
            this.parser = Xml.newPullParser();
            this.parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            this.parser.setInput(istr, "utf-8");
            this.parser.nextTag();
            this.layers = parseLayers();
            layerManager.generateLayers(this.layers);
            this.libs = parseLibs();
            Map<String, SchematicPart> parts = parseParts();
            parsePlain();
            parseInstances(parts);
            parseBusses();
            parseSignals();
            return true;
        } catch (IOException e) {
            error();
            return false;
        } catch (XmlPullParserException e2) {
            error();
            return false;
        }
    }
    */

    public boolean parseStream(InputStream istr) {
        try {
            super.parseStream(istr);

            this.layersList = parseLayers();
            layerManager.generateLayers(this.layersList);
            this.libsMap = parseLibs();
            Map<String, SchematicPart> parts = parseParts();
            parsePlain();
            parseInstances(parts);
            parseBusses();
            parseSignals();
            return true;
        } catch (IOException e) {
            Log.e("SchematicParser", e.getMessage());
            e.printStackTrace();
            logError();
            return false;
        } catch (XmlPullParserException e2) {
            Log.e("SchematicParser", e2.getMessage());
            e2.printStackTrace();
            logError();
            return false;
        }
    }

    private List<Layer> parseLayers() throws XmlPullParserException, IOException {
        ArrayList<Layer> layerList = new ArrayList();
        skipTo("layers");
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("layers")) {
                break;
            } else if (this.parser.getEventType() == 2 && this.parser.getName().equals("layer")) {
                int number = getIntegerValue("number");
                String layerName = getStringValue("name");
                int color = getIntegerValue("color");
                //String visible = getStringValue("visible");
                boolean layerVisible = getYesNoValueDef("visible", true);
/*                if (visible != null) {
                    layerVisible = visible.equalsIgnoreCase("yes");
                }*/
                Layer res = new Layer(
                        layerManager,
                        number,
                        color,
                        layerName
                );
                res.setVisible(layerVisible);
                layerList.add(res);
            }
        } while (this.parser.next() != 1);
        return layerList;
    }

    private Map<String, SchematicPart> parseParts() throws XmlPullParserException, IOException {
        skipTo("parts");
        HashMap<String, SchematicPart> partMap = new HashMap();
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("parts")) {
                break;
            } else if (this.parser.getEventType() == 2 && this.parser.getName().equals("part")) {
                String pName = getStringValue("name");
                String library = getStringValue("library");
                String deviceset = getStringValue("deviceset");
                String device = getStringValue("device");
                String pVal = getStringValueDef("value", deviceset + device);

                partMap.put(pName, new SchematicPart(
                        library.toLowerCase(),
                        pName,
                        pVal,
                        deviceset
                ));
            }
        } while (this.parser.next() != 1);

        /*
        for (Map.Entry<String, SchematicPart> p : partMap.entrySet()) {
            Log.d("test", String.format("%s %s %s", p.getKey(), p.getValue().library, p.getValue().getFromPartsManager().getLibrary()));
        }
        */

        return partMap;
    }

    private void parseInstances(Map<String, SchematicPart> parts) throws XmlPullParserException, IOException {
        skipTo("instances");
        boolean waitForName = false;
        boolean waitForValue = false;
        String smashedName = null;
        String smashedValue = null;
        do {
            if (this.parser.getEventType() != XmlPullParser.END_TAG || !this.parser.getName().equals("instances")) {
                if (this.parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = this.parser.getName();
                    float x, y;
                    String rot;
                    Rotation rotation;
                    if (name.equals("instance")) {
                        String partName = getStringValue("part");
                        String gate = getStringValue("gate");
                        x = getFloatValue("x");
                        y = getFloatValue("y");
                        rot = getStringValueDef("rot", "R0");
                        rotation = Rotation.getRotation(rot);
                        boolean isSmashed = getYesNoValueDef("smashed", false);
                        if (parts.containsKey(partName)) {

                            //TODO poprawić
                            Part part = parts.get(partName).getFromPartsManager();
                            part.addGate(gate);

                            SchematicLibrary eLib = this.libsMap.get(part.getLibrary());
                            if (eLib != null) {
                                SymbolTemplate ePkg = eLib.getSymbol(part.getDeviceset(), gate);
                                if (ePkg != null) {
                                    if (!eLib.getName().equalsIgnoreCase("frames")) // nie chcemy zaznaczania ramek
                                        layerManager.beginPart(part, gate);
                                    String finalName;
                                    String finalValue;
                                    if (isSmashed) {
                                        waitForName = true;
                                        waitForValue = true;
                                        if (eLib.hasAddLevel(part.getDeviceset(), gate)) {
                                            smashedName = part.getName() + gate;
                                            smashedValue = part.getValue() + gate;
                                        } else {
                                            smashedName = part.getName();
                                            smashedValue = part.getValue();
                                        }
                                        ePkg.AddCopyToLayer(layerManager, new PointF(x, y), rotation, "", "");
                                    } else {
                                        //dodaj od razu
                                        if (eLib.hasAddLevel(part.getDeviceset(), gate)) {
                                            finalName = part.getName() + gate;
                                            finalValue = part.getValue() + gate;
                                        } else {
                                            finalName = part.getName();
                                            finalValue = part.getValue();
                                        }
                                        ePkg.AddCopyToLayer(layerManager, new PointF(x, y), rotation, finalName, finalValue);
                                    }
                                    //TODO poprawić
                                    layerManager.commitPart();
                                }
                            }


                            /* //narysuj obrys elementu
                            RectF bounds = part.getBounds(EagleDataSource.TYPE.Schematic);
                            if (bounds != null) {
                                layerManager.getLayer(94).addDrawable(new RectangleDrawable(
                                        new PointF(bounds.centerX(), bounds.centerY()),
                                        new SizeF(bounds.width(), bounds.height()),
                                        layerManager.getLayer(94),
                                        new Rotation(0.0f, false)
                                ));
                            }
                            */

                        }
                    } else {
                        //smashed name i value?
                        if (name.equals("attribute") && (waitForName || waitForValue)) {
                            String attrName = getStringValue("name");
                            if (attrName.equalsIgnoreCase("name") || attrName.equalsIgnoreCase("value")) {
                                boolean isName = attrName.equalsIgnoreCase("name");
                                float size = getFloatValue("size");
                                String font = getStringValueDef("font", "default");
                                int layer = getIntegerValue("layer");
                                rotation = Rotation.getRotation(getStringValueDef("rot", "R0"));
                                String align = getStringValueDef("align", "bottom-left");
                                AlignType alignType = EagleAlign.getAlign(align);
                                int ratio = getIntegerValueDef("ratio", 8);
                                PointF pointF = new PointF(getFloatValue("x"), getFloatValue("y"));
                                Layer layer2 = layerManager.getLayer(layer);
                                String str = isName?smashedName:smashedValue;
                                new SchematicTextTemplate(pointF, layer2, str, font, size, ratio, 1.0f, rotation, alignType).
                                        AddCopyToLayer(layerManager, new PointF(0.0f, 0.0f), Rotation.ZERO);
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
                return;
            }
        } while (this.parser.next() != 1);
    }

    private Map<String, SchematicLibrary> parseLibs() throws XmlPullParserException, IOException {
        skipTo("libraries");
        Map<String, SchematicLibrary> retVal = new HashMap();
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("libraries")) {
                break;
            } else if (this.parser.getEventType() == 2 && this.parser.getName().equals("library")) {
                SchematicLibrary lib = parseLib();
                if (lib != null) {
                    retVal.put(lib.getName(), lib);
                }
            }
        } while (this.parser.next() != 1);
        return retVal;
    }

    private SchematicLibrary parseLib() throws XmlPullParserException, IOException {
        skipTo("library");
        SchematicLibrary retVal = null;
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("library")) {
                break;
            } else if (this.parser.getEventType() == 2) {
                String name = this.parser.getName();
                if (name.equals("library")) {
                    retVal = new SchematicLibrary(this.parser.getAttributeValue(nameSpace, "name").toLowerCase());
                } else if (retVal != null) {
                    if (name.equals("symbol")) {
                        SymbolTemplate sym = parseSymbol();
                        if (sym != null) {
                            retVal.addSymbol(sym);
                        }
                    } else if (name.equals("deviceset")) {
                        DeviceSet set = parseDeviceSet();
                        if (set != null) {
                            retVal.addDeviceSet(set);
                        }
                    }
                }
            }
        } while (this.parser.next() != 1);
        return retVal;
    }

    @Keep
    public Template test() {
        Log.d("test","test2");
        Log.d("test","test1");
        return null;
    }

    private SymbolTemplate parseSymbol() throws XmlPullParserException, IOException {
        skipTo("symbol");
        SymbolTemplate retVal = null;
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("symbol")) {
                break;
            } else if (this.parser.getEventType() == 2) {
                String name = this.parser.getName();
                if (name.equals("symbol")) {
                    retVal = new SymbolTemplate(this.parser.getAttributeValue(nameSpace, "name"));
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
                    } else if (name.equals("pin")) {
                        retVal.add(readPin());
                    }
                    */
                }
            }
        } while (this.parser.next() != 1);
        return retVal;
    }

    private DeviceSet parseDeviceSet() throws XmlPullParserException, IOException {
        skipTo("deviceset");
        DeviceSet retVal = null;
        do {
            if (this.parser.getEventType() == 3 && this.parser.getName().equals("deviceset")) {
                break;
            } else if (this.parser.getEventType() == 2) {
                String name = this.parser.getName();
                if (name.equals("deviceset")) {
                    retVal = new DeviceSet(this.parser.getAttributeValue(nameSpace, "name"));
                } else if (name.equals("gate")) {
                    String gateName = getStringValue("name");
                    retVal.put(gateName, new Gate(gateName, this.parser.getAttributeValue(nameSpace, "symbol"), getFloatValue("x"), getFloatValue("y"), this.parser.getAttributeValue(nameSpace, "addlevel") != null));
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
                    } else if (name.equals("circle")) {
                        result = readCircle();
                    } else if (name.equals("rectangle")) {
                        result = readRectangle();
                    } else if (name.equals("text")) {
                        result = readText();
                    } else if (name.equals("polygon")) {
                        result = parsePolygon();
                    }
                    */
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
        skipTo("nets");
        requireStart("nets");
        String currentName = null;
        while (this.parser.next() != 1) {
            if (this.parser.getEventType() != 3 || !this.parser.getName().equals("nets")) {
                if (this.parser.getEventType() == 2) {
                    String name = this.parser.getName();
                    Template result = null;
                    if (name.equals("net")) {
                        currentName = getStringValue("name");
                    } else if (name.equals("junction")) {
                        result = readJunction();
                    } else if (name.equals("wire")) {
                        result = readWire();
                    } else if (name.equals("label")) {
                        if (currentName != null) {
                            Template t = readLabel();
                            t.setText(currentName); //ZMIENIONE
                            result = t;
                        }
                    }
                    if (result != null) {
                        result.AddCopyToLayer(layerManager, new PointF(0.0f, 0.0f), Rotation.ZERO);
                    }
                }
            } else {
                return;
            }
        }
    }

    private void parseBusses() throws XmlPullParserException, IOException {
        skipTo("busses");
        requireStart("busses");
        String currentName = null;
        while (this.parser.next() != 1) {
            if (this.parser.getEventType() != 3 || !this.parser.getName().equals("busses")) {
                if (this.parser.getEventType() == 2) {
                    String name = this.parser.getName();
                    Template result = null;
                    if (name.equals("bus")) {
                        currentName = getStringValue("name");
                    } else if (name.equals("wire")) {
                        result = readWire();
                    } else if (name.equals("label")) {
                        if (currentName != null) {
                            Template t = readLabel();
                            t.setText(currentName);
                            result = t;
                        }
                    }
                    if (result != null) {
                        result.AddCopyToLayer(layerManager, new PointF(0.0f, 0.0f), Rotation.ZERO);
                    }
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

    public WireTemplate readWire() throws XmlPullParserException, IOException {
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
                return new WireTemplate(new PointF(x1, y1), getLayer(layer), new PointF(x2, y2), (double) width, 0.0d, style);
            }
            return new WireTemplate(new PointF(x1, y1), getLayer(layer), new PointF(x2, y2), (double) width, (double) (-Float.parseFloat(curve)), style);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    private JunctionTemplate readJunction() throws XmlPullParserException, IOException {
        requireStart("junction");
        try {
            return new JunctionTemplate(new PointF(getFloatValue("x"), getFloatValue("y")), layerManager.getLayer(91));
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
            String rot = getStringValueDef("rot", "R0");
            String font = getStringValueDef("font", "default");
            Rotation rotation = Rotation.getRotation(rot);
            int ratio = getIntegerValueDef("ratio", 8);
            String align = getStringValueDef("align", "bottom-left");
            AlignType alignType = EagleAlign.getAlign(align);
            String text = "";
            try {
                text = this.parser.nextText();
            } catch (XmlPullParserException e) {
            }
            return new SchematicTextTemplate(
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

    private LabelTemplate readLabel() throws XmlPullParserException, IOException {
        requireStart("label");
        try {
            float x = getFloatValue("x");
            float y = getFloatValue("y");
            float size = getFloatValue("size");
            int ratio = getIntegerValueDef("ratio", 8);
            int layer = getIntegerValue("layer");
            Rotation rotation = Rotation.getRotation(getStringValueDef("rot", "R0"));
            String font = getStringValueDef("font", "default");
            boolean xref = getYesNoValueDef("xref", false);
            return new LabelTemplate(
                    new PointF(x, y),
                    layerManager.getLayer(layer),
                    font,
                    size,
                    ratio,
                    rotation,
                    xref
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
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
            float sizef = getFloatValueDef("diameter", -1.0f);
            return new PadTemplate(
                    layerManager,
                    new PointF(x, y),
                    getLayer(17),
                    drill,
                    (double) sizef,
                    PadTemplate.getType(getStringValue("shape")),
                    Rotation.getRotation(getStringValueDef("rot", "R0"))
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logError();
            return null;
        }
    }

    @Keep
    private PinTemplate readPin() throws XmlPullParserException, IOException {
        requireStart("pin");
        /*
        pin jest rysowany na 2 warstwach
        kreska na 94 (symbols)
        kółko i nazwa pinu na 93 (pins)

         */
        try {
            String name = getStringValue("name");
            float x = getFloatValue("x");
            float y = getFloatValue("y");
            String visible = getStringValueDef("visible", "both");
            String length = getStringValueDef("length", "long");
            String function = getStringValueDef("function", "none");
            String rot = getStringValueDef("rot", "R0");
            return new PinTemplate(
                    new PointF(x, y),
                    layerManager.getLayer(94),
                    name,
                    PinTemplate.getVisibility(visible),
                    PinTemplate.getLength(length),
                    PinTemplate.getFunction(function),
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
        Log.d("SchematicParser", "Schematic parsing error");
    }

    private Layer getLayer(int layer) {
        return layerManager.getLayer(layer);
    }

    public Map<String, SchematicLibrary> getLibraries() {
        return this.libsMap;
    }

    public List<Layer> getLayersList() {
        return this.layersList;
    }
}
