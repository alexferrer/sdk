package com.reconinstruments.ui.examples.carousel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.reconinstruments.ui.carousel.*;
import com.reconinstruments.ui.examples.R;
import com.reconinstruments.ui.examples.util.MultiToast;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Demonstrates the different events that can be received by carousels and carousel items
 */
public class CarouselEvents extends CarouselActivity {

    MultiToast toast;

    class CarouselListenerItem extends StandardCarouselItem {
        public CarouselListenerItem(String title) {
            super(title);
        }
        @Override
        public void onClick(Context context) {
            toast.addMessage("Item "+getPosition()+": clicked");
        }

        @Override
        public void onSelected(Context context) {
            toast.addMessage("Item "+getPosition()+": selected");
        }

        @Override
        public void updateViewForPosition(View view, POSITION relativePosition) {
            toast.addMessage("Item "+getPosition()+": "+relativePosition.name());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toast = new MultiToast(this);

        setContentView(R.layout.carousel_host);
        getCarousel().setPageMargin(30);
        getCarousel().addOnPageSelectListener(new CarouselViewPager.OnPageSelectListener() {
            @Override
            public void onPageSelected(CarouselItem item, int position) {
                toast.addMessage("Carousel: "+position+" selected");
            }
        });

        getCarousel().setContents(
                new CarouselListenerItem("Item 1"),
                new CarouselListenerItem("Item 2"),
                new CarouselListenerItem("Item 3"),
                new CarouselListenerItem("Item 4"),
                new CarouselListenerItem("Item 5"));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN)
            getCarousel().setCurrentItem(0);

        return super.onKeyUp(keyCode, event);
    }
}
