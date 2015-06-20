package com.reconinstruments.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by chris on 19/06/15.
 */
public class ReconListView extends ListView {

    public ReconListView(Context context) {
        super(context);
        init();
    }

    public ReconListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int selectedItemPos = getSelectedItemPosition();
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if(selectedItemPos==(getAdapter().getCount() - 1)) {
                smoothScrollToPosition(0);
                setSelection(0);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (selectedItemPos == 0) {
                smoothScrollToPosition(getAdapter().getCount() - 1);
                setSelection(getAdapter().getCount() - 1);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
