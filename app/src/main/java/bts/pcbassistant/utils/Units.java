package bts.pcbassistant.utils;

public class Units {

    public enum Unit {
        Inch,
        Mm,
        Mil
    }

    public Unit getUnit(String s) {
        if (s.equalsIgnoreCase("inch")) {
            return Unit.Inch;
        }
        if (s.equalsIgnoreCase("mm")) {
            return Unit.Mm;
        }
        return Unit.Mil;
    }

    public static float convertToMm(float in, Unit from) {
        if (from == Unit.Inch) {
            return in * 25.4f;
        }
        if (from == Unit.Mil) {
            return in * 0.0254f;
        }
        return in;
    }
}
