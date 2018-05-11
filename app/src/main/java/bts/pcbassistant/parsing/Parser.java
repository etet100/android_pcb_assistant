package bts.pcbassistant.data;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by a on 2017-05-07.
 */

abstract public class Parser {
    protected XmlPullParser parser = null;
    protected static String nameSpace;

    static {
        System.loadLibrary("native-parser");
    }

    static {
        nameSpace = null;
    }

    public boolean parseStream(InputStream istr) throws IOException, XmlPullParserException {
        this.parser = Xml.newPullParser();
        this.parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
        this.parser.setInput(istr, "utf-8");
        this.parser.nextTag();
        return true;
    }

    protected void skipTo(String tag) throws XmlPullParserException, IOException {
        do {
            if (this.parser.getEventType() == 2 && this.parser.getName().equals(tag)) {
                return;
            }
        } while (this.parser.next() != 1);
    }

    protected String getStringValue(String attributeName) {
        return this.parser.getAttributeValue(nameSpace, attributeName);
    }

    protected String getStringValueDef(String attributeName, String def) {
        String v = this.parser.getAttributeValue(nameSpace, attributeName);
        if (v == null) return def;
        return v;
    }

    //get LowerCase
    protected String getLCStringValueDef(String attributeName, String def) {
        return getStringValueDef(attributeName, def).toLowerCase();
    }

    protected float getFloatValue(String attributeName) {
        return Float.parseFloat(this.getStringValue(attributeName));
    }

    protected float getFloatValueDef(String attributeName, float def) {
        String v = this.getStringValue(attributeName);
        if (v == null) return def;
        return Float.parseFloat(v);
    }

    protected double getDoubleValue(String attributeName) {
        return (double)Float.parseFloat(this.getStringValue(attributeName));
    }

    protected int getIntegerValue(String attributeName) {
        return Integer.parseInt(this.getStringValue(attributeName));
    }

    protected int getIntegerValueDef(String attributeName, int def) {
        String v = this.getStringValue(attributeName);
        if (v == null) return def;
        return Integer.parseInt(v);
    }

    protected boolean getYesNoValueDef(String attributeName, boolean def) {
        String v = this.getStringValueDef(attributeName, "").toLowerCase();
        if (v.equals("yes"))
            return true;
        if (v.equals("no"))
            return false;
        return def;
    }

    protected void requireStart(String nodeName) throws XmlPullParserException, IOException {
        this.parser.require(2, nameSpace, nodeName);
    }

}
