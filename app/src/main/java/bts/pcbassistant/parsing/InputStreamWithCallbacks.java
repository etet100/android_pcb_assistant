package bts.pcbassistant.parsing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import bts.pcbassistant.data.EagleDataSource;
import bts.pcbassistant.utils.DropboxManager;

/**
 * Created by a on 2017-05-07.
 */

public class InputStreamWithCallbacks extends InputStream {

    private InputStream inputStream = null;
    private EagleDataSource dataSource;

    public void setCallbackHandler(AssetInputStreamWithCallbacksHandler handler) {
        this.handler = handler;
    }

    private AssetInputStreamWithCallbacksHandler handler;

    private long size, pos;
    private int lastPercents, percents;

    public InputStreamWithCallbacks(EagleDataSource dataSource, Context c, String filePath) throws IOException {
        super();

        this.dataSource = dataSource;
        pos = 0;
        size = -1;
        lastPercents = 0;

        String[] str = filePath.split(":");
        switch (str[0]) {
            case "asset":
                try {
                    //str[1] to nazwa pliku bez ścieżki
                    inputStream = c.getAssets().open(str[1]);
                } catch (IOException exc) {
                    Log.d("po", exc.getMessage());
                }
                break;
            case "file":
                inputStream = new FileInputStream(str[1]);
                break;
            case "dropbox":
                try {
                    DbxClientV2 client = DropboxManager.getClient();
                    DbxUserFilesRequests files = client.files();
                    FileMetadata meta = (FileMetadata)files.getMetadata(str[1]);
                    size = meta.getSize();
                    DbxDownloader down = files.download(str[1]);
                    inputStream = down.getInputStream();
                } catch (DbxException e) {
                    Log.d("dropbox", e.getMessage());
                }
                //return;
        }
        if (size == -1)
            size = inputStream.available();
    }

    public InputStreamWithCallbacks() throws Exception {
        throw new Exception("Empty constructor not allowed.");
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(@NonNull byte[] b) throws IOException {
        throw new IOException("Method not allowed.");
        //return super.read(b);
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        int bytes = inputStream.read(b, off, len);

        pos += bytes;
        if (pos == size) {
            percents = 100;
        } else {
            percents = Math.round((float)pos/(float)size*100f);
        }
        if (percents > lastPercents) {
            //Log.d("parser", String.format("%d %d %d", pos, size, percents));
            if (handler != null) {
                handler.onProgress(dataSource, percents);
            }
            lastPercents = percents;
        }

        return bytes;
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("Method not allowed.");
        //return super.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("Method not allowed.");
        //super.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    public static final InputStream getStream(EagleDataSource dataSource, Context c, String fileName) throws IOException {
        return new InputStreamWithCallbacks(dataSource, c, fileName);
    }

}