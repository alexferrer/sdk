package com.reconinstruments.ui.examples.list;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.reconinstruments.ui.examples.R;
import com.reconinstruments.ui.list.SimpleListActivity;
import com.reconinstruments.ui.list.SimpleListItem;
import com.reconinstruments.ui.list.StandardListItem;
import com.reconinstruments.ui.list.ToggleListItem;

import java.util.Arrays;
import java.util.List;


public class ListWithInfoActivity extends SimpleListActivity {

    TextView infoTV;

    public SimpleListItem getToggleListInfoItem(String text,boolean enabled, final String infoText) {
        return new ToggleListItem(text, enabled) {
            @Override
            public void onSelected(Context context) {
                infoTV.setText(infoText);
            }
        };
    }
    public SimpleListItem getDefaultListInfoItem(String text,String subText, final String infoText) {
        return new StandardListItem(text, subText) {
            @Override
            public void onSelected(Context context) {
                infoTV.setText(infoText);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_with_info);
        setContents(
                getToggleListInfoItem("GPS Always On",true,"GPS Always On"),
                getDefaultListInfoItem("Record Length","15 sec","Video Camera Record Length"),
                getDefaultListInfoItem("Battery Level","95%","Battery Level")
        );
        infoTV = (TextView) findViewById(R.id.info_text);
    }

}