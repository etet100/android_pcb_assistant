package bts.pcbassistant.drawing;

import android.graphics.Path;

import java.util.List;

import de.lighti.clipper.Paths;

/**
 * Created by a on 2017-08-12.
 */

public interface IOffsetableDrawable {
    public Paths offset(int offset);
}
