package bts.pcbassistant.data;

import java.util.HashMap;

import bts.pcbassistant.drawing.templates.SymbolTemplate;

public class SchematicLibrary {
    private HashMap<String, DeviceSet> deviceSets;
    private String name;
    private HashMap<String, SymbolTemplate> symbols;

    public SchematicLibrary(String name) {
        this.deviceSets = new HashMap();
        this.symbols = new HashMap();
        this.name = name;
    }

    public void addDeviceSet(DeviceSet set) {
        this.deviceSets.put(set.getName(), set);
    }

    public void addSymbol(SymbolTemplate symbol) {
        this.symbols.put(symbol.getName(), symbol);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, DeviceSet> getDeviceSets() {
        return this.deviceSets;
    }

    public HashMap<String, SymbolTemplate> getSymbols() {
        return this.symbols;
    }

    public SymbolTemplate getSymbol(String deviceSet, String gate) {
        if (!this.deviceSets.containsKey(deviceSet)) {
            return null;
        }
        DeviceSet set = (DeviceSet) this.deviceSets.get(deviceSet);
        if (!set.containsKey(gate)) {
            return null;
        }
        return (SymbolTemplate) this.symbols.get(((Gate) set.get(gate)).getSymbol());
    }

    public boolean hasAddLevel(String deviceSet, String gate) {
        if (this.deviceSets.containsKey(deviceSet)) {
            return false;
        }
        Gate g = (Gate) ((DeviceSet) this.deviceSets.get(deviceSet)).get(gate);
        boolean z = g != null && g.isAddLevel();
        return z;
    }
}
