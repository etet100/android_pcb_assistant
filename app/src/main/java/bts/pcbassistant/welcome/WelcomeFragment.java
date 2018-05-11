package bts.pcbassistant.welcome;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.github.atzcx.appverupdater.AppVerUpdater;
import com.suredigit.inappfeedback.FeedbackDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import bts.pcbassistant.IntentFactory;
import bts.pcbassistant.R;
import bts.pcbassistant.StartActivity;
import bts.pcbassistant.WorkspaceActivity_;
import bts.pcbassistant.about.AboutActivity_;

/**
 * Created by And on 2017-05-03.
 */

@EFragment(R.layout.fragment_welcome)
public class WelcomeFragment extends Fragment implements com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener {

    private class RecentListAdapter extends BaseAdapter {

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return RecentlyOpened.getCount();
        }

        @Override
        public Object getItem(int position) {
            return "aaaaaa";
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater(null).inflate(
                    R.layout.list_item_recent,
                    null
            );
            RecentlyOpened.RecentlyOpenedItem rec = RecentlyOpened.getItem(position);
            if (rec != null) {
                ((TextView) v.findViewById(R.id.name)).setText(rec.name);
                ((TextView) v.findViewById(R.id.details)).setText(rec.getDetails());
                ((ImageView) v.findViewById(R.id.icon_board)).setVisibility(rec.board == true?View.VISIBLE:View.GONE);
                ((ImageView) v.findViewById(R.id.icon_schematic)).setVisibility(rec.schematic == true?View.VISIBLE:View.GONE);
            }
            return v;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    @ViewById(R.id.recent)
    ListView recentFiles;

    @ViewById(R.id.button_open)
    Button openFile;

    @ViewById(R.id.button_about)
    Button about;

    @ViewById(R.id.examples)
    SliderLayout examples;

    FeedbackDialog feedbackDialog;
    AppVerUpdater appVerUpdater;

    @Click(R.id.button_feedback)
    void openFeedback() {
        feedbackDialog.show();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        //sprawdź dodatkowo licencje
        if (!verifyLicense())
            return;

        String name = slider.getBundle().getString("name");
        Integer demo = slider.getBundle().getInt("extra");
        String filePath = "";
        switch (demo) {
            case R.drawable.ex_arduino:
                filePath = "asset:arduinoboard";
                break;
            case R.drawable.ex_msp:
                filePath = "asset:MSP-EXP430F5529LP";
                break;
            case R.drawable.ex_demo3:
                filePath = "asset:demo3";
                break;
        }
        /*
            w przypadku demo zawse płytka i schemat
         */
        final Intent intent = IntentFactory.openWorkspaceWithFileIntent(
                this.getActivity(),
                filePath,
                true,
                true
        );

        Snackbar snackbar = Snackbar
                .make(openFile, "Otworzyć demo `"+name+"`?", Snackbar.LENGTH_LONG)
                .setAction("OPEN", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WelcomeFragment.this.getActivity().startActivity(intent);
                    }
                });
        snackbar.show();
    }

    @AfterViews
    void init() {

        RecentlyOpened.init(
                this.getContext()
        );
        recentFiles.setAdapter(new RecentListAdapter());

        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sprawdź dodatkowo licencje
                if (!verifyLicense())
                    return;

                Intent intent = new Intent(WelcomeFragment.this.getActivity(), WorkspaceActivity_.class);
                intent.putExtra("action", "openSelectFileDialog");
                WelcomeFragment.this.getActivity().startActivity(intent);

                //((StartActivity)WelcomeFragment.this.getActivity()).openMainFragment();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WelcomeFragment.this.getActivity(), AboutActivity_.class);
                WelcomeFragment.this.getActivity().startActivity(intent);

            }
        });

        recentFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //sprawdź dodatkowo licencje
                if (!verifyLicense())
                    return;

                RecentlyOpened.RecentlyOpenedItem rec = RecentlyOpened.getItem(position);
                Intent intent = IntentFactory.openWorkspaceWithFileIntent(
                        WelcomeFragment.this.getActivity(),
                        rec.getPath(),
                        rec.board,
                        rec.schematic
                );

                WelcomeFragment.this.getActivity().startActivity(intent);

            }
        });

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Arduino Mega 2560", R.drawable.ex_arduino);
        file_maps.put("TI-Launchpad",      R.drawable.ex_msp);
        file_maps.put("Simple board",      R.drawable.ex_demo3);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this.getContext());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putInt("extra", file_maps.get(name));
            textSliderView.getBundle()
                    .putString("name", name);

            examples.addSlider(textSliderView);

            ImageView iv = (ImageView)textSliderView.getView().findViewById(com.daimajia.slider.library.R.id.daimajia_slider_image);
            iv.setScaleX(0.9f);
            iv.setScaleY(0.9f);
        }
        examples.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        examples.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        examples.setCustomAnimation(new DescriptionAnimation());
        examples.setDuration(4000);

        feedbackDialog = new FeedbackDialog(this.getContext(), "<key>");

        appVerUpdater = new AppVerUpdater(this.getContext())
                .setUpdateJSONUrl("<url>")
                .setShowNotUpdated(false)
                .setViewNotes(true)
                .build();
    }

    public void updateRecentList() {
        recentFiles.setAdapter(new RecentListAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                this.getActivity().finish();
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.welcome, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public boolean verifyLicense() {
        return ((StartActivity)this.getActivity()).verifyLicense();
    }
}