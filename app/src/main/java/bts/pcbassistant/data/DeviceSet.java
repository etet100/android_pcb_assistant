package bts.pcbassistant.data;

import java.util.HashMap;

public class DeviceSet extends HashMap<String, Gate> {
    private String name;

    public DeviceSet(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
