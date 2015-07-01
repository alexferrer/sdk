package com.reconinstruments.ui.list;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.reconinstruments.ui.R;

/**
 * StandardListItem with a toggleable checkbox in the 'subicon' position
 */
public class ToggleListItem extends StandardListItem {
    boolean enabled;
    public ToggleListItem(String text,boolean enabled){
        super(text);
        this.enabled = enabled;
    }
    @Override
    public void onClick(Context context) {
        setEnabled(!enabled);
        onToggle(enabled);
    }
    @Override
    public Integer getSubIconId() {
        return enabled ? R.drawable.selectable_checkbox_enabled:R.drawable.selectable_checkbox_disabled;
    }

    public void onToggle(boolean enabled) {}

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        View view = getView();
        // update view in place if already created
        if(view!=null) {
            ImageView subIcon = (ImageView) getView().findViewById(R.id.subicon);
            subIcon.setImageResource(getSubIconId());
        }
    }
}