package bts.pcbassistant.layers;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

import bts.pcbassistant.R;
import bts.pcbassistant.drawing.Layer;

public class LayerAdapter extends BaseAdapter {
    private Context ctx;
    private List<Layer> layers;

    public LayerAdapter(Context ctx, List<Layer> data) {
        this.layers = new ArrayList<>(data);
        this.ctx = ctx;
    }

    public int getCount() {
        return this.layers.size();
    }

    public Object getItem(int i) {
        return this.layers.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new LayerListItem(this.ctx);
        }
        TriangleView ivColor = (TriangleView)view.findViewById(R.id.iv_layer_color);
        CheckedTextView checkedTextView = (CheckedTextView)view.findViewById(R.id.text1);
        Layer current = this.layers.get(i);
        ivColor.setColor(current.getColor());
        if (Build.VERSION.SDK_INT < 24) {
            checkedTextView.setText(Html.fromHtml(String.format("%s <font color='#AAAAAA'>(%s)</font>", current.getName(), current.getNumber())));
        } else {
            checkedTextView.setText(Html.fromHtml(String.format("%s <font color='#AAAAAA'>(%s)</font>", current.getName(), current.getNumber()), Html.FROM_HTML_MODE_LEGACY));
        }
        //checkedTextView.setText(current.getNumber() + ": " + current.getName());
        view.setTag(current);
        return view;
    }
}
