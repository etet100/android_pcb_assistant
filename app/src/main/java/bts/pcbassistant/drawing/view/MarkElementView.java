package bts.pcbassistant.drawing.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import static android.animation.ValueAnimator.REVERSE;

/**
 * Created by And on 2017-04-22.
 */

public class MarkElementView extends View {
    //private Animation animation;
    private ValueAnimator animator = null;
    private RectF rect = null;

    public MarkElementView(Context context) {
        super(context);
        if (context instanceof Activity)
            init((Activity)context);
    }

    public MarkElementView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof Activity)
            init((Activity)context);
    }

    public MarkElementView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof Activity)
            init((Activity)context);
    }

    public MarkElementView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (context instanceof Activity)
            init((Activity)context);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        if (visibility == VISIBLE) {
            //startAnimation(animation);
        };
    }

    public void setPosition(RectF p) {

        rect = p;

        if (animator != null)
            animator.cancel();

        animator = ValueAnimator.ofInt(0, 10);
        animator.setRepeatMode(REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(1000,1000);

                int val = (int)animation.getAnimatedValue();

                lp.leftMargin = (int)rect.left - val;
                lp.topMargin = (int)rect.top - val;
                lp.width = (int)rect.width() + val + val;
                lp.height = (int)rect.height() + val + val;

                setLayoutParams(lp);

            }

        });
        animator.start();
    }

    private void init(Activity context) {
        //animation = AnimationUtils.loadAnimation(context, R.anim.mark_element_view_animation);
    }

    final int size = 10;

    @Override
    public void draw(Canvas canvas) {
        if (!this.isInEditMode()) {
            super.draw(canvas);

            Paint p = new Paint();
            p.setStrokeWidth(7.0f);
            p.setColor(Color.GRAY);

            int w = this.getWidth() - 1;
            int h = this.getHeight() - 1;

            //lewa góra
            canvas.drawLine(0, 0, size, 0, p);
            canvas.drawLine(0, 0, 0, size, p);

            //lewy doł
            canvas.drawLine(0, h, size, h, p);
            canvas.drawLine(0, h, 0, h - size, p);

            //prawa góra
            canvas.drawLine(w, 0, w - size, 0, p);
            canvas.drawLine(w, 0, w, size, p);

            //prawy doł
            canvas.drawLine(w, h, w - size, h, p);
            canvas.drawLine(w, h, w, h - size, p);
        }
    }
}
