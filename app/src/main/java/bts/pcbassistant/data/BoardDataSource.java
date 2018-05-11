package bts.pcbassistant.data;

import android.content.Context;
import android.graphics.RectF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bts.pcbassistant.drawing.BaseDrawable;
import bts.pcbassistant.drawing.ISelectableDrawable;
import bts.pcbassistant.parsing.AssetInputStreamWithCallbacksHandler;
import bts.pcbassistant.parsing.BoardParser;
import bts.pcbassistant.parsing.InputStreamWithCallbacks;

/**
 * Created by And on 2017-04-23.
 */

public class BoardDataSource extends EagleDataSource {

    HashMap<String, Signal> signals;

    public Signal addSignal(String name) {
        Signal signal = new Signal(name);
        this.signals.put(name, signal);
        return signal;
    }

    public BoardDataSource(String filePath, Context context, PartsManager partsManager, final AssetInputStreamWithCallbacksHandler callback) throws IOException {
        super(TYPE.Board, partsManager);

        if (!filePath.toLowerCase().endsWith(".brd")) {
            throw new IOException();
        }

        //DropboxManager.dropbox.download()
        signals = new HashMap<>();

        try {
            InputStreamWithCallbacks istr =
                    (InputStreamWithCallbacks) InputStreamWithCallbacks.getStream(this, context, filePath);
            istr.setCallbackHandler(new AssetInputStreamWithCallbacksHandler() {
                @Override
                public void onProgress(EagleDataSource dataSource, int loadingProgress) {
                    callback.onProgress(dataSource, loadingProgress);
                }
            });

            new BoardParser(
                    getLayers(),
                    partsManager
            ).parseStream(istr);
            //100% - wczytane
            dataReady.set(true);
            callback.onProgress(this, 100);
        } finally {
            //ok
        }
    }

    public RectF getDimensions() {
        return this.getLayers().getLayer(20).getDimension();
    }

    public List<Part> findConnectedParts(Part part) {
        String name = part.getName();
        HashMap<String, Part> parts = new HashMap<>();
        for (Signal signal : this.signals.values()) {
            if (signal.partExists(name)) {
                for (String signalPart : signal.getElements()) {
                    if (!signalPart.equals(name) && !parts.containsKey(signalPart)) {
                        parts.put(signalPart, getPartsManager().selectByName(signalPart));
                    }
                }
            }
        }
        return new ArrayList<Part>(parts.values());
    }

    /*
        najpierw odznacz zaznaczone a potem zaznacz wszystkie
        sygnały powiązane z danym elementem
     */
    public void selectSignalsByPart(Part part) {
        for (Signal signal : this.signals.values()) {
            for (BaseDrawable drawable : signal.getDrawables()) {
                if (drawable instanceof ISelectableDrawable) {
                    ((ISelectableDrawable)drawable).setSelected(false);
                }
            }
        }
        if (part != null) {
            List<Signal> signals = this.findConnectedSignals(part);
            for (Signal signal : signals) {
                for (BaseDrawable drawable : signal.getDrawables()) {
                    if (drawable instanceof ISelectableDrawable) {
                        ((ISelectableDrawable) drawable).setSelected(true);
                    }
                }
            }
        }
    }

    /*
        znajdź wszystkie sygnały połączone z danym elementem
     */
    public List<Signal> findConnectedSignals(Part part) {
        String name = part.getName();
        List<Signal> signals = new ArrayList<>();
        for (Signal signal : this.signals.values()) {
            if (signal.partExists(name)) {
                signals.add(signal);
            }
        }
        return signals;
    }

}
