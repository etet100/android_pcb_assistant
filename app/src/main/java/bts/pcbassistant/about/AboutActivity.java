package bts.pcbassistant.about;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.an.deviceinfo.device.model.Device;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.Scanner;

import bts.pcbassistant.R;

@EActivity(R.layout.activity_about)
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @ViewById
    TextView textDevice;

    @ViewById
    TextView textLibraries;

    @ViewById
    TextView textChangelog;

    @AfterViews
    public void init() {
        //setContentView(R.layout.activity_about);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Device device = new Device(this);

        textDevice.setText(
                device.getDevice()+"\r\n"+
                device.getHardware()+"\r\n"+
                device.getFingerprint()+"\r\n"+
                device.getModel()+"\r\n"+
                device.getOsVersion()+"\r\n"+
                device.getDisplayVersion()+"\r\n"+
                device.getBuildTime()+"\r\n"
        );

        Scanner scanner;
        try {
            scanner = new Scanner(this.getResources().getAssets().open("changelog.txt")).useDelimiter("\\A");
            textChangelog.setText(scanner.hasNext() ? scanner.next() : "");
            scanner.close();

            scanner = new Scanner(this.getResources().getAssets().open("libraries.txt")).useDelimiter("\\A");
            textLibraries.setText(scanner.hasNext() ? scanner.next() : "");
            scanner.close();
        } catch (IOException e) {
        }



        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
