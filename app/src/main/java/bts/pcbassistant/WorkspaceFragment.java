package bts.pcbassistant;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import bts.pcbassistant.data.EagleDataSource;
import bts.pcbassistant.data.LayerManager;
import bts.pcbassistant.data.Part;
import bts.pcbassistant.data.PartsManager;
import bts.pcbassistant.dialog.CustomDialog;
import bts.pcbassistant.drawing.Layer;
import bts.pcbassistant.drawing.view.EagleView;
import bts.pcbassistant.drawing.view.ZoomImageView;
import bts.pcbassistant.layers.LayerAdapter;
import bts.pcbassistant.parsing.AssetInputStreamWithCallbacksHandler;
import bts.pcbassistant.search.SearchActivity;
import bts.pcbassistant.utils.AnimationEndListener;
import bts.pcbassistant.utils.CarouselPicker;
import bts.pcbassistant.utils.DropboxManager;
import bts.pcbassistant.utils.FilenameHelpers;
import bts.pcbassistant.welcome.RecentlyOpened;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

@EFragment(R.layout.fragment_workspace) // Sets content view to R.layout.translate
public class WorkspaceFragment extends Fragment { // ZMIENIONE

    private static final int OPEN_BOARD = 1;
    private static final int SEARCH = 5;
    private static final int REQUEST_OPEN_FILE = 3;
    private static final String SAVE_DIMENSIONS = "save:dimensions";
    private static final String SAVE_IS_BRD = "save:is_brd";
    private static final String SAVE_OPENED_FILENAME = "save:open_fileneme";
    private static final String SAVE_OPENED_PATH = "save:open_path";
    private static boolean isBrd;
    private static String openedFileName;
    private static String openedPath;
    private MenuItem menuZoomToFit;
    private MenuItem menuLayers;
    private MenuItem menuSearch;
    private MenuItem menuFlip;
    private MenuItem menuBlindSideMode;
    private MenuItem menuDeselect;
    private String projectName;

    @ViewById(R.id.eagle_view1)
    EagleView eagleView1;

    @ViewById(R.id.eagle_view2)
    EagleView eagleView2;

    EagleView activeEagleView = null;

    @ViewById(R.id.select_part_carousel)
    CarouselPicker selectPartCarousel;

    PartsManager partsManager = null;

    enum Layouts {
        DUAL,
        SCHEMATIC,
        BOARD
    }

    Layouts currentLayout = Layouts.DUAL;

    LayerManager.DrawingMode boardDrawingMode = LayerManager.DrawingMode.DEFAULT;

    private AtomicInteger loadingCounter;

    /* czy trwa ładowania danych? */
    public boolean loadingInProgress() {
        return loadingCounter.get() > 0;
    }

    //DODANE

    @UiThread
    public void setDataSource(EagleView view, EagleDataSource ds)
    {
        view.setDataSource(ds);
        menuDeselect.setVisible(false);
    }

    @UiThread
    public void setLoadingProgress(EagleView view, int progress) {
        view.setProgress(progress);
    }

    @Background
    public void loadBoardFile(String file) {

        loadingCounter.incrementAndGet();

        EagleDataSource ds = EagleDataSource.createDataSource(
                file,
                WorkspaceFragment.this.getActivity(),
                partsManager,
                new AssetInputStreamWithCallbacksHandler() {
                    @Override
                    public void onProgress(EagleDataSource dataSource, int progress) {
                        setLoadingProgress(eagleView2, progress);
                        if (progress == 100) {
                            updateMenus();
                        }
                    }
                }
        );
        if (ds != null) {
            setDataSource(eagleView2, ds);
            loadingCounter.decrementAndGet();
        }

    }

    @Background
    public void loadSchematicFile(String file) {

        loadingCounter.incrementAndGet();

        EagleDataSource ds = EagleDataSource.createDataSource(
                file,
                WorkspaceFragment.this.getActivity(),
                partsManager,
                new AssetInputStreamWithCallbacksHandler() {
                    @Override
                    public void onProgress(EagleDataSource dataSource, int progress) {
                        setLoadingProgress(eagleView1, progress);
                        if (progress == 100)
                            updateMenus();
                    }
                }
        );
        if (ds != null) {
            setDataSource(eagleView1, ds);
            loadingCounter.decrementAndGet();
        }

    }

    public void openProject(String filePath, boolean board, boolean schematic) {
        setNewProject(filePath);
        if (board)
            loadBoardFile(filePath+".brd");
        if (schematic)
            loadSchematicFile(filePath+".sch");

        String[] str = FilenameHelpers.stripExtension(filePath).split(":");
        RecentlyOpened.RecentlyOpenedItem recentlyOpened = RecentlyOpened.add(str[1], str[0]);
        if (board)
            recentlyOpened.update(EagleDataSource.TYPE.Board);
        if (schematic)
            recentlyOpened.update(EagleDataSource.TYPE.Schematic);
    }

    /*
        pokaż lub ukryj odpowiednie menu na górze
     */
    @UiThread
    protected void updateMenus() {
        if (WorkspaceFragment.this.menuLayers != null) {

            boolean dataReady = (activeEagleView != null && activeEagleView.getDataReady());
            WorkspaceFragment.this.menuLayers.setVisible(dataReady);
            WorkspaceFragment.this.menuZoomToFit.setVisible(dataReady);
            WorkspaceFragment.this.menuSearch.setVisible(
                    eagleView1.getDataReady() ||
                    eagleView2.getDataReady()
            );
            //tylko płytkę można odwracać
            WorkspaceFragment.this.menuFlip.setVisible(
                    dataReady && (activeEagleView.getType() == EagleDataSource.TYPE.Board)
            );
            WorkspaceFragment.this.menuBlindSideMode.setVisible(
                    dataReady && (activeEagleView.getType() == EagleDataSource.TYPE.Board)
            );
        }
    }

    private void handleOnPartsSelected(EagleView view, List<Part> parts) {

        if (parts.size() == 1) {

            this.selectPart(partsManager.select(parts.get(0)));
            getView()
                    .findViewById(R.id.ambiguous_part_bar)
                    .setVisibility(View.GONE);
            selectPartCarousel.setAdapter(null);

        } else
        if (parts.size() > 1) {

            final List<Part> finalParts = new ArrayList<Part>(parts);

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
                                        return finalParts.size();
                                    }

                                    @Override
                                    public Object getItem(int position) {
                                        return finalParts.get(position);
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
                                            LayoutInflater inflater = LayoutInflater.from(WorkspaceFragment.this.getContext());
                                            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
                                        }
                                        ((TextView) view.findViewById(android.R.id.text1)).setText(finalParts.get(position).getName());
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
                                        WorkspaceFragment.this.selectPart(partsManager.select(finalParts.get(position)));

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
                //tryb DIALOG
            } else if (BuildConfiguration.AMBIGUOUS_SELECTION_MODE == BuildConfiguration.AMBIGUOUS_SELECTION_MODES.SWIPABLE_PICKER) {
                //tryb listy przesuwanej

                //domyślnie zaznacz pierwszy
                this.selectPart(partsManager.select(parts.get(0)));

                final List<CarouselPicker.PickerItem> textItems = new ArrayList<>();
                for (Part part : parts) {
                    //android.text.Spanned text = Html.fromHtml(String.format("%s <font color='#AAAAAA'>(%s)</font>", part.getName(), part.getLibrary()), Html.FROM_HTML_MODE_LEGACY);
                    //textItems.add(new CarouselPicker.DrawableItem(String.format("%s <font color='#AAAAAA'>(%s)</font>", part.getName(), part.getLibrary()), 10));
                }

                class AmbiguousViewAdapter extends CarouselPicker.CarouselViewAdapter {
                    public AmbiguousViewAdapter(Context context, List<CarouselPicker.PickerItem> items, int drawable) {
                        super(context, items, drawable);
                    }

                    @Override
                    public int getCount() {
                        return finalParts.size();
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {

                            View view = LayoutInflater.from(WorkspaceFragment.this.getContext()).inflate(R.layout.list_item_ambiguous_item_carousel, null);
                            TextView text = (TextView)view.findViewById(R.id.text);

                            Part part = finalParts.get(position);
                            if (Build.VERSION.SDK_INT < 24) {
                                text.setText(Html.fromHtml(String.format("%s <font color='#000000'>(%s)</font>", part.getName(), part.getLibrary())));
                            } else {
                                text.setText(Html.fromHtml(String.format("%s <font color='#000000'>(%s)</font>", part.getName(), part.getLibrary()), Html.FROM_HTML_MODE_LEGACY));
                            }

                            container.addView(view);
                            return view;

                    }
                }

                final CarouselPicker.CarouselViewAdapter textAdapter = new AmbiguousViewAdapter(this.getContext(), textItems, R.layout.list_item_ambiguous_item_carousel);
                selectPartCarousel.setAdapter(textAdapter);
                selectPartCarousel.clearOnPageChangeListeners();
                selectPartCarousel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
                    @Override public void onPageScrollStateChanged(int state) { }

                    @Override
                    public void onPageSelected(int position) {
                        WorkspaceFragment.this.selectPart(finalParts.get(position));
                    }

                });
                getView().findViewById(R.id.select_ambiguous_bar_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getView().findViewById(R.id.ambiguous_part_bar).setVisibility(View.GONE);
                        selectPartCarousel.setAdapter(null);
                        selectPartCarousel.clearOnPageChangeListeners();
                    }
                });
                getView().findViewById(R.id.ambiguous_part_bar).setVisibility(View.VISIBLE);
                //tryb listy przesuwanej
            }
        }

    }

    private void selectPart(Part part) {
        if (part != null) {
            eagleView1.selectPart(part);
            eagleView2.selectPart(part);
            menuDeselect.setVisible(true);
        }
    }

    private void deselectPart() {
        eagleView1.selectPart(null);
        eagleView2.selectPart(null);
        //beznadziejne rozwiązanie problemu NullPointer kiedy wczytujemy projekt
        //w momencie otwarcia aktywności
        if (menuDeselect != null)
            menuDeselect.setVisible(false);
        getView()
                .findViewById(R.id.ambiguous_part_bar)
                .setVisibility(View.GONE);
        selectPartCarousel.setAdapter(null);
    }

    @AfterViews
    public void init() {

        loadingCounter = new AtomicInteger(0);

        eagleView1.setHandler(new EagleView.Handler() {

            @Override
            public void onPartsSelected(EagleView view, List<Part> parts) {
                WorkspaceFragment.this.handleOnPartsSelected(view, parts);
            }

            /*
            @Override
            public void onPartSelected(EagleView view, Part part) {
                WorkspaceFragment.this.eagleView2.selectPart(part);
                Toast.makeText(
                        WorkspaceFragment.this.getActivity(),
                        "Part: "+part.getName(),
                        Toast.LENGTH_SHORT
                ).show();
                menuDeselect.setVisible(true);
            }
*/
            @Override
            public void onFocusChange(EagleView view, boolean hasFocus) {
                if (hasFocus) {
                    activeEagleView = view;
                } else if (activeEagleView == view)
                    activeEagleView = null;
                updateMenus();
            }

            @Override
            public void onSlide(ZoomImageView zoomImageView, boolean up) {
                layoutChange(up);
            }
        });

        eagleView2.setHandler(new EagleView.Handler() {

            @Override
            public void onPartsSelected(EagleView view, List<Part> parts) {
                WorkspaceFragment.this.handleOnPartsSelected(view, parts);
            }

            /*
            @Override
            public void onPartSelected(EagleView view,Part part) {
                WorkspaceFragment.this.eagleView1.selectPart(part);
                Toast.makeText(
                        WorkspaceFragment.this.getActivity(),
                        "Part: "+part.getName(),
                        Toast.LENGTH_SHORT
                ).show();
                menuDeselect.setVisible(true);
            }
            */

            @Override
            public void onFocusChange(EagleView view, boolean hasFocus) {
                if (hasFocus) {
                    activeEagleView = view;
                } else if (activeEagleView == view)
                    activeEagleView = null;
                if (WorkspaceFragment.this.menuLayers != null) {
                    WorkspaceFragment.this.menuLayers.setVisible(activeEagleView != null);
                    WorkspaceFragment.this.menuZoomToFit.setVisible(activeEagleView != null);
                }
            }

            @Override
            public void onSlide(ZoomImageView zoomImageView, boolean up) {
                layoutChange(up);
            }
        });

        currentLayout = Layouts.valueOf(this.getContext().getSharedPreferences(
                "52429e7f2938393d05c9de9c062615ec8f1b780caef4b4907789dc11c0b17f78", Context.MODE_PRIVATE).getString("workspaceLayout", Layouts.DUAL.toString())
        );
        applyLayout(false);
        boardDrawingMode = LayerManager.DrawingMode.valueOf(this.getContext().getSharedPreferences(
                "52429e7f2938393d05c9de9c062615ec8f1b780caef4b4907789dc11c0b17f78", Context.MODE_PRIVATE).getString("boardDrawingMode", LayerManager.DrawingMode.DEFAULT.toString())
        );
        applyBoardDrawingMode(boardDrawingMode);
    }

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //setRetainInstance(true);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            partsManager = savedInstanceState.getParcelable("partsManager");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putBoolean(SAVE_IS_BRD, isBrd);
        //outState.putString(SAVE_OPENED_FILENAME, openedFileName);
        //outState.putString(SAVE_OPENED_PATH, openedPath);
        outState.putParcelable("partsManager", partsManager);
    }

    public WorkspaceFragment() {
        //this.size = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        this.menuLayers = null;
        this.menuSearch = null;
        this.menuZoomToFit = null;
        this.menuFlip = null;
        this.menuBlindSideMode = null;
        this.menuDeselect = null;
    }

    static {
        isBrd = true;
        openedPath = null;
        openedFileName = null;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        this.menuLayers = menu.findItem(R.id.action_layers);
        this.menuSearch = menu.findItem(R.id.action_search);
        this.menuZoomToFit = menu.findItem(R.id.action_zoom_to_fit);
        this.menuFlip = menu.findItem(R.id.action_flip);
        this.menuBlindSideMode = menu.findItem(R.id.action_blind_side_mode);
        this.menuDeselect = menu.findItem(R.id.action_deselect);

        //setBackground();
    }

    //zmien layout na kolejny
    private void layoutChange(boolean up)
    {
        if (up) {
            switch (currentLayout) {
                case SCHEMATIC: currentLayout = Layouts.DUAL; break;
                case DUAL: currentLayout = Layouts.BOARD; break;
            }
        } else {
            switch (currentLayout) {
                case BOARD: currentLayout = Layouts.DUAL; break;
                case DUAL: currentLayout = Layouts.SCHEMATIC; break;
            }
        }
        applyLayout(true);
    }

    private void applyBoardDrawingMode(LayerManager.DrawingMode mode)
    {
        boardDrawingMode = mode;
        eagleView2.setDrawingMode(boardDrawingMode); // płytka
        //zapisz aktualny tryb rysowanie
        this.getContext().getSharedPreferences(
                "52429e7f2938393d05c9de9c062615ec8f1b780caef4b4907789dc11c0b17f78", Context.MODE_PRIVATE
        ).edit().putString("boardDrawingMode", boardDrawingMode.toString()).commit();
    }

    private void __applyLayout(float val)
    {
        LinearLayout.LayoutParams p;

        p = (LinearLayout.LayoutParams) eagleView1.getLayoutParams();
        p.weight = val;
        eagleView1.setLayoutParams(p);

        p = (LinearLayout.LayoutParams) eagleView2.getLayoutParams();
        p.weight = 2 - val;
        eagleView2.setLayoutParams(p);
    }

    /*
    ustaw właściwy układ
     */
    private boolean applyLayoutZoomAfterAnimation = false;

    private void applyLayout(boolean animate)
    {
        ValueAnimator animator = null;
        final float currentWeight = ((LinearLayout.LayoutParams)eagleView1.getLayoutParams()).weight;
        float toWeight = 0;
        switch (currentLayout) {
            case SCHEMATIC:
                if (currentWeight == 0.0f) return;
                toWeight = 0.0f;
                break;
            case BOARD:
                if (currentWeight == 2f - 0.0f) return;
                toWeight = 2.0f;
                break;
            case DUAL:
                if (currentWeight == 1.0f) return;
                toWeight = 1.0f;
                break;
        }

        //zapisz aktualny układ
        this.getContext().getSharedPreferences(
                "52429e7f2938393d05c9de9c062615ec8f1b780caef4b4907789dc11c0b17f78", Context.MODE_PRIVATE
        ).edit().putString("workspaceLayout", currentLayout.toString()).commit();

        if (animate) {
            animator = ValueAnimator.ofFloat(currentWeight, toWeight);
            animator.setDuration(200);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setRepeatCount(0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    WorkspaceFragment.this.__applyLayout((float)animation.getAnimatedValue());
                }
            });
            final float toWeight2 = toWeight;
            animator.addListener(new AnimationEndListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //w tym momencie layout to DUAL a currentWeight = 0.0f lub 2.0f
                    if (applyLayoutZoomAfterAnimation) {
                        //current to początkowy rozmiar
                        if (currentWeight == 2.0f)
                            eagleView1.zoomToFit(); else
                        if (currentWeight == 0.0f)
                            eagleView2.zoomToFit();
                        applyLayoutZoomAfterAnimation = false;
                    }
                }
            });
            animator.start();
        } else {
            this.__applyLayout(toWeight);
            if (currentLayout != Layouts.DUAL) {
                //schemat lub płytka jest ukryta na starcie, po odkryciu
                //zrób zoom
                applyLayoutZoomAfterAnimation = true;
            }
        }
        /*
        animator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {     }

            @Override
            public void onAnimationEnd(Animator animation) {
                //eagleView1.redraw();
                //eagleView2.redraw();
            }

            @Override public void onAnimationCancel(Animator animation) {    }
            @Override public void onAnimationRepeat(Animator animation) {    }
        });*/
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //
            case R.id.action_open_file_internal_browser:
                WorkspaceFragment.this.startActivityForResult(
                        IntentFactory.createOpenFileIntent(WorkspaceFragment.this.getActivity(), "", "file"),
                        WorkspaceFragment.REQUEST_OPEN_FILE
                );
                break;
            case R.id.action_open_file_dropbox:
                WorkspaceFragment.this.startActivityForResult(
                        IntentFactory.createOpenFileIntent(WorkspaceFragment.this.getActivity(), "", "dropbox"),
                        WorkspaceFragment.REQUEST_OPEN_FILE
                );
                break;
            case R.id.action_open_file_dropbox_new_session:
                WorkspaceFragment.this.startActivityForResult(
                        IntentFactory.createOpenFileIntent(WorkspaceFragment.this.getActivity(), "", "dropbox_new_sessions"),
                        WorkspaceFragment.REQUEST_OPEN_FILE
                );
                break;
            //
            case R.id.action_layout_schematic:
            case R.id.action_layout_board:
            case R.id.action_layout_dual:
                switch (item.getItemId()) {
                    case R.id.action_layout_dual:
                        currentLayout = Layouts.DUAL; break;
                    case R.id.action_layout_board:
                        currentLayout = Layouts.BOARD; break;
                    case R.id.action_layout_schematic:
                        currentLayout = Layouts.SCHEMATIC; break;
                }
                applyLayout(true);
                break;
            case R.id.action_blind_side_default:
            case R.id.action_blind_side_hidden:
            case R.id.action_blind_side_transparent:
                switch (item.getItemId()) {
                    case R.id.action_blind_side_default:
                        applyBoardDrawingMode(LayerManager.DrawingMode.DEFAULT); break;
                    case R.id.action_blind_side_hidden:
                        applyBoardDrawingMode(LayerManager.DrawingMode.BLIND_SIDE_HIDDEN); break;
                    case R.id.action_blind_side_transparent:
                        applyBoardDrawingMode(LayerManager.DrawingMode.BLIND_SIDE_TRANSPARENT); break;
                }
                break;
            case R.id.action_change_layout:
                /*
                if (!isValidFileOpened()) {
                    makeText(getActivity(), getResources().getString(R.string.noFileOpened), OPEN_BOARD).show();
                    break;
                }
                //String basePath = openedPath;//.substring(0, openedPath.lastIndexOf("."));
                String baseFileName = openedFileName.substring(0, openedFileName.lastIndexOf("."));
                if (!isBrd) {
                    openFile(baseFileName + ".brd", openedPath); //basePath + ".brd");
                    break;
                }
                openFile(baseFileName + ".sch", openedPath); //basePath + ".sch");
                */
                break;
            case R.id.action_open:
                openFileDialog();
//                loadFile(null);
/*
                try {
                    openDemoFile();
                    break;
                } catch (IOException e) {
                    break;
                }*/
                break;
            case R.id.action_layers:
                if (activeEagleView == null) {
                    makeText(getActivity(), getResources().getString(R.string.no_active_view), LENGTH_LONG).show();
                    break;
                }
                setLayers();
                break;
            case R.id.action_flip:
                if (activeEagleView == null) {
                    makeText(getActivity(), getResources().getString(R.string.no_active_view), LENGTH_LONG).show();
                    break;
                } else
                    activeEagleView.flipView();
                break;
            case R.id.action_zoom_to_fit:
                if (activeEagleView == null) {
                    makeText(getActivity(), getResources().getString(R.string.no_active_view), LENGTH_LONG).show();
                    break;
                }
                zoomToFit();
                break;
            case R.id.action_hide_toolbar:
                ((WorkspaceActivity_)this.getActivity()).getSupportActionBar().hide();
                break;
            case R.id.action_deselect:
                this.deselectPart();
                break;
            case R.id.action_search:

                /*if (activeEagleView != null)
                {*/
                    /*
                    if (activeEagleView.getDataSource().getParts().size() == 0) {
                        makeText(getActivity(), "Brak elementów", OPEN_BOARD).show();
                        break;
                    }
                    */
                    if (partsManager != null) {
                        SearchActivity.partsManager = partsManager;
                        WorkspaceFragment.this.startActivityForResult(
                                IntentFactory.searchIntent(
                                        WorkspaceFragment.this.getActivity()
                                ), WorkspaceFragment.SEARCH
                        );
                    }
                /*}

                 */

//                Intent intent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
//                startActivity(intent);
//                searchForPart();
                break;
            case R.id.action_cancel:
                this.getActivity().finish();
                System.exit(0);
                break;

            case R.id.action_welcome_screen:
                this.getActivity().finish();
                //((StartActivity)this.getActivity()).openWelcomeScreen();
                break;
        }
        if (item.getItemId() == R.id.action_zoom_to_fit) {
            zoomToFit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openFileDialog() {

        //saveStateHandler = new LovelySaveStateHandler();
        final CustomDialog dialog = (CustomDialog)new CustomDialog(this.getContext());
        dialog.setView(R.layout.dialog_choice)
              .setTopColorRes(R.color.colorDialogTitleBar)
              .setTopTitle(R.string.choose_file_method)
              .configureView(new LovelyCustomDialog.ViewConfigurator() {
                    @Override
                    public void configureView(View v) {

                        String state = Environment.getExternalStorageState();
                        boolean extCardAvailable = (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));

                        class Item {
                            public String name;
                            public int action;
                            public Item(String name, int action) {
                                this.name = name;
                                this.action = action;
                            }
                        }
                        class Items extends ArrayList<Item> {
                            public CharSequence[] toArray() {
                                String[] arr = new String[this.size()];
                                int j = 0;
                                for (Item i : this) {
                                    arr[j++] = i.name;
                                }
                                return arr;
                            }
                        }
                        final Items items = new Items();
                        //czy mamy token dropboxa?
                        if (DropboxManager.hasToken(WorkspaceFragment.this.getActivity()))
                            items.add(new Item(getString(R.string.open_file_dropbox), R.id.action_open_file_dropbox));
                        items.add(new Item(getString(R.string.open_file_dropbox_new_session), R.id.action_open_file_dropbox_new_session));
                        items.add(new Item(getString(R.string.open_file_internal_browser), R.id.action_open_file_internal_browser));
                        //czy włożona karta sd?
                        if (extCardAvailable)
                            items.add(new Item(getString(R.string.open_file_internal_browser_sdcard), R.id.action_open_file_internal_browser_sdcard));

                        /*
                        CharSequence[] items = new String[extCardAvailable?4:3];
                        items[0] = getString(R.string.open_file_dropbox);
                        items[1] = getString(R.string.open_file_dropbox_new_session);
                        items[2] = getString(R.string.open_file_internal_browser);
                        if (extCardAvailable)
                            items[3] = getString(R.string.open_file_internal_browser_sdcard);
                            */

                        ListView list = (ListView)v.findViewById(R.id.list);

                        list.setAdapter(new ArrayAdapter<CharSequence>(
                                WorkspaceFragment.this.getContext(),
                                android.R.layout.simple_list_item_1,
                                items.toArray()));

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                dialog.dismiss();
                                int action = items.get(position).action;

                                switch (action) {
                                    /*
                                    dropbox
                                     */
                                    case R.id.action_open_file_dropbox:
                                        WorkspaceFragment.this.startActivityForResult(IntentFactory.createOpenFileIntent(WorkspaceFragment.this.getActivity(), "", "dropbox"), WorkspaceFragment.REQUEST_OPEN_FILE);
                                        break;
                                    case R.id.action_open_file_dropbox_new_session:
                                        WorkspaceFragment.this.startActivityForResult(IntentFactory.createOpenFileIntent(WorkspaceFragment.this.getActivity(), "", "dropbox_new_session"), WorkspaceFragment.REQUEST_OPEN_FILE);
                                        break;

                                    /*
                                    lokalny system
                                     */
                                    case R.id.action_open_file_internal_browser:
                                    case R.id.action_open_file_internal_browser_sdcard:
                                        SharedPreferences prefs = getActivity().getPreferences(0);

                                        String lastPath = prefs.getString("lastOpenPath", Environment.getExternalStorageDirectory().getAbsolutePath());
                                        if (lastPath.equals("/android_asset") || lastPath.equals("/android_asset/"))
                                            lastPath = Environment.getExternalStorageDirectory().getAbsolutePath();

                                        WorkspaceFragment.this.startActivityForResult(IntentFactory.createOpenFileIntent(
                                                WorkspaceFragment.this.getActivity(),
                                                lastPath,
                                                (action==R.id.action_open_file_internal_browser)?"file":"file_ext"
                                            ), WorkspaceFragment.REQUEST_OPEN_FILE
                                        );
                                        break;
                                }

                            }
                        });

                    }
                })
                .setIcon(R.drawable.ic_action_collection);

        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();

    }

    private void setNewProject(String projectName) {
        partsManager = new PartsManager();
        this.projectName = projectName;
        this.deselectPart();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH && resultCode == -1 && data != null) {

            this.selectPart(partsManager.selectByName(
                data.getStringExtra("Part")
            ));

            Toast.makeText(this.getActivity(), "Part: "+data.getStringExtra("Part"), Toast.LENGTH_SHORT).show();
            menuDeselect.setVisible(true);
        }

        if (requestCode == REQUEST_OPEN_FILE && resultCode == -1 && data != null) {

            String filePath = data.getStringExtra("path");
            String compFilePath = data.getStringExtra("compPath");

            final String extension = FilenameHelpers.getExtension(filePath);
            String newProjectName = FilenameHelpers.stripExtension(filePath);
            if (!newProjectName.equals(projectName)) {
                setNewProject(newProjectName);
            }

            String[] str = FilenameHelpers.stripExtension(filePath).split(":");
            final RecentlyOpened.RecentlyOpenedItem recentlyOpened = RecentlyOpened.add(str[1], str[0]);
            switch (extension) {
                case "brd":
                    loadBoardFile(filePath);
                    recentlyOpened.update(EagleDataSource.TYPE.Board);
                    break;
                case "sch":
                    loadSchematicFile(filePath);
                    recentlyOpened.update(EagleDataSource.TYPE.Schematic);
                    break;
            }

            if (compFilePath != null) {
                final View v = getView().findViewById(R.id.eagle_view1);
                final String snackSecondFile = compFilePath;
                Snackbar snackbar = Snackbar
                        .make(v, "Czy chcesz wczytać także "+snackSecondFile, Snackbar.LENGTH_LONG)
                        .setAction("OPEN", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                switch (extension) {
                                    case "brd":
                                        loadSchematicFile(snackSecondFile);
                                        recentlyOpened.update(EagleDataSource.TYPE.Schematic);
                                        break;
                                    case "sch":
                                        loadBoardFile(snackSecondFile);
                                        recentlyOpened.update(EagleDataSource.TYPE.Board);
                                        break;
                                }

                            }
                        });

                snackbar.show();
            }
        }
    }

    //private LovelySaveStateHandler saveStateHandler;

    private void setLayers() {
        if (activeEagleView == null)
            return;

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layer, null, false);

        //saveStateHandler = new LovelySaveStateHandler();
        final CustomDialog dialog = (CustomDialog)new CustomDialog(this.getContext())
                .setView(dialogView)
                //.setTopColorRes(R.color.black)
                .setTopTitle("Select visible layers")
                //.setTopTitleColor(R.color.white)
                .setIcon(R.drawable.ic_action_add_to_queue);


        /*

        //View btnSelectAll = dialogView.findViewById(R.id.btn_check_all);
        //View btnSelectNone = dialogView.findViewById(R.id.btn_check_none);

        toolbar.inflateMenu(R.menu.layers_dialog);
*/

        Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.toolbar2);

        final ArrayList<Layer> allLayers = new ArrayList<>();
        for (Map.Entry<Integer, Layer> it : activeEagleView.getDataSource().getLayers().getLayers() .entrySet()) {
            if (!it.getValue().isEmpty()) {
                allLayers.add(it.getValue());
            }
        }

        Collections.sort(allLayers, new Comparator<Layer>(){
            public int compare(Layer obj1, Layer obj2) {
                return obj1.getNumber() - obj2.getNumber();
            }
        });

        //Arrays.sort(allLayers);
        final ListView layerList = (ListView) dialogView.findViewById(R.id.lv_layers);
        final LayerAdapter adapter = new LayerAdapter(getActivity(), allLayers);
        layerList.setAdapter(adapter);
        for (int i = 0; i < allLayers.size(); i += OPEN_BOARD) {
            layerList.setItemChecked(i, allLayers.get(i).isShown());
        }

        toolbar.inflateMenu(R.menu.layers_dialog);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_select_all:
                        for (int i = 0; i < allLayers.size(); i += WorkspaceFragment.OPEN_BOARD) {
                            layerList.setItemChecked(i, true);
                        }
                        break;
                    case R.id.action_select_none:
                        for (int i = 0; i < allLayers.size(); i += WorkspaceFragment.OPEN_BOARD) {
                            layerList.setItemChecked(i, false);
                        }
                        break;
                }
                return false;
            }
        });

        dialog.setPositiveButton(R.string.OK, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int p;
                SparseBooleanArray checkedItems = layerList.getCheckedItemPositions();
                for (p = 0; p < adapter.getCount(); p += WorkspaceFragment.OPEN_BOARD) {
                    ((Layer)adapter.getItem(p)).setVisible(false);
                }
                for (p = 0; p < checkedItems.size(); p += WorkspaceFragment.OPEN_BOARD) {
                    ((Layer)adapter.getItem(checkedItems.keyAt(p))).setVisible(checkedItems.valueAt(p));
                }
                //WorkspaceFragment.this.redrawLayers(WorkspaceFragment.this.zoomImageView.getMatrix());
                WorkspaceFragment.this.activeEagleView.redraw();
                dialog.dismiss();


            }
        });

        dialog.show();
    }

    private void openFileDemo(String fileName, String path) {
        openedPath = path;
        openedFileName = fileName;
        if (fileName.startsWith("~")) { //pliki demo zaznaczone przez ~
            fileName = fileName.substring(1);
        }
        isBrd = fileName.toLowerCase().endsWith(".brd");

        getActivity().getPreferences(0).edit().
                putString("lastOpenPath", openedPath).
                putString("lastOpenName", openedFileName).
                apply();

        //setBackground();
        zoomToFit();
        makeText(getActivity(), "DEMO: " + fileName, LENGTH_LONG).show();
    }

    private void zoomToFit() {
        if (activeEagleView != null) {
            activeEagleView.zoomToFit();
        }
    }

}
