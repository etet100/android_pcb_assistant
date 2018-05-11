package bts.pcbassistant.data;

import java.util.ArrayList;
import java.util.List;

import bts.pcbassistant.drawing.BaseDrawable;

/**
 * Created by a on 2017-08-12.
 */

public class Signal {
    private String name;

    List<BaseDrawable> drawables;

    public void addDrawable(BaseDrawable drawable) {
        drawables.add(drawable);
    }

    public void addElement(String element) {
        this.elements.add(element);
    }

    public void addContactRef(ContactRef cref) {
        this.elements.add(cref.getElement());
    }

    public Signal(String name) {
        this.elements = new ArrayList<>();
        this.drawables = new ArrayList<>();
        this.name = name;
    }

    public boolean partExists(String name) {
        return this.elements.contains(name);
    }

    public List<String> getElements() {
        return elements;
    }

    public List<BaseDrawable> getDrawables() {
        return drawables;
    }

    List<String> elements;
}
