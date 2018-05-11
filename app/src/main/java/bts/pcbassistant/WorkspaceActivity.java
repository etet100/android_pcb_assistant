package bts.pcbassistant;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;

import bts.pcbassistant.drawing.BaseTextDrawable;
import bts.pcbassistant.utils.DropboxManager;
import bts.pcbassistant.utils.FilenameHelpers;
import bts.pcbassistant.utils.MetricsHelpers;

@EActivity(R.layout.activity_workspace)
public class WorkspaceActivity extends AppCompatActivity {

    @FragmentById(R.id.workspace)
    WorkspaceFragment workspaceFragment;

    @AfterViews
    void init() {

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setLogo(R.drawable.ic_launcher);
        //ab.setDisplayUseLogoEnabled(true);

        MetricsHelpers.initDisplayMetrics(
                getWindowManager().getDefaultDisplay()
        );

        //otwórz demo
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("action")) {
            //otwórz konkretny plik
            if (intent.getStringExtra("action").equals("openFile")) {
                /*
                if (FilenameHelpers.getPathType(intent.getStringExtra("fileName")).equals("dropbox")) {
                    DropboxManager.checkInitialization(this);
                    if (DropboxManager.getState() == DropboxManager.STATE_WAITING_OAUTH)
                        return;
                }
                workspaceFragment.openProject(
                        intent.getStringExtra("fileName"),
                        intent.getBooleanExtra("openBoard", false),
                        intent.getBooleanExtra("openSchematic", false)
                );*/
                return;
            } else
            //wybór pliku
            if (intent.getStringExtra("action").equals("openSelectFileDialog")) {
                workspaceFragment.openFileDialog();
            }
            intent.removeExtra("action");
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseTextDrawable.vectorTypeface = Typeface.createFromAsset(getAssets(), "eagle_vector.otf");
        BaseTextDrawable.vectorTypeface15 = Typeface.createFromAsset(getAssets(), "eagle_vector15.otf");
        BaseTextDrawable.vectorTypeface20 = Typeface.createFromAsset(getAssets(), "eagle_vector20.otf");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("action")) {
            if (FilenameHelpers.getPathType(intent.getStringExtra("fileName")).equals("dropbox")) {
                //dropbox nie był zalogowany?
                DropboxManager.checkInitialization(this);
                if (DropboxManager.getState() == DropboxManager.STATE_WAITING_OAUTH)
                    return;
                if (DropboxManager.getState() == DropboxManager.STATE_NONE)
                    return;
            }
            workspaceFragment.openProject(
                    intent.getStringExtra("fileName"),
                    intent.getBooleanExtra("openBoard", false),
                    intent.getBooleanExtra("openSchematic", false)
            );
            intent.removeExtra("action");
        }
    }
/*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //workspaceFragment = savedInstanceState.getParcelable("workspaceFragment");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelable("workspaceFragment", workspaceFragment);
        //outState.
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (!getSupportActionBar().isShowing()) {
                getSupportActionBar().show();
                return false;
            }
            if (workspaceFragment.loadingInProgress()) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
