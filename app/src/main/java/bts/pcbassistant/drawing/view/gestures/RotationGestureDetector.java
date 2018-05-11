package bts.pcbassistant.drawing.view.gestures;

import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Created by And on 2017-05-02.
 */

public class RotationGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private int ptrID1, ptrID2;
    private float mAngle, mLastAngle;

    public float getFocusX() {
        return focus.x;
    }

    public float getFocusY() {
        return focus.y;
    }

    public PointF getFocus() {
        return new PointF(focus.x, focus.y);
    }

    private PointF focus;
    private boolean reset;

    private OnRotationGestureListener mListener;

    public float getAngle() {
        return mAngle;
    }

    public float getDelta() {
        return mAngle-mLastAngle;
    }

    public boolean isInProgress() {
        return (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID);
    }

    public RotationGestureDetector(OnRotationGestureListener listener){
        mListener = listener;
        ptrID1 = INVALID_POINTER_ID;
        ptrID2 = INVALID_POINTER_ID;
        reset = false;
        focus = new PointF(-1,-1);
    }

    public void resetAngle() {
        reset = true;
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                ptrID1 = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                ptrID2 = event.getPointerId(event.getActionIndex());
                sX = event.getX(event.findPointerIndex(ptrID1));
                sY = event.getY(event.findPointerIndex(ptrID1));
                fX = event.getX(event.findPointerIndex(ptrID2));
                fY = event.getY(event.findPointerIndex(ptrID2));
                break;
            case MotionEvent.ACTION_MOVE:
                if(ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID){
                    float nfX, nfY, nsX, nsY;
                    nsX = event.getX(event.findPointerIndex(ptrID1));
                    nsY = event.getY(event.findPointerIndex(ptrID1));
                    nfX = event.getX(event.findPointerIndex(ptrID2));
                    nfY = event.getY(event.findPointerIndex(ptrID2));

                    //punkt środkowy
                    focus.x = (nsX + nfX) / 2f;
                    focus.y = (nsY + nfY) / 2f;

                    if (reset) {
                        //aktualny odczyt traktuj jak początkowy - kąt = 0;
                        fX = nfX;
                        fY = nfY;
                        sX = nsX;
                        sY = nsY;
                        mAngle = 0;
                        reset = false;
                    } else
                        mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);

                    if (mListener != null) {
                        mListener.OnRotation(this);
                    }

                    mLastAngle = mAngle;
                }
                break;
            case MotionEvent.ACTION_UP:
                ptrID1 = INVALID_POINTER_ID;
                if (mListener != null) {
                    mListener.OnRotationFinished(this);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                ptrID2 = INVALID_POINTER_ID;
                if (mListener != null) {
                    mListener.OnRotationFinished(this);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                ptrID1 = INVALID_POINTER_ID;
                ptrID2 = INVALID_POINTER_ID;
                if (mListener != null) {
                    mListener.OnRotationFinished(this);
                }
                break;
        }
        return true;
    }

    private float angleBetweenLines (float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY)
    {
        float angle1 = (float) Math.atan2( (fY - sY), (fX - sX) );
        float angle2 = (float) Math.atan2( (nfY - nsY), (nfX - nsX) );

        float angle = ((float)Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return angle;
    }

    public interface OnRotationGestureListener {
        void OnRotation(RotationGestureDetector rotationDetector);
        void OnRotationFinished(RotationGestureDetector rotationDetector);
    }
}
