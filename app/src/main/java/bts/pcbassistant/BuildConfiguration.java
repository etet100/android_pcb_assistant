package bts.pcbassistant;

/**
 * Created by a on 2017-08-12.
 */

public class BuildConfiguration {
    final public static boolean SHOW_CONNECTED_PARTS = false;
    final public static boolean SHOW_CONNECTED_SIGNALS = true;
    final public static boolean CALCULATE_OFFSETS = true;

    public enum AMBIGUOUS_SELECTION_MODES {
        DIALOG,
        SWIPABLE_PICKER
    }
    final public static AMBIGUOUS_SELECTION_MODES AMBIGUOUS_SELECTION_MODE = AMBIGUOUS_SELECTION_MODES.SWIPABLE_PICKER;
}