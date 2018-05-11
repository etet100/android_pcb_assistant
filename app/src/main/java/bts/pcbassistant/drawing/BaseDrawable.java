package bts.pcbassistant.drawing;

import android.graphics.RectF;

import bts.pcbassistant.data.Part;

/**
 * Created by And on 2017-04-16.
 */

abstract public class BaseDrawable implements IDrawable {

    protected Part part = null;
    protected RectF bounds = null;

    public void setPart(Part part_) {
        this.part = part_;
    }

}
