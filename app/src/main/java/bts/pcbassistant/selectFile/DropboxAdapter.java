package bts.pcbassistant.selectFile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bts.pcbassistant.R;
import bts.pcbassistant.utils.DropboxManager;
import bts.pcbassistant.utils.FilenameHelpers;

public class DropboxAdapter extends FileBrowserAdapter {
    private File root;
    protected List<Metadata> fileList;

    /* renamed from: bts.test4.views.FileAdapter.1 */
    class C00681 implements Comparator<File> {
        C00681() {
        }

        public int compare(File file, File file2) {
            return file.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
        }
    }

    public DropboxAdapter(Context ctx, OnUpdateHandler cbk) {
        super(ctx, cbk);
    }

    protected void init() {
        this.fileList = new ArrayList();
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
            view.setTag(((MetaFile)this.root).getParentMeta());
        } else {
            Metadata current;
            if (hasParent()) {
                current = this.fileList.get(i - 1);
            } else {
                current = this.fileList.get(i);
            }
            name.setText(current.getName());
            if (current instanceof FolderMetadata) {
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

    public MetaFile getRoot() {
        return new MetaFile();
    }

    public void setRoot(File root) {
        if (!DropboxManager.clientReady())
            return;

        this.root = (root != null)?root:this.getRoot();

        this.fileList = new ArrayList();
        try {
            ListFolderResult list = DropboxManager.getClient().files().listFolder(this.root.getPath());
            for (Metadata meta : list.getEntries()) {
                if (meta instanceof FolderMetadata || FilenameHelpers.isSchOrBrd(meta.getName()))
                    this.fileList.add(meta);
            }
            Collections.sort(this.fileList, new Comparator<Metadata>() {
                @Override
                public int compare(Metadata o1, Metadata o2) {
                    String n1 = ((o1 instanceof FileMetadata)?"F":"D") + o1.getName();
                    String n2 = ((o2 instanceof FileMetadata)?"F":"D") + o2.getName();
                    return n1.compareToIgnoreCase(n2);
                }
            });
        } catch (DbxException e) {
            Log.d("dropbox", e.getMessage());
        }

        cbk.OnUpdate();
    }

    @Override
    public File findComplementaryFile(File file) {
        String ext = FilenameHelpers.getExtension(file.getName());
        String name = FilenameHelpers.stripExtension(file.getName());
        for (Metadata f : fileList) {
            if (FilenameHelpers.stripExtension(f.getName()).equals(name)) {
                if (!FilenameHelpers.getExtension(f.getName()).equals(ext)) {
                    return new MetaFile(f);
                }
            }
        }
        return null;
    }

    private boolean hasParent() {
        return (
                this.root != null &&
                !this.root.getName().equals("/")
        );//|| this.root.getParentFile() == null) ? false : true;
    }

    public class MetaFile extends File implements Serializable {

        private Metadata meta;

        @NonNull
        @Override
        public String getName() {
            return meta!=null?meta.getName():"/";
        }

        @NonNull
        @Override
        public String getPath() {
            return meta!=null?meta.getPathDisplay():"";
        }

        @Override
        public boolean exists() {
            return true;
        }

        public FileMetadata getParentMeta() {
            if (meta != null) {
                String parent = meta.getPathDisplay().substring(0, meta.getName().length() - meta.getName().length()+1);
                if (!parent.equals("/"))
                    return null;//DropboxManager.getClient().getMetadata(parent);
            }
            return null;
        }

        @Override
        public boolean isDirectory() {
            return meta!=null?(meta instanceof FolderMetadata):true;
        }

        public MetaFile() {
            super("Dropbox");
            this.meta = null;
        }

        public MetaFile(Metadata meta) {
            super("Dropbox");
            this.meta = meta;
        }

        public MetaFile(@NonNull FileMetadata meta) {
            super("Dropbox");
            this.meta = meta;
        }

        public List<FileMetadata> getChildren() {
            //return DropboxAdapter.this.dropbox.getChildren(meta!=null?meta.getPath():"/");
            return null;
        }
    }

    public File getFile(View view) {
        return new MetaFile(
                (Metadata)view.getTag()
        );
    }

}
