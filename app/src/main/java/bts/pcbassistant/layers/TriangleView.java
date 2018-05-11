package bts.pcbassistant.layers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by a on 2017-05-27.
 */

public class TriangleView extends View {

    Paint mPaint;
    Path mPath;

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPath = null;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mPath == null) {
            mPath = new Path();
            mPath.moveTo(0, 0);
            mPath.lineTo(0, this.getHeight());
            mPath.lineTo( this.getHeight(), 0);
            mPath.close();
        }

        canvas.drawPath(mPath, mPaint);
    }

}
