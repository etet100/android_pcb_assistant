package bts.pcbassistant.utils;

import android.graphics.DashPathEffect;

/**
 * Created by a on 2017-08-06.
 */

public class PathEffectHelpers {

    public enum Effect {
        CONTINUOUS,
        LONGDASH,
        SHORTDASH,
        DASHDOT,
        UNKNOWN
    }

    private static DashPathEffect effectLongDash;
    private static DashPathEffect effectShortDash;
    private static DashPathEffect effectDashDot;

    static {
        effectLongDash = new DashPathEffect(new float[]{2.0f, 2.0f}, 0.0f);
        effectShortDash = new DashPathEffect(new float[]{1.0f, 1.0f}, 0.0f);
        effectDashDot = new DashPathEffect(new float[]{2.0f, 0.5f, 0.2f, 0.5f}, 0.0f);
    }

    public static DashPathEffect getDashPathEffect(Effect effect) {
        switch (effect) {
            case LONGDASH:
                return PathEffectHelpers.effectLongDash;
            case SHORTDASH:
                return PathEffectHelpers.effectShortDash;
            case DASHDOT:
                return PathEffectHelpers.effectDashDot;
            case CONTINUOUS:
            case UNKNOWN:
            default:
                return null;
        }
    }

    public static float[] getPathEffect(String name) {
        if (name == null || name.equalsIgnoreCase("continuous")) {
            return null;
        }
        if (name.equalsIgnoreCase("longdash")) {
            return new float[]{2.0f, 2.0f};
        }
        if (name.equalsIgnoreCase("shortdash")) {
            return new float[]{1.0f, 1.0f};
        }
        if (name.equalsIgnoreCase("dashdot")) {
            return new float[]{2.0f, 0.5f, 0.2f, 0.5f};
        }
        return null;
    }

    public static Effect fromString(String name) {
        if (name == null || name.equalsIgnoreCase("continuous")) {
            return Effect.CONTINUOUS;
        }
        if (name.equalsIgnoreCase("longdash")) {
            return Effect.LONGDASH;
        }
        if (name.equalsIgnoreCase("shortdash")) {
            return Effect.SHORTDASH;
        }
        if (name.equalsIgnoreCase("dashdot")) {
            return Effect.DASHDOT;
        }
        return Effect.UNKNOWN;
    }

}
