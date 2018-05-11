package bts.pcbassistant.selectFile;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.io.File;

import bts.pcbassistant.R;

@EFragment
public class SelectFileFragment extends DialogFragment { // ZMIENIONE
    private FileBrowserAdapter adapter;
    private ListView fileList;
    private MenuItem cancel;

    public SelectFileFragment() {
        super();
    }

    public static SelectFileFragment newInstance(int title) {
        SelectFileFragment frag = new SelectFileFragment_();
        //Bundle args = new Bundle();
        //args.putInt("title", title);
        //frag.setArguments(args);
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_file, container, false);

        //DODANE
        fileList = (ListView)view.findViewById(R.id.list);

        return view;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                this.getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Background
    protected void changeDirectory(File directory) {
        this.adapter.setRoot(directory);
    }

    @UiThread
    protected void update() {
        this.adapter.notifyDataSetChanged();
    }
}
