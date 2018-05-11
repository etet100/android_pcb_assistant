package bts.pcbassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.an.deviceinfo.device.model.Device;
import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;

import bts.pcbassistant.utils.MetricsHelpers;
import bts.pcbassistant.welcome.LicActivity_;
import bts.pcbassistant.welcome.WelcomeFragment;
import io.fabric.sdk.android.Fabric;

@EActivity(R.layout.activity_main)
public class StartActivity extends AppCompatActivity {

    @FragmentById(R.id.fragmentWelcome)
    WelcomeFragment welcomeFragment;


    /*
    @ViewById(R.id.pager)
    MyViewPager pager;
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
    }

    static {
        System.loadLibrary("native-lic");
    }

    public native String Liccc(String input);

    @AfterViews
    void init() {

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setLogo(R.drawable.ic_launcher);

        ab.setTitle(
                ab.getTitle()+", v"+
                BuildConfig.VERSION_NAME
        );
        //ab.setDisplayUseLogoEnabled(true);

        MetricsHelpers.initDisplayMetrics(
                getWindowManager().getDefaultDisplay()
        );
    }

    public boolean verifyLicense() {
        if (BuildConfig.DEBUG) {
            return true;
        }

        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "52429e7f2938393d05c9de9c062615ec8f1b780caef4b4907789dc11c0b17f78", Context.MODE_PRIVATE);
        String s = sharedPref.getString("2615ec8f1b780caef4b4", "");

        Device device = new Device(this);
        String devHash = this.Liccc(device.getDevice()+"\r\n"+
                        device.getHardware()+"\r\n"+
                        device.getFingerprint()+"\r\n"+
                        device.getModel()+"\r\n"+
                        device.getOsVersion()+"\r\n"+
                        device.getDisplayVersion()+"\r\n"+
                        device.getBuildTime());
        //.substring(0,8);
        String devShah = this.Liccc(devHash);// + "e3e4e97b39bbd30aece3d6703de3b52cca00d70f17170412bc35a48c3668ab91");
        //.substring(0,8);
        if (!s.equals(devShah)) {
            Intent i = new Intent(this, LicActivity_.class);
            i.putExtra("ece3d670", devHash);
            i.putExtra("a00d70f1", devShah);
            Log.d("c", devShah);
            startActivityForResult(i, 1);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyLicense();
        welcomeFragment.updateRecentList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 2) {

                    Context context = this;
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            "52429e7f2938393d05c9de9c062615ec8f1b780caef4b4907789dc11c0b17f78", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("2615ec8f1b780caef4b4", data.getStringExtra("de3b552"));
                    editor.commit();

                }
                break;
        }
    }

}
