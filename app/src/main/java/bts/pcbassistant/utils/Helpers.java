package bts.pcbassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import bts.pcbassistant.drawing.Rotation;

public class Helpers {

    public static PointF moveRotatePoint(PointF input, PointF move, Rotation rotation) {
        //PointF rotated;
        float[] inputPoint = new float[]{input.x, input.y};
        if (rotation.getAngle() != 0.0f) {
            Matrix matrix = new Matrix();
            matrix.setRotate(rotation.getAngle());
            matrix.mapPoints(inputPoint);
        }
        if (rotation.isMirrored()) {
            inputPoint[0] = -inputPoint[0];
//            rotated = new PointF(-inputPoint[0], inputPoint[1]);
        }/* else {
            rotated = new PointF(inputPoint[0], inputPoint[1]);
        }*/
        return new PointF(inputPoint[0] + move.x, inputPoint[1] + move.y);
    }

    public static PointF rotatePoint(PointF input, PointF center, Rotation rotation) {
        //PointF rotated;
        float[] inputPoint = new float[]{input.x - center.x, input.y - center.y};
        if (rotation.getAngle() != 0.0f) {
            Matrix matrix = new Matrix();
            matrix.setRotate(rotation.getAngle());
            matrix.mapPoints(inputPoint);
        }
        if (rotation.isMirrored()) {
            inputPoint[0] = -inputPoint[0];
        } else {
            //rotated = new PointF(inputPoint[0], inputPoint[1]);
        }
        return new PointF(inputPoint[0] + center.x, inputPoint[1] + center.y);
    }

    public static float[] rotateAndMovePoints(float[] input, PointF center, Rotation rotation) {
        Matrix matrix = new Matrix();
        if (rotation.getAngle() != 0.0f) {
            matrix.setRotate(rotation.getAngle());
            matrix.mapPoints(input);
        }
        if (rotation.isMirrored()) {
            //odwróc wartości X
            for (int i=0; i<input.length; i+=2) {
                input[i] = center.x-input[i];
                input[i+1]+=center.y;
            }
        } else
            for (int i=0; i<input.length; i+=2) {
                input[i]+=center.x;
                input[i+1]+=center.y;
            }
        return input;
    }

    public static RectF moveRectF(RectF input, float xMovement, float yMovement) {
        return new RectF(input.left + xMovement, input.top + yMovement, input.right + xMovement, input.bottom + yMovement);
    }

    public static Rect moveRect(Rect input, int xMovement, int yMovement) {
        return new Rect(input.left + xMovement, input.top + yMovement, input.right + xMovement, input.bottom + yMovement);
    }

    public static PointF movePointF(PointF input, float xMovement, float yMovement) {
        return new PointF(input.x + xMovement, input.y + yMovement);
    }

    public static double selectValue(double valueL1, double valueL2, int shownLayers) {
        if (shownLayers == 1) {
            return valueL1;
        }
        if (shownLayers == 2) {
            return valueL2;
        }
        return Math.max(valueL1, valueL1);
    }

    public static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt((x * x) + (y * y)); //ZMIENIONE
    }

    public static void midPointF(PointF point, MotionEvent event) {
        point.set((event.getX(0) + event.getX(1)) / 2.0f, (event.getY(0) + event.getY(1)) / 2.0f);
    }

    public static PointF midPointF(PointF point, PointF point2) {
        return new PointF((point.x + point2.x) / 2.0f, (point.y + point2.y) / 2.0f);
    }

    public static PointF midPointF(float x1, float y1, float x2, float y2) {
        return new PointF((x1 + x2) / 2.0f, (y1 + y2) / 2.0f);
    }

    public static Point midPoint(Rect rect) {
        return new Point(
                (int)Math.floor((rect.left + rect.right) / 2.0f),
                (int)Math.floor((rect.top + rect.bottom) / 2.0f)
        );
    }

    public static RectF getScreenSize(Activity c) {
        Display display = c.getWindowManager().getDefaultDisplay();
        return new RectF(0.0f, 0.0f, (float) display.getWidth(), (float) display.getHeight());
    }

    public static Matrix getInitMatrix(Activity c, RectF boardSize) {
        Matrix retVal = new Matrix();
        RectF screenSize = getScreenSize(c);
        float scale = Math.min(screenSize.height() / boardSize.height(), screenSize.width() / boardSize.width()) * 0.8f;
        retVal.setScale(scale, scale);
        retVal.postTranslate((screenSize.width() - (boardSize.width() * scale)) / 2.0f, (boardSize.height() * scale) + ((screenSize.height() - (boardSize.height() * scale)) / 2.0f));
        return retVal;
    }

    /* wersja biorąca pod uwagę wielkość EagleView zamiast screen size */
    public static Matrix getInitMatrix(RectF viewSize, RectF boardSize) {
        Matrix retVal = new Matrix();
        float scale = Math.min(viewSize.height() / boardSize.height(), viewSize.width() / boardSize.width()) * 0.8f;
        retVal.setScale(scale, scale);
        retVal.postTranslate((viewSize.width() - (boardSize.width() * scale)) / 2.0f, (boardSize.height() * scale) + ((viewSize.height() - (boardSize.height() * scale)) / 2.0f));
        return retVal;
    }

    public static void dumpEvent(MotionEvent event) {
        String[] names = new String[]{"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEventCompat.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == 5 || actionCode == 6) {
            sb.append("(pid ").append(action >> 8);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount()) {
                sb.append(";");
            }
        }
        sb.append("]");
        Log.d("Touch", sb.toString());
    }

    public static RectF getViewSize(View view) {
        return new RectF(0.0f, 0.0f, (float) view.getWidth(), (float) view.getHeight());
    }

    public static String getRealPathFromURI(Context ctx, Uri contentUri) {
        Cursor cursor = ctx.getContentResolver().query(contentUri, new String[]{"_data"}, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static int middle(int v1, int v2) {
        return Math.round(((float)v2 + (float)v1) / 2);
    }

    //https://stackoverflow.com/questions/4846484/md5-hashing-in-android
    /*
    public static String MD5_Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return ("000000000000000".substring(0, 32-hash.length()))+hash;
    }*/

    public static void matrixToBundle(Matrix matrix, Bundle bundle, String index) {
        float[] matrixValues = new float[9]; matrix.getValues(matrixValues);
        bundle.putFloatArray(index, matrixValues);
    }

    public static Matrix matrixFromBundle(Bundle bundle, String index) {
        float[] matrixValues = bundle.getFloatArray(index);
        Matrix currentMatrix = new Matrix();
        currentMatrix.setValues(matrixValues);
        return currentMatrix;
    }

}
