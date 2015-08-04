package com.reconinstruments.ui.examples.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.reconinstruments.ui.examples.R;
import com.reconinstruments.ui.list.SimpleListActivity;
import com.reconinstruments.ui.list.SimpleListItem;
import com.reconinstruments.ui.list.StandardListItem;

import java.util.ArrayList;

public class DynamicList extends SimpleListActivity {

    StandardListItem addItem;
    ArrayList<SimpleListItem> listItems;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_standard_layout);

        addItem = new StandardListItem("Add item") {
            @Override
            public void onClick(Context context) {
                addListItem();
            }
        };
        listItems = new ArrayList<SimpleListItem>();
        listItems.add(addItem);
        setContents(listItems);
    }

    static int numItemsAdded = 1;
    public class RemovableListItem extends StandardListItem {
        public RemovableListItem() {
            super("Item "+numItemsAdded);
            numItemsAdded++;
        }
        @Override
        public void onClick(Context context) {
            listItems.remove(this);
            updateContents(listItems);
        }
    }

    public void addListItem() {
        listItems.add(new RemovableListItem());
        updateContents(listItems);
    }
}