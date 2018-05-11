package bts.pcbassistant.drawing;

import bts.pcbassistant.data.Part;
import bts.pcbassistant.drawing.view.ExtendedCanvas;

public interface IDrawable {
    void Draw(ExtendedCanvas extendedCanvas);
    void setPart(Part part_);
}
