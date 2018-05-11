package bts.pcbassistant.dialog;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bts.pcbassistant.R;

public class CustomDialogActivity extends FragmentActivity {
    public CustomDialogActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom2);
        init();
    }

    private ImageView iconView;
    private TextView topTitleView;
    private TextView titleView;
    private TextView messageView;
    private View addedView;

    private void init() {
        //dialogView = LayoutInflater.from(dialogBuilder.getContext()).inflate(getLayout(), null);
        //dialog = dialogBuilder.setView(dialogView).create();

        iconView = (ImageView)findViewById(R.id.ld_icon);
        titleView = (TextView)findViewById(R.id.ld_title);
        messageView = (TextView)findViewById(R.id.ld_message);
        topTitleView = (TextView)findViewById(R.id.ld_top_title);
    }

    public CustomDialogActivity setTopTitle(@StringRes int title) {
        return setTopTitle(string(title));
    }

    public CustomDialogActivity setTopTitle(CharSequence title) {
        topTitleView.setVisibility(View.VISIBLE);
        topTitleView.setText(title);
        return this;
    }

    public CustomDialogActivity setTopTitleColor(int color) {
        topTitleView.setTextColor(color);
        return this;
    }

    public CustomDialogActivity setIcon(Bitmap bitmap) {
        iconView.setVisibility(View.VISIBLE);
        iconView.setImageBitmap(bitmap);
        return this;
    }

    public CustomDialogActivity setIcon(Drawable drawable) {
        iconView.setVisibility(View.VISIBLE);
        iconView.setImageDrawable(drawable);
        return this;
    }

    public CustomDialogActivity setIcon(@DrawableRes int iconRes) {
        iconView.setVisibility(View.VISIBLE);
        iconView.setImageResource(iconRes);
        return this;
    }

    public CustomDialogActivity setIconTintColor(int iconTintColor) {
        iconView.setColorFilter(iconTintColor);
        return this;
    }

    public CustomDialogActivity setTitleGravity(int gravity) {
        titleView.setGravity(gravity);
        return this;
    }

    public CustomDialogActivity setMessageGravity(int gravity) {
        messageView.setGravity(gravity);
        return this;
    }

    public CustomDialogActivity setTopColor(@ColorInt int topColor) {
        findViewById(R.id.ld_color_area).setBackgroundColor(topColor);
        return this;
    }

    public CustomDialogActivity setTopColorRes(@ColorRes int topColoRes) {
        return setTopColor(color(topColoRes));
    }


    public CustomDialogActivity setView(@LayoutRes int layout) {
        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup parent = (ViewGroup)findViewById(R.id.ld_custom_view_container);
        addedView = inflater.inflate(layout, parent, true);
        return this;
    }

    public CustomDialogActivity setView(View customView) {
        ViewGroup container = (ViewGroup)findViewById(R.id.ld_custom_view_container);
        container.addView(customView);
        addedView = customView;
        return this;
    }

    /*
    public CustomDialogActivity(Context context) {
        super(context);
    }

    public CustomDialogActivity(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_custom2;
    }

    */

    private enum ButtonType {
        BUTTON_POSITIVE,
        BUTTON_NEGATIVE,
        BUTTON_NEUTRAL
    };

    private void setButton(ButtonType type, int resourceText, View.OnClickListener listener) {

        View bar = findViewById(R.id.Id_button_bar);
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
        this.setButton(ButtonType.BUTTON_POSITIVE, resourceText, listener);
    }

    public void setNeutralButton(int resourceText, View.OnClickListener listener) {
        this.setButton(ButtonType.BUTTON_POSITIVE, resourceText, listener);
    }

    protected String string(@StringRes int res) {
        return this.getString(res);
    }

    protected int color(@ColorRes int colorRes) {
        return ContextCompat.getColor(this, colorRes);
    }

}
