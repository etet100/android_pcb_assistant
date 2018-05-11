package bts.pcbassistant.data;

import java.util.HashMap;

import bts.pcbassistant.drawing.templates.PackageTemplate;

public class BoardLibrary extends HashMap<String, PackageTemplate> {
    private static final long serialVersionUID = -1914149615455105136L;
    private String name;

    public BoardLibrary(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
