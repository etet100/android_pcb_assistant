package bts.pcbassistant.layers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;

import bts.pcbassistant.R;

public class LayerListItem extends RelativeLayout implements Checkable {
    private CheckedTextView checkedTextView;

    public LayerListItem(Context context) {
        super(context);
        this.checkedTextView = null;
        init();
    }

    public LayerListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.checkedTextView = null;
        init();
    }

    public LayerListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.checkedTextView = null;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_layer, this, true);
        this.checkedTextView = (CheckedTextView)findViewById(R.id.text1);
/*
        this.checkedTextView = (BabushkaCheckedText)findViewById(R.id.text1);
        this.checkedTextView.addPiece(new BabushkaCheckedText.Piece.Builder("Central Park, NY\n")
                .textColor(Color.parseColor("#414141"))
                .build());
        this.checkedTextView.display();
*/
        int padding = getResources().getDimensionPixelSize(R.dimen.padding);
        setPadding(padding, padding, padding, padding);
    }

    public void setChecked(boolean b) {
        this.checkedTextView.setChecked(b);
        invalidate();
    }

    public boolean isChecked() {
        return this.checkedTextView.isChecked();
    }

    public void toggle() {
        this.checkedTextView.toggle();
        invalidate();
    }
}
