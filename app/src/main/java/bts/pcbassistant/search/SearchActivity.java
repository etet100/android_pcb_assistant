package bts.pcbassistant.search;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.List;

import bts.pcbassistant.R;
import bts.pcbassistant.data.Part;
import bts.pcbassistant.data.PartsManager;
import bts.pcbassistant.dialog.CustomDialogActivity;

@EActivity
public class SearchActivity extends CustomDialogActivity {

    private class SearchListAdapter implements ListAdapter {

        final public ArrayList<Part> list;

        public SearchListAdapter(List<Part> parts, String search) {
            super();

            list = new ArrayList<>();

            if (search != null)
                search = search.toLowerCase();
            for (Part p : parts) {
                if (search == null || p.getName().toLowerCase().startsWith(search)) {
                    list.add(p);
                }
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
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
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).getName();
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
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_item_search, null);
            } else {
              //  holder = (ViewHolder)convertView.getTag();
            }
            Part part = list.get(position);
            TextView text = (TextView)convertView.findViewById(R.id.text1);
            if (Build.VERSION.SDK_INT < 24) {
                text.setText(Html.fromHtml(String.format("%s <font color='#AAAAAA'>(%s)</font>", part.getName(), part.getLibrary())));
            } else {
                text.setText(Html.fromHtml(String.format("%s <font color='#AAAAAA'>(%s)</font>", part.getName(), part.getLibrary()), Html.FROM_HTML_MODE_LEGACY));
            }
            //            ((TextView)convertView.findViewById(R.id.text1)).setText(
            //                    list.get(position).getName() + "("+ list.get(position).getLibrary() +")");
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
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

    //statycznie żeby uniknać problemów z przesyłaniem tablic między
    //activities
    public static PartsManager partsManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //View dialogView = getLayoutInflater().inflate(R.layout.activity_search, null, false);

        setView(R.layout.activity_search);
        //setTopColorRes(R.color.colorDialogTop);
        setTopTitle("Search part list");
        setIcon(R.drawable.ic_action_search);

        setPositiveButton(R.string.OK, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchActivity.this.finish();

            }
        });


        //setTitle("Search...");

        ListView listView = (ListView)findViewById(R.id.list);

        listView.setAdapter(new SearchListAdapter(
                partsManager.getParts(),
                null
        ));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent resultData = new Intent();
                resultData.putExtra("Part", ((SearchListAdapter)parent.getAdapter()).list.get(position).getName());
                resultData.setData(Uri.EMPTY);

                partsManager.selectByName(
                        ((SearchListAdapter)parent.getAdapter()).list.get(position).getName()
                );

                SearchActivity.this.setResult(-1, resultData);
                SearchActivity.this.finish();
            }
        });

        ((EditText)findViewById(R.id.edit)).addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ((ListView)findViewById(R.id.list)).setAdapter(new SearchListAdapter(
                        partsManager.getParts(),
                        s.toString()
                ));

            }
        });
    }


}
