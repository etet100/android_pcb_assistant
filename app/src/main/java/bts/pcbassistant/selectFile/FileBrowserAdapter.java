package bts.pcbassistant.selectFile;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import java.io.File;

/**
 * Created by a on 2017-05-21.
 */

public abstract class FileBrowserAdapter extends BaseAdapter {
    protected Context ctx;
    protected OnUpdateHandler cbk;

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public void setCallback(OnUpdateHandler cbk) {
        this.cbk = cbk;
    }

    protected abstract interface OnUpdateHandler {
        public abstract void OnUpdate();
    }

    public FileBrowserAdapter(Context ctx, OnUpdateHandler cbk) {
        this.ctx = ctx;
        this.cbk = cbk;
        init();
    }

    abstract protected void init();

    abstract public File getFile(View view);

    abstract public File getRoot();

    abstract public void setRoot(File root);

    abstract public File findComplementaryFile(File file);

}
