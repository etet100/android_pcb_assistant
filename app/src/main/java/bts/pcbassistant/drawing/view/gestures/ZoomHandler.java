package bts.pcbassistant.drawing.view.gestures;

import android.graphics.Matrix;
import android.graphics.PointF;

import bts.pcbassistant.drawing.view.ZoomImageView;

public interface ZoomHandler {
    void onZoomFinished(ZoomImageView zoomImageView, Matrix matrix, boolean fullUpdate);
    void onLongTouch(ZoomImageView zoomImageView, Matrix matrix, PointF point);
    void onSlide(ZoomImageView zoomImageView, boolean up);
}
