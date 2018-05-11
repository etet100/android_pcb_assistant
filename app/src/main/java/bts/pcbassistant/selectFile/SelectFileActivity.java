package bts.pcbassistant.selectFile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.io.File;

import bts.pcbassistant.R;
import bts.pcbassistant.dialog.CustomDialogActivity;
import bts.pcbassistant.utils.DropboxManager;

@EActivity
public class SelectFileActivity extends CustomDialogActivity {

    private FileBrowserAdapter adapter;
    private ListView fileList;

    private class AdapterUpdateHandler implements FileBrowserAdapter.OnUpdateHandler {
        @Override
        public void OnUpdate() {
            SelectFileActivity.this.update();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView(R.layout.fragment_select_file);
        //setTopColorRes(R.color.colorDialogTop);
        setTopTitle("Select file to open");
        setIcon(R.drawable.ic_action_collection);

        setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectFileActivity.this.finish();
            }
        });

        fileList = (ListView)findViewById(R.id.list);

        if (savedInstanceState == null) {

            switch (this.getIntent().getStringExtra("SOURCE")) {
                case "file":
                    this.adapter = new FileAdapter(
                            this,
                            new AdapterUpdateHandler()
                    );
                    break;
                case "file_ext":
                    this.adapter = new ExtFileAdapter(
                            this,
                            new AdapterUpdateHandler()
                    );
                    break;
                case "dropbox_new_session":
                    DropboxManager.resetSession(this);
                case "dropbox":
                    this.adapter = new DropboxAdapter(
                            this,
                            new AdapterUpdateHandler()
                    );
                    break;
            }

            //Log.d("abvc", this.getActivity().getIntent().getData().toString());
            //this.getActivity().getIntent().getStringExtra("PATH")
            this.fileList.setAdapter(this.adapter);

        }

        this.fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = SelectFileActivity.this.adapter.getFile(view);
                if (file.isDirectory()) {
                    SelectFileActivity.this.changeDirectory(file);
                    return;
                }
                if (!file.exists())
                    return;

                Intent resultData = new Intent();

                if (file instanceof DropboxAdapter.MetaFile) {
                    resultData.putExtra("path", "dropbox:" + file.getPath());
                } else if (file instanceof File) {
                    resultData.putExtra("path", "file:" + file.getPath());
                }

                File compFile = SelectFileActivity.this.adapter.findComplementaryFile(file);
                if (compFile instanceof DropboxAdapter.MetaFile) {
                    resultData.putExtra("compPath", "dropbox:" + compFile.getPath());
                } else if (compFile instanceof File) {
                    resultData.putExtra("compPath", "file:" + compFile.getPath());
                }

//                resultData.setData(Uri.fromFile(file));
                SelectFileActivity.this.setResult(-1, resultData);
                SelectFileActivity.this.finish();
            }

        });
        changeDirectory(null);

        setTitle("Select file");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.adapter instanceof DropboxAdapter) {
            if (DropboxManager.getState() == DropboxManager.STATE_NONE) {
                if (DropboxManager.checkInitialization(this) == DropboxManager.STATE_OK)
                    changeDirectory(null);
            } else
            if (DropboxManager.getState() == DropboxManager.STATE_WAITING_OAUTH) {
                if (DropboxManager.checkInitialization(this) == DropboxManager.STATE_NONE) {
                    finish();
                } else if (DropboxManager.clientReady()) {
                    changeDirectory(null);
                }
            }
        }
    }

    private static FileBrowserAdapter savedAdapter;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.adapter = savedAdapter;
        this.fileList.setAdapter(this.adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        savedAdapter = adapter;
        super.onSaveInstanceState(outState);
    }

    @UiThread
    protected void update() {
        this.adapter.notifyDataSetChanged();
    }

    @Background
    protected void changeDirectory(File directory) {
        if (this.adapter != null)
            this.adapter.setRoot(directory);
    }

}
