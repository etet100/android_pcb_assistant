package bts.pcbassistant.data;

import android.content.Context;
import android.graphics.RectF;

import java.io.IOException;

import bts.pcbassistant.parsing.AssetInputStreamWithCallbacksHandler;
import bts.pcbassistant.parsing.InputStreamWithCallbacks;
import bts.pcbassistant.parsing.SchematicParser;

/**
 * Created by And on 2017-04-23.
 */

public class SchematicDataSource extends EagleDataSource {

    public SchematicDataSource(String filePath, Context context, PartsManager partsManager, final AssetInputStreamWithCallbacksHandler callback) throws IOException {
        super(TYPE.Schematic, partsManager);

        if (!filePath.toLowerCase().endsWith(".sch")) {
            throw new IOException();
        }

        try {
            InputStreamWithCallbacks istr =
                    (InputStreamWithCallbacks) InputStreamWithCallbacks.getStream(this, context, filePath);
            istr.setCallbackHandler(new AssetInputStreamWithCallbacksHandler() {
                @Override
                public void onProgress(EagleDataSource dataSource, int loadingProgress) {
                    callback.onProgress(dataSource, loadingProgress);
                }
            });

            new SchematicParser(
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
        return this.getLayers().getLayer(94).getDimension();
    }

        /*
    public void selectPartByName(String partName) {
        for (Part p : layers.parts) {
            if (p.getName().compareTo(partName) == 0) {
                selectedPart = p;
                return;
            }
        }
    }
        */

}
