package bts.pcbassistant.data;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import bts.pcbassistant.parsing.AssetInputStreamWithCallbacksHandler;

/**
 * Created by And on 2017-04-23.
 */

public abstract class EagleDataSource implements Parcelable {

    protected LayerManager layers;

    private PartsManager partsManager;
    //protected File file;
    protected TYPE type;

    //tylko płytka może być flipped
    protected boolean flipped;

    public PartsManager getPartsManager() {
        return partsManager;
    }

    public Part getSelectedPart() {
        return selectedPart;
    }

    protected Part selectedPart;

    protected AtomicBoolean dataReady;

    public static EagleDataSource createDataSource(String filePath, Context context, PartsManager partsManager, AssetInputStreamWithCallbacksHandler callback) {
        //File file = new File(filePath);
        try {
            switch (decodeFileType(filePath)) {
                case Board:
                    return new BoardDataSource(filePath, context, partsManager, callback);
                case Schematic:
                    return new SchematicDataSource(filePath, context, partsManager, callback);
                default:
                    return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public abstract RectF getDimensions();

    //public abstract void selectPartByName(String partName);

    /*
    public List<Part> getParts() {
        //return getLayers().getParts();
    }
    */

    public EagleDataSource(TYPE type, PartsManager partsManager) {
        this.layers = new LayerManager(this);
        this.partsManager = partsManager;
        this.type = type;
        this.dataReady = new AtomicBoolean(false);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //typ źródła danych
        dest.writeInt(type.ordinal());
    }

    public enum TYPE {
        Board,
        Schematic,
        None,

        File,
        Asset,
        Dropbox
    };

    public static TYPE decodeFileType(String filePath) {
        if (filePath.toLowerCase().endsWith(".brd")) {
            return TYPE.Board;
        } else
        if (filePath.toLowerCase().endsWith(".sch")) {
            return TYPE.Schematic;
        }
        return TYPE.None;
    }

    public static TYPE decodeFileType(File file) {
        return EagleDataSource.decodeFileType(file.getName());
    }

    public TYPE getType() {
        return type;
    }

    public boolean getDataReady() {
        return this.dataReady.get();
    }

    public LayerManager getLayers() {
        return layers;
    }

    public ArrayList<Part> findNearest(PointF point) {
        ArrayList<Part> list = new ArrayList<>();
        for (Part part : partsManager.getParts()) {
            Part inBoundPart = part.inBounds(this.type, point);
            if (inBoundPart != null) {
                list.add(inBoundPart);
            }
        }
        return list;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void flipView() {
        flipped = !flipped;
    }

}
