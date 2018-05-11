package bts.pcbassistant.drawing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by And on 2017-04-30.
 */

public class EagleViewLayout extends ViewGroup {
    public EagleViewLayout(Context context) {
        super(context);
    }

    public EagleViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EagleViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EagleViewLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        /*final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;*/
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
