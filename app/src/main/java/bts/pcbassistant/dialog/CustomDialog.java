package bts.pcbassistant.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import bts.pcbassistant.R;

/**
 * Created by yarolegovich on 16.04.2016.
 */
public class CustomDialog extends LovelyCustomDialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_custom2;
    }

    private enum ButtonType {
        BUTTON_POSITIVE,
        BUTTON_NEGATIVE,
        BUTTON_NEUTRAL
    };

    private void setButton(ButtonType type, int resourceText, View.OnClickListener listener) {

        View bar = findView(R.id.Id_button_bar);
        Button btn = null;
        switch (type) {
            case BUTTON_POSITIVE:
                btn = (Button)bar.findViewById(R.id.ld_btn_yes);
                break;
            case BUTTON_NEGATIVE:
                btn = (Button)bar.findViewById(R.id.ld_btn_no);
                break;
            case BUTTON_NEUTRAL:
                btn = (Button)bar.findViewById(R.id.ld_btn_neutral);
                break;
        }
        btn.setText(string(resourceText));
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(listener);

        bar.setVisibility(View.VISIBLE);

    }

    public void setPositiveButton(int resourceText, View.OnClickListener listener) {
        this.setButton(ButtonType.BUTTON_POSITIVE, resourceText, listener);
    }

    public void setNegativeButton(int resourceText, View.OnClickListener listener) {
        this.setButton(ButtonType.BUTTON_NEGATIVE, resourceText, listener);
    }

    public void setNeutralButton(int resourceText, View.OnClickListener listener) {
        this.setButton(ButtonType.BUTTON_NEUTRAL, resourceText, listener);
    }
}
