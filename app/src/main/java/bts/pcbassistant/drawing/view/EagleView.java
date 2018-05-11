package bts.pcbassistant.drawing.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import bts.pcbassistant.BuildConfig;
import bts.pcbassistant.BuildConfiguration;
import bts.pcbassistant.R;
import bts.pcbassistant.data.BoardDataSource;
import bts.pcbassistant.data.EagleDataSource;
import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.data.Part;
import bts.pcbassistant.data.PartsManager;
import bts.pcbassistant.dialog.CustomDialog;
import bts.pcbassistant.drawing.view.gestures.ZoomHandler;
import bts.pcbassistant.utils.Helpers;
import bts.pcbassistant.utils.MetricsHelpers;

import static android.graphics.Bitmap.Config;
import static android.graphics.Bitmap.createBitmap;

/**
 * Created by And on 2017-04-23.
 */

@EView
public class EagleView extends ZoomImageView {

    public interface Handler {
        //public void onPartSelected(EagleView view, Part part);
        public void onPartsSelected(EagleView view, List<Part> parts);
        public void onFocusChange(EagleView view, boolean hasFocus);
        public void onSlide(ZoomImageView zoomImageView, boolean up);
    }

    private ValueAnimator animator = null;
    private Matrix currentMatrix;
    private int partMarkerAnimationValue = 0;

    private Paint markerLinePaint = null;
    private Paint markerOverlayPaint = null;
    private Paint focusedBorderPaint = null;
    private Paint rotationMarkerPaint = null;
    private Path rotationMarketPath = null;
    private Paint progressBarPaint = null;

    public void setDrawingMode(LayerManager.DrawingMode drawingMode) {
        this.drawingMode = drawingMode;
        redraw();
    }

    private LayerManager.DrawingMode drawingMode = LayerManager.DrawingMode.DEFAULT;

    private Bitmap b1;

    private float progressBarWidth;

    private Handler handler = null;
    private Rect partMarkerRect = null;
    private int progress = -1;
    private Part selectedPart;
    private List<Part> connectedParts;

    public EagleDataSource.TYPE getType() {
        return type;
    }

    private EagleDataSource.TYPE type;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (progress>-1 && progressBarPaint == null) {
            progressBarPaint = new Paint();
            progressBarPaint.setColor(Color.argb(255, 255, 204, 0));
            progressBarPaint.setStrokeWidth(0);
        } else
        if (progress==-1 && progressBarPaint != null) {
            progressBarPaint = null;
        }
        invalidate();
    }

    public void resetProgress() {
        setProgress(-1);
    }

    /*
    public void selectPartByName(String partName) {
        //dataSource.selectPartByName(partName);
        if (selectedPartRect(currentMatrix)) {
            invalidate();
        }
    }
    */

    public void selectPart(Part part) {
        if (this.selectedPart != part) {
            this.selectedPart = part;
            if (BuildConfiguration.SHOW_CONNECTED_PARTS) {
                if (part != null && this.getDataSource() instanceof BoardDataSource) {
                    connectedParts = ((BoardDataSource) this.getDataSource()).findConnectedParts(part);
                } else {
                    connectedParts = null;
                }
            }
            selectedPartRect(currentMatrix, true);
            if (BuildConfiguration.SHOW_CONNECTED_SIGNALS && this.getDataSource() instanceof BoardDataSource) {
                ((BoardDataSource) this.getDataSource()).selectSignalsByPart(part);
                redraw();
            }
        }
    }

    /*
    zaznacz nowy element, jeśli trzeba to animuj marker, jeśli element jest poza widokiem
    to przesun widok
     */
    private boolean selectedPartRect(Matrix matrix, boolean animate) {

        if (selectedPart != null && dataSource != null && dataSource.getDataReady()) {
            /*
                animacja markera
             */

            final Rect endRect = selectedPart.getTransformedBounds(dataSource.getType(), matrix);
            if (endRect != null && animate) {

                final boolean animateMarker = (partMarkerRect != null);
                boolean tempAnimateView = false;
                final PointF moveViewTo = new PointF();

                Point center = Helpers.midPoint(endRect);
                if (center.x < 0 || center.x > getWidth() || center.y < 0 || center.y > getHeight()) {
                    PointF temp = new PointF(0, 0);

                    if (center.x < 0 || center.x > getWidth()) {
                        float borderOffset = (endRect.width() < getWidth())?
                                ((endRect.width() / 2) + MetricsHelpers.dpToPixels(5)):MetricsHelpers.dpToPixels(20);
                        temp.x = center.x < 0 ? (-center.x + borderOffset) : (-(center.x - getWidth()) - borderOffset);
                    }
                    if (center.y < 0 || center.y > getHeight()) {
                        float borderOffset = (endRect.height() < getHeight())?
                                ((endRect.height() / 2) + MetricsHelpers.dpToPixels(5)):MetricsHelpers.dpToPixels(20);
                        temp.y = center.y < 0 ? (-center.y + borderOffset) : (-(center.y - getHeight()) - borderOffset);
                    }

                    moveViewTo.set(temp);
                    //Log.d("przesuwam 1", String.format("%d %d   %.2f %.2f", center.x, center.y, moveViewTo.x, moveViewTo.y));
                    tempAnimateView = true;
                }
                final boolean animateView = tempAnimateView;

                if (animateView || animateMarker) {
                    final Rect startRect = new Rect(partMarkerRect); // animacja markera
                    final PointF scrolledBy = new PointF(0, 0); // animacja widoku
                    ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    animator.setDuration(200);
                    animator.addListener(new ValueAnimator.AnimatorListener() {

                        @Override public void onAnimationStart(Animator animation) {                        }
                        @Override public void onAnimationCancel(Animator animation) {                        }
                        @Override public void onAnimationRepeat(Animator animation) {                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            partMarkerRect = endRect;
                            if (animateView) {
                                partMarkerRect.offset((int)moveViewTo.x, (int)moveViewTo.y);
                            }
                            EagleView.this.invalidate();
                        }

                    });
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float fraction = animation.getAnimatedFraction();
                            //marker
                            if (animateMarker) {
                                partMarkerRect = new Rect(
                                        (int) (startRect.left + ((float) (endRect.left - startRect.left) * fraction)),
                                        (int) (startRect.top + ((float) (endRect.top - startRect.top) * fraction)),
                                        (int) (startRect.right + ((float) (endRect.right - startRect.right) * fraction)),
                                        (int) (startRect.bottom + ((float) (endRect.bottom - startRect.bottom) * fraction))
                                );
                                /*
                                razem z animowaniem widoku trzeba też przesuwać marker - niezależnie od jego animacji
                                 */
                                if (animateView) {
                                    partMarkerRect.offset((int)moveViewTo.x, (int)moveViewTo.y);
                                }
                            }

                            //widok
                            if (animateView) {

                                EagleView.this.scrollBy(
                                        (moveViewTo.x * fraction) - scrolledBy.x,
                                        (moveViewTo.y * fraction) - scrolledBy.y
                                );
                                scrolledBy.x = (moveViewTo.x * fraction);
                                scrolledBy.y = (moveViewTo.y * fraction);
                            }

                            EagleView.this.invalidate();
                        }
                    });
                    animator.start();
                }

                if (!animateMarker) {
                    partMarkerRect = endRect;
                }
            } else
            if (endRect != null) {
                partMarkerRect = endRect;
            }

            //animator mógł się zatrzymać np po zmianie orientacji urządzenia
            if (!animator.isRunning())
                animator.start();

            return true;

        } else {
            partMarkerRect = null;

            if (animator.isRunning())
                animator.cancel();

            invalidate();
        }
        return false;
    }

    public void setDataSource(EagleDataSource dataSource) {
        this.dataSource = dataSource;

        if (getVisibility() == VISIBLE) {
            zoomToFit();
            redrawLayers(new Matrix());
        }

        //usun pasek postępu
        setProgress(-1);
    }

    public EagleDataSource getDataSource() {
        return dataSource;
    }

    public boolean getDataReady() {
        return (this.dataSource != null) && (this.dataSource.getDataReady());
    }

    private EagleDataSource dataSource;
    private Context context;

    public EagleView(Context context, EagleDataSource dataSource) {
        super(context);
        this.dataSource = dataSource;
        init(context);
    }

    public EagleView(Context context, AttributeSet attrs, int defStyle, EagleDataSource dataSource) {
        super(context, attrs, defStyle);
        this.dataSource = dataSource;
        init(context);
    }

    public EagleView(Context context, AttributeSet attrs, EagleDataSource dataSource) {
        super(context, attrs);
        this.dataSource = dataSource;
        init(context);
    }

    public EagleView(Context context) {
        super(context);
        init(context);
    }

    public EagleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EagleView, EagleDataSource.TYPE.None.ordinal(), 0);
        switch (a.getInt(R.styleable.EagleView_type, 2)) {
            case 0:
                this.type = EagleDataSource.TYPE.Board;
                break;
            case 1:
                this.type = EagleDataSource.TYPE.Schematic;
                break;
            default:
                //err
                break;
        }
        a.recycle();

        init(context);
    }

    @CallSuper
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (handler != null) {
            handler.onFocusChange(EagleView.this, gainFocus);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && !hasFocus()) {
            requestFocus();
            //getParent().requestChildFocus(this, this);
        }
        return super.onTouchEvent(event);
    }

    public void drawConnectedParts(Canvas canvas) {
        if (this.connectedParts != null && this.selectedPart != null) {
            Rect fromRect = this.selectedPart.getTransformedBounds(dataSource.getType(), currentMatrix);
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(1);
            if (fromRect != null) {
                Point fromCenter = Helpers.midPoint(fromRect);
                for (Part part : this.connectedParts) {
                    Rect toRect = part.getTransformedBounds(dataSource.getType(), currentMatrix);
                    if (toRect != null) {

                        Point toCenter = Helpers.midPoint(toRect);
                        canvas.drawLine(fromCenter.x, fromCenter.y, toCenter.x, toCenter.y, paint);

                    }
                }
            }
        }
    }

    public void drawPartMarker(Canvas canvas) {

        if (partMarkerRect != null)// && dataSource != null && dataSource.hasSelectedPart())
        {
            final int x2 = (partMarkerRect.right + partMarkerAnimationValue);
            final int y2 = (partMarkerRect.bottom + partMarkerAnimationValue);
            final int x1 = (partMarkerRect.left - partMarkerAnimationValue);
            final int y1 = (partMarkerRect.top - partMarkerAnimationValue);
            final int size = 10;

            //canvas.drawLine(_20dp, _20dp, canvas.getWidth() - _20dp, canvas.getHeight() - _20dp, markerLinePaint);

            Point boxCenter, screenCenter;

            boxCenter = new Point(
                    Helpers.middle(partMarkerRect.left, partMarkerRect.right),
                    Helpers.middle(partMarkerRect.top, partMarkerRect.bottom)
            );
            screenCenter = new Point(
                    Helpers.middle(0, canvas.getWidth()),
                    Helpers.middle(0, canvas.getHeight())
            );

            Point pS = new Point();

            if (boxCenter.x > screenCenter.x) { pS.x = 0; } else { pS.x = canvas.getWidth() - 1; }
            if (boxCenter.y > screenCenter.y) { pS.y = 0; } else { pS.y = canvas.getHeight() - 1; }

            canvas.drawLine(pS.x, pS.y, boxCenter.x, boxCenter.y, markerLinePaint);

            //paint.setStrokeWidth((1.0f / getMatrix().mapRadius(1.0f)) * width);
            //super.drawLines(pts, paint);
            /*
            canvas.drawLines(new float[]{
                    boxCenter.x, boxCenter.y - 0.25f,
                    boxCenter.x, boxCenter.y + 0.25f,
                    boxCenter.x - 0.25f, boxCenter.y,
                    boxCenter.x + 0.25f, boxCenter.y
            }, markerLinePaint);*/
            //canvas.drawCircle(boxCenter.x, boxCenter.y, MetricsHelpers.dpToPixels(5), markerLinePaint);

/*
            Path p = new Path();
            p.moveTo(x1,y1);
            p.lineTo(x2,y1);
            p.lineTo(x2,y2);
            p.lineTo(x1,y2);
            p.close();
            canvas.drawPath(p, markerOverlayPaint);
*/
            final int size2 = size / 2;
            canvas.drawLines(new float[] {
                    boxCenter.x - size2, boxCenter.y - size2,
                    boxCenter.x + size2, boxCenter.y + size2,
                    boxCenter.x - size2, boxCenter.y + size2,
                    boxCenter.x + size2, boxCenter.y - size2,
                    //lewa góra
                    x1, y1, x1 + size, y1,
                    x1, y1, x1, y1 + size,
                    //prawa góra
                    x2, y1, x2 - size, y1,
                    x2, y1, x2, y1 + size,
                    //lewy doł
                    x1, y2, x1 + size, y2,
                    x1, y2, x1, y2 - size,
                    //prawy doł
                    x2, y2, x2 - size, y2,
                    x2, y2, x2, y2 - size
            }, markerLinePaint);
/*
            //lewa góra
            canvas.drawLine(x1, y1, x1 + size, y1, markerLinePaint);
            canvas.drawLine(x1, y1, x1, y1 + size, markerLinePaint);

            //prawa góra
            canvas.drawLine(x2, y1, x2 - size, y1, markerLinePaint);
            canvas.drawLine(x2, y1, x2, y1 + size, markerLinePaint);

            //lewy doł
            canvas.drawLine(x1, y2, x1 + size, y2, markerLinePaint);
            canvas.drawLine(x1, y2, x1, y2 - size, markerLinePaint);

            //prawy doł
            canvas.drawLine(x2, y2, x2 - size, y2, markerLinePaint);
            canvas.drawLine(x2, y2, x2, y2 - size, markerLinePaint);*/
        }
    }

    private void drawFocus(Canvas canvas) {
        if (this.hasFocus()) {
            canvas.drawLine(0, 0, getWidth(), 0, focusedBorderPaint);
            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), focusedBorderPaint);
            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), focusedBorderPaint);
            canvas.drawLine(0, 0, 0, getHeight(), focusedBorderPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        if (progress != -1 && progressBarPaint != null) {
            canvas.drawRect(
                    0, getHeight() - progressBarWidth,
                    getWidth() * ((float)progress/100.00f), getHeight(),
                    progressBarPaint
            );
        }
    }

    private void drawRotationMarker(Canvas canvas) {
        float angle = getAngle();
        if (angle != 0) {
            if (Math.abs(angle) > 20)
                angle = 20 * (angle < 0 ? -1 : 1);

            //if (Math.abs(angle) > 0)
            {
                angle = (float) Math.sin(angle / 3.14f / 4.0f) * 10.0f;

                canvas.save();
                canvas.rotate(
                        angle,
                        getWidth() / 2,
                        getHeight() / 2
                );

                rotationMarketPath.reset();

                rotationMarketPath.moveTo(-200, -200);
                rotationMarketPath.lineTo(getWidth() + 200, -200);
                rotationMarketPath.lineTo(getWidth() + 200, getHeight() + 200);
                rotationMarketPath.lineTo(-200, getHeight() + 200);
                rotationMarketPath.close();

                rotationMarketPath.moveTo(0, 0);
                rotationMarketPath.lineTo(getWidth(), 0);
                rotationMarketPath.lineTo(getWidth(), getHeight());
                rotationMarketPath.lineTo(0, getHeight());
                rotationMarketPath.close();

                rotationMarkerPaint.setColor(Color.argb((int) (Math.abs(angle) * 15), 160, 160, 160));
                canvas.drawPath(rotationMarketPath, rotationMarkerPaint);

                canvas.restore();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //getContext().getDrawable(R.drawable.board_big).draw(canvas);
        if (getDataSource() == null) {
            canvas.drawBitmap(b1, (getWidth() / 2.0f) - (b1.getWidth() / 2.0f), (getHeight() / 2.0f) - (b1.getHeight() / 2.0f), markerLinePaint);
        }
        //getResources().getDrawable();

        drawFocus(canvas);
        drawPartMarker(canvas);
        if (BuildConfiguration.SHOW_CONNECTED_PARTS)
            drawConnectedParts(canvas);
        drawRotationMarker(canvas);
        drawProgress(canvas);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d("ev", "save");
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());

        bundle.putParcelable("dataSource", this.dataSource);
        bundle.putParcelable("zoomImageView", super.getParcel());

        Helpers.matrixToBundle(currentMatrix, bundle, "currentMatrix");

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            this.dataSource = bundle.getParcelable("dataSource");

            currentMatrix = Helpers.matrixFromBundle(bundle, "currentMatrix");
            super.restoreParcel((Bundle)bundle.getParcelable("zoomImageView"));

            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
   }

   /*
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (!isInEditMode()) {
            if (visibility == VISIBLE) {
                redrawLayers(new Matrix()); ///a
            }
        }
    }
    */

    private void init(final Context context) {

        /*
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyCustomView, defStyle, 0);

        String str = a.getString(R.styleable.MyCustomView_my_custom_attribute);

        //do something with str

        a.recycle();
        */

        if (animator == null) {
            animator = (ValueAnimator)AnimatorInflater.loadAnimator(context, R.animator.mark_element_view_animation2);
            /*
            animator = ValueAnimator.ofInt(0, 10);
            animator.setDuration(500);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            */
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    partMarkerAnimationValue = (int)animation.getAnimatedValue();
                    EagleView.this.invalidate();
                }
            });
        }

        currentMatrix = new Matrix();
        this.context = context;

        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        this.setZoomHandler(new ZoomHandler() {
            @Override
            public void onZoomFinished(ZoomImageView zoomImageView, Matrix matrix, boolean fullUpdate) {
                if (fullUpdate) {
                    redrawLayers(matrix);
                } else {

                    Matrix m = new Matrix();
                    m.set(currentMatrix);
                    m.postConcat(matrix);

                    //przesunać zaznaczenie elementu
                    if (selectedPartRect(m, false)) {
                        invalidate();
                    }

                    //ukryj marker
                    //MarkElementView iv = (MarkElementView)WorkspaceFragment.this.getActivity().findViewById(R.id.view2);
                    //iv.setVisibility(View.INVISIBLE);

                    //przywróc wersje bitmapy bez linii/strzałki
                    //zoomImageView.setImageBitmap(tempBitmap, false);
                }
            }

            @Override
            public void onLongTouch(ZoomImageView zoomImageView, Matrix matrix, PointF point) {

                if (dataSource == null)
                    return;
                final PartsManager partsManager = dataSource.getPartsManager();
                if (partsManager == null)
                    return;

                Matrix m = new Matrix();
                m.set(currentMatrix);
                m.postConcat(matrix);

                m.invert(m);

                float[] points = {
                      point.x,
                      point.y
                };
                m.mapPoints(points);
                point.set(
                    points[0],
                    points[1]
                );

                if (dataSource != null) {
                    final ArrayList<Part> parts = dataSource.findNearest(
                            point
                    );
                    if (handler != null)
                        handler.onPartsSelected(EagleView.this, parts);
/*
                    if (parts.size() == 1) {

                        Part p = partsManager.select(parts.get(0));
                        selectPart(p);
                        if (handler != null)
                            handler.onPartSelected(EagleView.this, p);

                    } else
                    if (parts.size() > 1) {

                        if (BuildConfiguration.AMBIGUOUS_SELECTION_MODE == BuildConfiguration.AMBIGUOUS_SELECTION_MODES.DIALOG) {
                            //tryb DIALOG
                            final CustomDialog dialog = (CustomDialog) new CustomDialog(getContext());
                            dialog.setView(R.layout.dialog_choice)
                                    .setTopColorRes(R.color.colorDialogTitleBar)
                                    .setIcon(R.drawable.changebrdlight)
                                    .setTopTitle(R.string.select_part)
                                    .configureView(new LovelyCustomDialog.ViewConfigurator() {
                                        @Override
                                        public void configureView(View v) {

                                            class PartsArrayAdapter extends BaseAdapter {

                                                public PartsArrayAdapter() {
                                                }

                                                @Override
                                                public int getCount() {
                                                    return parts.size();
                                                }

                                                @Override
                                                public Object getItem(int position) {
                                                    return parts.get(position);
                                                }

                                                @Override
                                                public long getItemId(int position) {
                                                    return position;
                                                }

                                                @Override
                                                public View getView(int position, View convertView, ViewGroup parent) {
                                                    View view;
                                                    if (convertView != null) {
                                                        view = convertView;
                                                    } else {
                                                        LayoutInflater inflater = LayoutInflater.from(EagleView.this.getContext());
                                                        view = inflater.inflate(android.R.layout.simple_list_item_1, null);
                                                    }
                                                    ((TextView) view.findViewById(android.R.id.text1)).setText(parts.get(position).getName());
                                                    return view;
                                                }

                                            }

                                            PartsArrayAdapter adapter = new PartsArrayAdapter();

                                            ListView list = (ListView) v.findViewById(R.id.list);

                                            list.setAdapter(adapter);
                                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                    dialog.dismiss();

                                                    Part p = partsManager.select(parts.get(position));
                                                    selectPart(p);
                                                    if (handler != null)
                                                        handler.onPartSelected(EagleView.this, p);

                                                }
                                            });

                                        }
                                    });
                            dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        } else
                        if (BuildConfiguration.AMBIGUOUS_SELECTION_MODE == BuildConfiguration.AMBIGUOUS_SELECTION_MODES.SWIPABLE_PICKER) {
                            //tryb listy przesuwanej
                            handler.
                            this.g
                            selectPartCarousel
                        }
*/

                        /*
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        AlertDialog.Builder title = builder.setTitle("Wybierz element");

                        ArrayAdapter<String> array = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice);
                        array.addAll(list);

                        title.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Part p = partsManager.selectByName(list.get(which));
                                selectPart(p);
                                if (handler != null)
                                    handler.OnPartSelected(EagleView.this, p);
                            }
                        });
                        builder.create().show();

                    }*/

                }
            }

            @Override
            public void onSlide(ZoomImageView zoomImageView, boolean up) {
                if (handler != null)
                    handler.onSlide(EagleView.this, up);
            }
        });

        markerLinePaint = new Paint();
        markerLinePaint.setStrokeWidth(
                MetricsHelpers.dpToPixels(2)
        );
        markerLinePaint.setColor(Color.argb(200,255,0,0));

        markerOverlayPaint = new Paint();
        markerOverlayPaint.setStrokeWidth(
                MetricsHelpers.dpToPixels(2)
        );
        markerOverlayPaint.setColor(Color.argb(100, 100, 100, 100));//Color.RED);

        focusedBorderPaint = new Paint();
        focusedBorderPaint.setColor(Color.LTGRAY);
        focusedBorderPaint.setStrokeWidth(MetricsHelpers.dpToPixels(3));

        rotationMarkerPaint = new Paint();
        rotationMarkerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        rotationMarketPath = new Path();
        rotationMarketPath.setFillType(Path.FillType.EVEN_ODD);

        progressBarWidth = MetricsHelpers.dpToPixels(5);

        Resources res = getResources();
        switch (this.type) {
            case Board:
                b1 = BitmapFactory.decodeResource(res, R.drawable.board_big);
                break;
            case Schematic:
                b1 = BitmapFactory.decodeResource(res, R.drawable.schematic_big);
                break;
        }
        Log.d("bitmapa", String.format("%d %d", b1.getWidth(), b1.getHeight()));
        //b1.getWidth()
    }

    public void zoomToFit() {
        RectF dimensions = this.dataSource.getDimensions();
        //Log.d("dimm", dimensions.toString());
        if (dimensions != null) {
            //initMatrix = new Matrix(); //MetricsHelpers.getInitMatrix(getActivity(), dimensions);
            Matrix initMatrix = Helpers.getInitMatrix(
                    new RectF(0, 0, this.getWidth(), this.getHeight()),
                    dimensions
            );
            currentMatrix = new Matrix();
            currentMatrix.postScale(1, -1);
            if (this.dataSource.isFlipped()) {
                currentMatrix.postScale(-1, 1, dimensions.width() / 2.0f, 0);
            }
            redrawLayers(initMatrix);
            selectedPartRect(currentMatrix, true);
        }
    }

    public void redraw() {
        redrawLayers(null);
    }

    @UiThread
    public void setBitmap(Bitmap bitmap) {
        this.setImageBitmap(bitmap, true);
    }

    @Background
    public void backgroundRedrawLayers(Bitmap bitmap, ExtendedCanvas c, LayerManager layers, LayerManager.DrawingMode drawingMode) {
        layers.draw(c, drawingMode);
        this.setBitmap(bitmap);
    }

    private void redrawLayers(Matrix matrix) {
        if (dataSource != null && this.getHeight() > 0 && this.getWidth() > 0) {
            //    this.layoutEmpty.setVisibility(8);
            long start = System.currentTimeMillis();
            ExtendedCanvas c = new ExtendedCanvas();

            //co to do cholery jest ??
            if (matrix != null) {
                Matrix tempZoomMatrix = new Matrix(currentMatrix);
                tempZoomMatrix.postConcat(matrix);
                if (tempZoomMatrix.mapRadius(1.0f) < 250.0f) {
                    currentMatrix.postConcat(matrix);
                }
            } else {
                //macież się nie zmieniła
                //robimy po prostu odrysowanie
            }

            c.setMatrix(currentMatrix);
            // skalowanie -1 w pionie przniesione w inne miejsce
            // c.scale(1.0f, -1.0f);
            Bitmap tempBitmap = createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
            c.setBitmap(tempBitmap);

            this.backgroundRedrawLayers(tempBitmap, c, this.dataSource.getLayers(), drawingMode);
            //this.dataSource.getLayers().draw(c, drawingMode);
    /*
            if (selectedPart != null) {
                selectPart(selectedPart);
            } else {
            }
    */
            //this.setImageBitmap(tempBitmap, true);

            /*
            //Log.d("Draw", "draw time:" + (System.currentTimeMillis() - start) + "ms");
            }
            setImageBitmap(createBitmap(1, 1, Config.ARGB_8888), true);
            setBackgroundColor(Color.BLUE);
            //this.layoutEmpty.setVisibility(0);
            */
        }
    }

    /*
    odwróc widok płytki
     */
    public void flipView() {
        if (dataSource == null || !dataSource.getDataReady())
            return;

        final RectF dimensions = this.dataSource.getDimensions();
        final float p[] = {
            dimensions.width() / 2.0f,
            dimensions.height() / 2.0f
        };
        final float p2[] = new float[2];
        currentMatrix.mapPoints(p2, p);

        final Camera camera = new Camera();
        final Matrix matrix = new Matrix(currentMatrix);

        final ValueAnimator v = ValueAnimator.ofInt(0, 179);
        v.setDuration(600);
        v.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //mapuj środek płytki wg nowej matrycy
                currentMatrix.mapPoints(p2, p);
                currentMatrix.postScale(-1, 1, p2[0], p2[1]);
                EagleView.this.setMatrix(currentMatrix);
                //przerysuj ponownie
                redrawLayers(null);

                //i przenieś zaznaczenie elementu
                selectedPartRect(currentMatrix, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private boolean flipped = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int degrees = (int)animation.getAnimatedValue();
                //Log.d("animation", String.format("%d", degrees));

                if (degrees >= 89 && !flipped) {
                    long playTime = v.getCurrentPlayTime();
                    flipped = true;
                    EagleView.this.dataSource.flipView();
                    redrawLayers(null);
                    //w momencie redraw matryca jest resetowana? więc naszą zapamiętana matryce
                    //też zresetujmy
                    matrix.reset();
                    v.setCurrentPlayTime(playTime);
                }

                camera.save();
                camera.rotateY(degrees);
                camera.rotateX(degrees*2);
                camera.getMatrix(matrix);
                camera.restore();

                //przesun środek do 0,0
                matrix.preTranslate(-p2[0], -p2[1]);
                matrix.postTranslate(p2[0], p2[1]);

                EagleView.this.setImageMatrix(matrix);
            }
        });
        v.start();
    }

}
