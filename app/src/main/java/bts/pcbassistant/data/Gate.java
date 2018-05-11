package bts.pcbassistant.data;

public class Gate {
    private boolean addLevel;
    private String name;
    private String symbol;
    private float f4x;
    private float f5y;

    public Gate(String name, String symbol, float x, float y, boolean addLevel) {
        this.name = name;
        this.symbol = symbol;
        this.f4x = x;
        this.f5y = y;
        this.addLevel = addLevel;
    }

    public boolean isAddLevel() {
        return this.addLevel;
    }

    public void setAddLevel(boolean addLevel) {
        this.addLevel = addLevel;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public float getX() {
        return this.f4x;
    }

    public void setX(float x) {
        this.f4x = x;
    }

    public float getY() {
        return this.f5y;
    }

    public void setY(float y) {
        this.f5y = y;
    }
}
