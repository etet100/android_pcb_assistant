package bts.pcbassistant.parsing;

import bts.pcbassistant.data.EagleDataSource;

/**
 * Created by a on 2017-05-07.
 */

public interface AssetInputStreamWithCallbacksHandler {
    void onProgress(EagleDataSource dataSource, int progress);
}

