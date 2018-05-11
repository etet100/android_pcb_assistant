package bts.pcbassistant.selectFile;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import junit.framework.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import bts.pcbassistant.R;
import bts.pcbassistant.utils.FilenameHelpers;

public class ExtFileAdapter extends FileBrowserAdapter {
    private File root;
    private Context ctx;
    protected List<File> fileList;

    public ExtFileAdapter(Context ctx, OnUpdateHandler cbk) {
        super(ctx, cbk);
        this.ctx = ctx;
    }

    @Override
    protected void init() {
        this.fileList = new ArrayList();
    }

    @Override
    public File getFile(View view) {
        return (File)view.getTag();
    }

    @Override
    public File getRoot() {
        return Environment.getExternalStorageDirectory();
    }

    /* renamed from: bts.test4.views.FileAdapter.1 */
    class C00681 implements Comparator<File> {
        C00681() {
        }

        public int compare(File file, File file2) {
            return file.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
        }
    }

    public int getCount() {
        if (hasParent()) {
            return this.fileList.size() + 1;
        }
        return this.fileList.size();
    }

    public Object getItem(int i) {
        if (!hasParent()) {
            return this.fileList.get(i);
        }
        if (i == 0) {
            return null;
        }
        return this.fileList.get(i - 1);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(this.ctx).inflate(R.layout.list_item_file, viewGroup, false);
        }
        TextView name = (TextView) view.findViewById(R.id.text1);
        ImageView icon = (ImageView) view.findViewById(R.id.iv_icon);
        if (i == 0 && hasParent()) {
            icon.setImageResource(R.drawable.ic_action_back);
            name.setText(R.string.back);
            view.setTag(this.root.getParentFile());
        } else {
            File current;
            if (hasParent()) {
                current = this.fileList.get(i - 1);
            } else {
                current = this.fileList.get(i);
            }
            name.setText(current.getName());
            if (current.isDirectory()) {
                icon.setImageResource(R.drawable.ic_action_collection);
            } else {
                icon.setImageResource(
                        FilenameHelpers.filenameToLightDrawable(current.getName())
                );
            }
            view.setTag(current);
        }
        return view;
    }

    public void setRoot(File root) {
        int i = 0;

        if (root == null) {
            root = this.getRoot();
        }

        boolean ex = root.exists();
        boolean f = root.isFile();

        /*
        if (root.isFile()) {
            root = new File(root.getAbsolutePath());
        }
        //File b = root.getParentFile();
*/
        this.root = root;
        File[] files = root.listFiles();
        if (files == null) {
            //brak uprawnień czy co ??
            this.fileList = new ArrayList();
            cbk.OnUpdate();
            return;
        }
        Assert.assertNotNull(files);
        Arrays.sort(files, new C00681());
        this.fileList = new ArrayList();
        if (files != null) {
            File file;
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    this.fileList.add(file2);
                }
            }
            int length = files.length;
            while (i < length) {
                File file2 = files[i]; //ZMIENIONE
                if (!file2.isDirectory() && (file2.getName().toLowerCase().endsWith(".brd") || file2.getName().toLowerCase().endsWith(".sch"))) {
                    this.fileList.add(file2);
                }
                i++;
            }
        }

        cbk.OnUpdate();
        //notifyDataSetChanged();
    }

    @Override
    public File findComplementaryFile(File file) {
        String ext = FilenameHelpers.getExtension(file.getName());
        String name = FilenameHelpers.stripExtension(file.getName());
        for (File f : fileList) {
            if (FilenameHelpers.stripExtension(f.getName()).equals(name)) {
                if (!FilenameHelpers.getExtension(f.getName()).equals(ext)) {
                    return f;
                }
            }
        }
        return null;
    }

    private boolean hasParent() {
        return (this.root == null || this.root.getParentFile() == null) ? false : true;
    }
}
