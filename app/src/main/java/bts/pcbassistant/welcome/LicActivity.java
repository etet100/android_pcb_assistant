package bts.pcbassistant.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;

import bts.pcbassistant.R;
import bts.pcbassistant.dialog.CustomDialogActivity;

@EActivity
public class LicActivity extends CustomDialogActivity {

    EditText unlock;

    static {
        System.loadLibrary("native-lic");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView(R.layout.activity_lic);
        //setTopColorRes(R.color.colorDialogTop);
        setTopTitle("Unlock application");
        setIcon(R.drawable.ic_action_unlock);

        setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);

            }
        });

        ((EditText)findViewById(R.id.code1)).setText(
                this.getIntent().getStringExtra("ece3d670")
        );

        unlock = (EditText)findViewById(R.id.code2);

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = unlock.getText().toString().toLowerCase();
                if (t.equals(LicActivity.this.getIntent().getStringExtra("a00d70f1"))) {
                    Intent intent = new Intent();
                    intent.putExtra("de3b552", t);
                    LicActivity.this.setResult(2, intent);
                    LicActivity.this.finish();
                } else {
                    Toast.makeText(LicActivity.this, "Incorrect code. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
