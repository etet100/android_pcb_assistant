package bts.pcbassistant.welcome;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import bts.pcbassistant.R;
import bts.pcbassistant.WorkspaceActivity_;

/**
 * Created by a on 2017-05-04.
 */

@EFragment(R.layout.fragment_example_project)
public class ExampleFragment extends Fragment {

    @FragmentArg("title")
    String title;

    @FragmentArg("image")
    String imageId;

    @FragmentArg("assetName")
    String assetName;

    @ViewById(R.id.text)
    TextView text;

    @ViewById(R.id.image)
    ImageView iv;


    @AfterViews
    void init()
    {
        iv.setImageResource(Integer.parseInt(imageId));
        text.setText(title);

        this.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ExampleFragment.this.getActivity(), WorkspaceActivity_.class);
                intent.putExtra("demo", assetName);
                ExampleFragment.this.getActivity().startActivity(intent);

                //((StartActivity)(ExampleFragment.this.getActivity())).openExample(assetName);
            }
        });
    }

    public ExampleFragment() {
        super();
    }
}
