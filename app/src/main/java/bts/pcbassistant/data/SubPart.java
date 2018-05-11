package bts.pcbassistant.data;

import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Created by a on 2017-08-05.
 */

public class SubPart extends Part {

    private String gate;
    private Part part;

    public SubPart(Part part, String gate) {
        this.gate = gate;
        this.part = part;
    }

    public Rect getTransformedBounds(EagleDataSource.TYPE sourceType, Matrix matrix) {
        switch (sourceType) {
            case Board:
                return this.part.getTransformedBounds(sourceType, matrix);
            case Schematic:
                return this.part.getSchematicTransformedBounds(this.gate, matrix);
        }
        return null;
    }

    public String getName() {
        return this.part.getName() + ", " + this.gate;
    }

}
