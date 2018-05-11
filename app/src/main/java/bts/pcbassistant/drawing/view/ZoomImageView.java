package bts.pcbassistant.drawing.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.bornander.math.Vector2D;

import bts.pcbassistant.drawing.view.gestures.RotationGestureDetector;
import bts.pcbassistant.drawing.view.gestures.ZoomHandler;
import bts.pcbassistant.utils.Helpers;
import bts.pcbassistant.utils.MetricsHelpers;

public class ZoomImageView extends android.support.v7.widget.AppCompatImageView  implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener {

    /* TOUCH MANAGER */
    /* TOUCH MANAGER */
    /* TOUCH MANAGER */
    /* TOUCH MANAGER */
    /* TOUCH MANAGER */

    public float getAngle() {
        return angle;
    }

    private float angle = 0, scale = 1;
    private Vector2D translation = new Vector2D(0, 0);

    public void setMatrix(Matrix matrix) {
        this.matrix = new Matrix(matrix);
        this.savedMatrix = new Matrix(matrix);
    }

    public void resetMatrix() {
        this.matrix = new Matrix();
        this.savedMatrix = new Matrix();
    }

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF scaleCenter = new PointF(0, 0);
    private int direction = 0;
    private boolean rotationInProgress = false;

    private float angleDelta = 0, lastAngle = 0;

    private Handler autoUpdateHandler = null;

    private int screenOrientation;

    private PointF longPressPoint = null;

    public PointF getLongPressPoint() {
        return longPressPoint;
    }

    public void autoUpdateStop() {
        if (autoUpdateHandler != null)
            autoUpdateHandler.removeCallbacksAndMessages(null);
        autoUpdateHandler = null;
    }

    public void autoUpdateRestart() {
        autoUpdateStop();

        autoUpdateHandler = new android.os.Handler();
        autoUpdateHandler.postDelayed(
                new Runnable() {
                    public void run() {
                        autoUpdateHandler = null;
                        if (rotationInProgress == false) {
                            onTouchActionFinished(
                                    ACTIONS.REDRAW,
                                    matrix
                            );
                        }
                    }
                },
                100);
    }

    boolean swipeEnabled = false;

    @Override public boolean onSingleTapConfirmed(MotionEvent e) {  return false; }
    @Override public boolean onDoubleTapEvent(MotionEvent e) { return false; }
    @Override public boolean onDown(MotionEvent e) {
        if ((screenOrientation == Configuration.ORIENTATION_PORTRAIT && e.getX() < MetricsHelpers.cmToPixelsX(1)) ||
                (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && e.getY() > getHeight() - MetricsHelpers.cmToPixelsY(1)))
        {
            swipeEnabled = true;
        }
        //Log.d("down", "down");
        return false;
    }
    @Override public void onShowPress(MotionEvent e) { }
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }

    @Override public void onLongPress(MotionEvent e) {
        ZoomImageView.this.longPressPoint = new PointF(
                e.getX(),
                e.getY()
        );
        onTouchActionFinished(
                ACTIONS.LONGTOUCH,
                matrix
        );
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        final PointF scaleCenter = new PointF(e.getX(), e.getY());
        final float startScale = scale;
        final float endScale =
                scale > 15 ? 1f :
                        (scale * 2);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float currentVal = (float)animation.getAnimatedValue();
                float nextScale = (startScale+(currentVal*(endScale-startScale)));
                float scaleMultiplier = (nextScale / scale);

                //Log.d("test", String.format("%d %.2f", (int)animation.getAnimatedValue(), animation.getAnimatedFraction()));
                scale *= scaleMultiplier;

                matrix.set(savedMatrix);
                matrix.postScale(scaleMultiplier, scaleMultiplier, scaleCenter.x, scaleCenter.y);
                savedMatrix.set(matrix);

                onTouchActionFinished(
                        ACTIONS.UPDATE,
                        matrix
                );
            }

        });
        animator.addListener(new ValueAnimator.AnimatorListener() {

            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {	}
            @Override public void onAnimationRepeat(Animator animation) {   }

            @Override
            public void onAnimationEnd(Animator animation) {

                Log.d("test", String.format("skala %.2f", scale));
                autoUpdateRestart();

            }

        });
        animator.start();

        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        scaleCenter = new PointF(
                detector.getFocusX(),
                detector.getFocusY()
        );

        float scaleDelta = detector.getScaleFactor();
        if (scaleDelta != 1.0f) {

            matrix.set(savedMatrix);

            scale *= scaleDelta;
            matrix.postScale(scaleDelta, scaleDelta, detector.getFocusX(), detector.getFocusY());

            scaleCenter = new PointF(
                    detector.getFocusX(),
                    detector.getFocusY()
            );

            savedMatrix.set(matrix);

            onTouchActionFinished(
                    ACTIONS.UPDATE,
                    matrix
            );
            autoUpdateRestart();
        }
        return true;

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        autoUpdateRestart();
        /*
		handler.onActionFinished(
				ACTIONS.REDRAW,
				TouchManager2.this,
				matrix
		);*/
    }

    public void scrollBy(float x, float y) {
        matrix.set(this.savedMatrix);
        matrix.postTranslate(x, y);
        savedMatrix.set(this.matrix);

        onTouchActionFinished(
                ACTIONS.UPDATE,
                matrix
        );
        autoUpdateRestart();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        //Log.d("cmToPix", String.format("%d %.2f", MetricsHelpers.cmToPixelsX(1), e1.getX()));

        //e1 - punkt startu ??
        //e2 - punkt aktualny?
        if ((screenOrientation == Configuration.ORIENTATION_PORTRAIT && e1.getX() > MetricsHelpers.cmToPixelsX(1)) ||
                (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && e1.getY() < getHeight() - MetricsHelpers.cmToPixelsY(1)))
        {
            Vector2D delta = new Vector2D(-distanceX, -distanceY);
            if (delta.getX() != 0.0f || delta.getY() != 0.0f) {
                scrollBy(delta.getX(), delta.getY());
	/*			matrix.set(this.savedMatrix);
				matrix.postTranslate(delta.getX(), delta.getY());
				savedMatrix.set(this.matrix);

				handler.onActionFinished(
						ACTIONS.UPDATE,
						TouchManager2.this,
						matrix
				);
				autoUpdateRestart();
	*/
            }
        } else {
            if (swipeEnabled) {
                if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (e2.getX() <= MetricsHelpers.cmToPixelsX(1)) {
                        int yDiff = Math.round(e2.getY() - e1.getY());
                        //2cm
                        if (Math.abs(yDiff) > MetricsHelpers.cmToPixelsX(2)) {
                            onTouchActionFinished(
                                    (yDiff < 0) ? ACTIONS.SLIDEUP : ACTIONS.SLIDEDOWN,
                                    null
                            );
                            //deaktywuj do następnego kliknięcia
                            swipeEnabled = false;
                        }
                    }
                } else {
                    if (e2.getY() >= getHeight() - MetricsHelpers.cmToPixelsY(1)) {
                        int xDiff = Math.round(e2.getX() - e1.getX());
                        //2cm
                        if (Math.abs(xDiff) > MetricsHelpers.cmToPixelsY(2)) {
                            onTouchActionFinished(
                                    (xDiff > 0) ? ACTIONS.SLIDEUP : ACTIONS.SLIDEDOWN,
                                    null
                            );
                            //deaktywuj do następnego kliknięcia
                            swipeEnabled = false;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        if (rotationInProgress == false) {
            angle = -rotationDetector.getAngle();
            scaleCenter = rotationDetector.getFocus();

            onTouchActionFinished(
                    ACTIONS.UPDATE,
                    matrix
            );

            if (Math.abs(angle) > 30) {
                mRotationGestureDetector.resetAngle();
                rotate(
                        angle < 0 ? -90 : 90
                );
            }
        }
    }

    @Override
    public void OnRotationFinished(RotationGestureDetector rotationDetector) {

        if (angle != 0) {
            //wrcacaj od 0
            ValueAnimator animator = ValueAnimator.ofFloat(angle, 0);
            animator.setDuration(100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    angle = (float)animation.getAnimatedValue();
                    if (rotationInProgress == false) {
                        onTouchActionFinished(
                                ACTIONS.UPDATE,
                                matrix
                        );
                    }
                }

            });
            animator.start();
        }

    }

    public enum ACTIONS {
        UPDATE,
        REDRAW,
        LONGTOUCH,
        SLIDEUP,
        SLIDEDOWN
    }

    public Matrix getMatrix() {
        return matrix;
    }

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RotationGestureDetector mRotationGestureDetector;

    private void rotate(final float toAngle) {

        rotationInProgress = true;

        //wrcacaj od 0
        ValueAnimator frameAnimator = ValueAnimator.ofFloat(angle, 0);
        frameAnimator.setDuration(200);
        frameAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (float)animation.getAnimatedValue();
                if (rotationInProgress == false) {
                    onTouchActionFinished(
                            ACTIONS.UPDATE,
                            matrix
                    );
                }
            }

        });
        frameAnimator.start();

        ValueAnimator contentAnimator = ValueAnimator.ofFloat(0, toAngle);
        contentAnimator.setDuration(300);
        contentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ang = (float)animation.getAnimatedValue();
                matrix.set(savedMatrix);
                matrix.postRotate(ang - lastAngle, scaleCenter.x, scaleCenter.y);
                lastAngle = ang;

                savedMatrix.set(matrix);

                onTouchActionFinished(
                        ACTIONS.UPDATE,
                        matrix
                );
            }

        });
        contentAnimator.addListener(new ValueAnimator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                float ang = (float)((ValueAnimator)animation).getAnimatedValue();

                matrix.set(savedMatrix);
                matrix.postRotate(ang - lastAngle, scaleCenter.x, scaleCenter.y);
                lastAngle = ang;

                savedMatrix.set(matrix);

                onTouchActionFinished(
                        ACTIONS.UPDATE,
                        matrix
                );

                onTouchActionFinished(
                        ACTIONS.REDRAW,
                        matrix
                );

                angle = 0;
                rotationInProgress = false;
            }

            @Override public void onAnimationCancel(Animator animation) {	}
            @Override public void onAnimationRepeat(Animator animation) {   }
        });

        lastAngle = 0;
        contentAnimator.start();

    }

    /*

    WERJSA OBSŁUGIWANA 2 PALCAMI OKAZAŁO SIĘ NIEWYGODNA
    DO EWENTUALNEGO UŻYCIA W PRZYSZŁOŚCI

    boolean twoFingersDown = false;
	int GLOBAL_TOUCH_POSITION_X = 0;
	int GLOBAL_TOUCH_CURRENT_POSITION_X = 0;
	int twoFingersXDiff = 0;

	private void handleTwoFingersSlide(MotionEvent m){
		//Number of touches
		int pointerCount = m.getPointerCount();
		if (pointerCount == 2){
			int action = m.getActionMasked();
			//int actionIndex = m.getActionIndex();
			//String actionString;
			switch (action)
			{
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_DOWN:
					GLOBAL_TOUCH_POSITION_X = (int) m.getY(1);
					//actionString = "DOWN"+" current "+GLOBAL_TOUCH_CURRENT_POSITION_X+" prev "+GLOBAL_TOUCH_POSITION_X;
					//Log.d("ac", actionString);
					twoFingersDown = true;
					//odległość palców?
					twoFingersXDiff = (int)m.getX(1) - (int) m.getX(0);
					break;
				case MotionEvent.ACTION_MOVE:
					if (twoFingersDown) {
						int currentTwoFingersXDiff = (int) m.getX(1) - (int) m.getX(0);
						if (Math.abs(currentTwoFingersXDiff - twoFingersXDiff) > Math.abs(twoFingersXDiff) * 0.3f) {
							twoFingersDown = false;
							//Log.d("ac", String.format("UP %d %d %d %d", twoFingersXDiff, currentTwoFingersXDiff, Math.abs(currentTwoFingersXDiff - twoFingersXDiff), Math.abs(currentTwoFingersXDiff - twoFingersXDiff)));
						} else {
							GLOBAL_TOUCH_CURRENT_POSITION_X = (int) m.getY(1);
							int diff = GLOBAL_TOUCH_POSITION_X - GLOBAL_TOUCH_CURRENT_POSITION_X;
							if (Math.abs(diff) > 300) {
								handler.onActionFinished(
										(diff > 0)?ACTIONS.SLIDEUP:ACTIONS.SLIDEDOWN,
										TouchManager2.this,
										null
								);

								//Log.d("blu", (diff > 0) ? "up" : "dpwm");
								twoFingersDown = false;
							}
							//actionString = "Diff " + diff + " current " + GLOBAL_TOUCH_CURRENT_POSITION_X + " prev " + GLOBAL_TOUCH_POSITION_X;
							//Log.d("ac", actionString);
							break;
						}
					}
				//default:
					//actionString = "";
			}

			//pointerCount = 0;
		}
		else {
			if (twoFingersDown) {
				twoFingersDown = false;
				//Log.d("ac", "UP");
			}
		}
	}
	*/

    public boolean onTouchEvent(MotionEvent event) {

        //handleTwoFingersSlide(event);
        //e.getAction()

        boolean ret = mScaleGestureDetector.onTouchEvent(event);
        mRotationGestureDetector.onTouchEvent(event);
        if(!mScaleGestureDetector.isInProgress() && !mRotationGestureDetector.isInProgress()) {
            ret |= mGestureDetector.onTouchEvent(event);
        }
        return ret;
    }

    /* ZOOM IMAGE VIEW */
    /* ZOOM IMAGE VIEW */
    /* ZOOM IMAGE VIEW */
    /* ZOOM IMAGE VIEW */
    /* ZOOM IMAGE VIEW */

    private void onTouchActionFinished(ACTIONS action, Matrix matrix) {
        switch (action) {
            case UPDATE:
                setImageMatrix(matrix);
                if (handler != null)
                    handler.onZoomFinished(ZoomImageView.this, matrix, false);
                break;
            case REDRAW:
                if (handler != null)
                    handler.onZoomFinished(ZoomImageView.this, matrix, true);
                break;
            case LONGTOUCH:
                if (handler != null)
                    handler.onLongTouch(ZoomImageView.this, matrix, getLongPressPoint());
                break;
            case SLIDEDOWN:
            case SLIDEUP:
                if (handler != null)
                    handler.onSlide(ZoomImageView.this, (action == ACTIONS.SLIDEUP));
                break;
        }
    }

    private ZoomHandler handler;

    public void setZoomHandler(ZoomHandler handler) {
        this.handler = handler;
    }

    public ZoomImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.handler = null;
        setScaleType(ScaleType.MATRIX);

        screenOrientation = context
                .getResources()
                .getConfiguration()
                .orientation;

        /* TOUCH MANAGER */
        resetMatrix();

        mGestureDetector = new GestureDetector(getContext(), this);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(this);

        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);

        mRotationGestureDetector = new RotationGestureDetector(this);
        /* TOUCH MANAGER */
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //oldw i oldh jest 0 na starcie lub po zmienie orientacji
        //ekranu, powodowało to przesunięcie o połowe ekranu w prawo i dół
        if (oldw > 0 && oldh > 0) {
            //Log.d("scrollBy", String.format("%.2f %.2f", (w - oldw) / 2.0f, (h - oldh) / 2.0f ));
            scrollBy((w - oldw) / 2.0f, (h - oldh) / 2.0f);
        } else {
            autoUpdateRestart();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        throw new UnsupportedOperationException("not supported");
    }

    public void setImageBitmap(Bitmap bm, boolean resetMatrix) {
        super.setImageBitmap(bm);
        if (resetMatrix) {
            resetMatrix();
            setImageMatrix(getMatrix());
        }
    }

    /* zapis stanu */

    protected Parcelable getParcel() {
        Bundle bundle = new Bundle();

        Helpers.matrixToBundle(matrix, bundle, "matrix");
        Helpers.matrixToBundle(savedMatrix, bundle, "savedMatrix");

        return bundle;
    }

    protected void restoreParcel(Bundle bundle) {

        matrix = Helpers.matrixFromBundle(bundle, "matrix");
        savedMatrix = Helpers.matrixFromBundle(bundle, "savedMatrix");

    }

}
