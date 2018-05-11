package bts.pcbassistant.drawing;

import java.util.HashMap;
import java.util.Map;

public class EagleAlign {
    private static Map<String, AlignType> modes = null;

    public enum AlignType {
        BL,
        BC,
        BR,
        CL,
        C,
        CR,
        TL,
        TC,
        TR
    }

    private static void generateHashMap() {
        int i = 0;
        synchronized (EagleAlign.class) {
            try {
                if (modes == null) {
                    modes = new HashMap();
                    String[] strArr = new String[9];
                    strArr[0] = "bottom-left";
                    strArr[1] = "bottom-center";
                    strArr[2] = "bottom-right";
                    strArr[3] = "center-left";
                    strArr[4] = "center";
                    strArr[5] = "center-right";
                    strArr[6] = "top-left";
                    strArr[7] = "top-center";
                    strArr[8] = "top-right";
                    AlignType[] values = AlignType.values();
                    while (i < strArr.length) {
                        modes.put(strArr[i], values[i]);
                        i++;
                    }
                }
            } catch (Throwable th) {
                Class cls = EagleAlign.class;
            }
        }
    }

    static {
        generateHashMap();
    }

    public static boolean isTop(AlignType align) {
        return (
                align == AlignType.TL ||
                align == AlignType.TC ||
                align == AlignType.TR
        );
    }

    public static boolean isCenter(AlignType align) {
        return (
                align == AlignType.CL ||
                        align == AlignType.C ||
                        align == AlignType.CR
        );
    }

    public static boolean isBottom(AlignType align) {
        return (
                align == AlignType.BL ||
                        align == AlignType.BC ||
                        align == AlignType.BR
        );
    }

    public static AlignType getAlign(String str) {
        //generateHashMap();
        return (AlignType) modes.get(str);
    }

    public static AlignType mirror(AlignType type) {
        switch (type) {
            case BL:
                return AlignType.BR;
            case BR:
                return AlignType.BL;
            case CL:
                return AlignType.CR;
            case CR:
                return AlignType.CL;
            case TL:
                return AlignType.TR;
            case TR:
                return AlignType.TL;
        }
        return type;
    }

    public static AlignType flip(AlignType type) {
        switch (type) {
            case BL:
                return AlignType.TL;
            case BR:
                return AlignType.TR;
            case TL:
                return AlignType.BL;
            case TR:
                return AlignType.BR;
        }
        return type;
    }

    public static AlignType invert(AlignType type) {
        switch (type) {
            case BL:
                return AlignType.TR;
            case BR:
                return AlignType.TL;
            case CL:
                return AlignType.CR;
            case CR:
                return AlignType.CL;
            case TL:
                return AlignType.BR;
            case TR:
                return AlignType.BL;
        }
        return type;
    }

    public static AlignType getAlignNoMirror(String str, boolean mirror) {
        if (!mirror)
            return EagleAlign.getAlign(str);
        switch (str) {
            case "bottom-left":
                return AlignType.BR;
            case "bottom-right":
                return AlignType.BL;
            case "center-left":
                return AlignType.CR;
            case "center-right":
                return AlignType.CL;
            case "top-left":
                return AlignType.TR;
            case "top-right":
                return AlignType.TL;
        }
        return EagleAlign.getAlign(str);
    }

    public static android.graphics.Paint.Align getPaintAlign(AlignType alignType) {
        return (alignType == AlignType.BL || alignType == AlignType.CL || alignType == AlignType.TL) ? android.graphics.Paint.Align.LEFT : (alignType == AlignType.BR || alignType == AlignType.CR || alignType == AlignType.TR) ? android.graphics.Paint.Align.RIGHT : android.graphics.Paint.Align.CENTER;
    }

    public static android.graphics.Paint.Align getRotatedPaintAlign(AlignType alignType, Rotation rotation) {
        if (rotation.getAngle() == 180.0f || rotation.getAngle() == 0.0f) {
            if (rotation.isMirrored() != (rotation.getAngle() == 180.0f)) {
                return (alignType == AlignType.BL || alignType == AlignType.CL || alignType == AlignType.TL) ? android.graphics.Paint.Align.RIGHT : (alignType == AlignType.BR || alignType == AlignType.CR || alignType == AlignType.TR) ? android.graphics.Paint.Align.LEFT : android.graphics.Paint.Align.CENTER;
            }
            /*
            int v1 = rotation.isMirrored()?1:0;
            int v0 = (rotation.getAngle() == 180.0f)?1:0;
            v0 = v0 ^ v1;
            if (v0 != 0) {
                return (alignType == AlignType.BL || alignType == AlignType.CL || alignType == AlignType.TL) ? android.graphics.Paint.EagleAlign.RIGHT : (alignType == AlignType.BR || alignType == AlignType.CR || alignType == AlignType.TR) ? android.graphics.Paint.EagleAlign.LEFT : android.graphics.Paint.EagleAlign.CENTER;
            }
            */
        }
        if (rotation.getAngle() != 270.0f) {
            return getPaintAlign(alignType);
        }
        return (alignType == AlignType.BL || alignType == AlignType.CL || alignType == AlignType.TL) ? android.graphics.Paint.Align.RIGHT : (alignType == AlignType.BR || alignType == AlignType.CR || alignType == AlignType.TR) ? android.graphics.Paint.Align.LEFT : android.graphics.Paint.Align.CENTER;
    }

    public static float getRotatedVerticalAlign(AlignType alignType, Rotation rotation) {
        if (rotation.getAngle() == 270.0f || rotation.getAngle() == 90.0f) {
            if (rotation.isMirrored() != (rotation.getAngle() == 270.0f)) {
                return (alignType == AlignType.TC || alignType == AlignType.TL || alignType == AlignType.TR) ? 0.0f : (alignType == AlignType.BC || alignType == AlignType.BL || alignType == AlignType.BR) ? 1.0f : 0.5f;
            }
            /*
            int v1 = rotation.isMirrored()?1:0;
            int v0 = (rotation.getAngle() == 270.0f)?1:0;
            v0 = v0 ^ v1;
            if (v0 != 0) {
                return (alignType == AlignType.TC || alignType == AlignType.TL || alignType == AlignType.TR) ? 0.0f : (alignType == AlignType.BC || alignType == AlignType.BL || alignType == AlignType.BR) ? 1.0f : 0.5f;
            }*/
        }
        if (rotation.getAngle() != 180.0f) {
            return getVerticalAlign(alignType);
        }
        return (alignType == AlignType.TC || alignType == AlignType.TL || alignType == AlignType.TR) ? 0.0f : (alignType == AlignType.BC || alignType == AlignType.BL || alignType == AlignType.BR) ? 1.0f : 0.5f;
    }

    public static float getVerticalAlign(AlignType alignType) {
        return (alignType == AlignType.C || alignType == AlignType.CL || alignType == AlignType.CR) ? 0.5f : (alignType == AlignType.TL || alignType == AlignType.TC || alignType == AlignType.TR) ? 1.0f : 0.0f;
    }

    public static float getPositiveOrNegativeVerticalAlign(AlignType alignType) {
        return (alignType == AlignType.C || alignType == AlignType.CL || alignType == AlignType.CR) ? 0f : (alignType == AlignType.TL || alignType == AlignType.TC || alignType == AlignType.TR) ? -1.0f : 1.0f;
    }

}