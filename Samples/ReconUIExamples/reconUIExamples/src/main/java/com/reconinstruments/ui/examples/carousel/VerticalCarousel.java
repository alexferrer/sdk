package com.reconinstruments.ui.examples.carousel;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.reconinstruments.ui.carousel.CarouselActivity;
import com.reconinstruments.ui.carousel.CarouselItem;
import com.reconinstruments.ui.carousel.StandardCarouselItem;
import com.reconinstruments.ui.examples.R;

public class VerticalCarousel extends CarouselActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carousel_host_vertical);

        getCarousel().setContents(
                new StandardCarouselItem("1"),
                new StandardCarouselItem("2"),
                new StandardCarouselItem("3"),
                new StandardCarouselItem("4"));
    }
}