package com.reconinstruments.ui.carousel;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.reconinstruments.ui.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * ViewPager adapter class for CarouselItemFragments
 */
public class CarouselPagerViewAdapter extends PagerAdapter {

    LayoutInflater inflater;

    Context context;
    List<? extends CarouselItem> items;
    View[] views;
    CarouselViewPager pager;

    public CarouselPagerViewAdapter(Context context,List<? extends CarouselItem> items,CarouselViewPager pager) {
        this.context = context;
        this.items = items;
        this.pager = pager;
        inflater = LayoutInflater.from(context);
        views = new View[items.size()];
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = getView(position);
        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==((View)object);
    }

    public View getView(int position) {
        View view = views[position];
        if(view==null) {
            CarouselItem carouselItem = items.get(position);

            View innerView = inflater.inflate(carouselItem.getLayoutId(), null);
            carouselItem.updateView(innerView);

            /*pager.setClipChildren(false);
            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setClipChildren(false);
            relativeLayout.setClipToPadding(false);
            relativeLayout.addView(innerView);*/
            //FrameLayout frameLayout = new FrameLayout(context);
            /*frameLayout.setClipChildren(false);
            frameLayout.setClipToPadding(false);
            frameLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);*/
            //frameLayout.addView(innerView);
            /*FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if(position==0) {
                //lp.leftMargin = getViewMargin(view);
                //lp.rightMargin = -lp.leftMargin;
            }
            view.setLayoutParams(lp);*/
            //view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            /*if(position-1>0)
            {
                View prevView = views[position];
                pager.setPageMargin();

                int viewWidth = view.getMeasuredWidth();
            }*/

            //view.setBackgroundColor(0xFF0000);
            //view.setPadding(50,0,50,0);

            /*if(position==0)
                offset = getViewMargin(innerView);
            //innerView.setTranslationX(offset);
            innerView.invalidate();
            innerView.requestLayout();

            relativeLayout.invalidate();
            relativeLayout.requestLayout();

            pager.invalidate();*/

            //view = frameLayout;
            view = innerView;
            view.setTag(position);
            views[position] = view;
        }
        return view;
    }

    int offset;

    public int getViewMargin(View view) {
        int pagerWidth = UIUtils.JET_SCREEN_WIDTH;//pager.getMeasuredWidth();
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int viewWidth = view.getMeasuredWidth();
        int margin = (pagerWidth-viewWidth)/2;
        //Log.d("TAG"," width "+view.getMeasuredWidth()+ " pager width "+pagerWidth+" margin "+margin);
        return margin;
    }

    public CarouselItem getCarouselItem(int position) {
        return items.get(position);
    }

    public void updateViewForPosition(int position, CarouselItem.POSITION rel_position) {

        View view = getView(position);
        CarouselItem carouselItem = items.get(position);

        if(rel_position==CarouselItem.POSITION.CENTER)
            view.setSelected(true);
        else
            view.setSelected(false);

        carouselItem.updateViewForPosition(view, rel_position);
    }


    @Override
    public float getPageWidth(int position) {
        int pagerWidth = pager.getMeasuredWidth();

        View view = getView(position);
        int viewWidth = view.getMeasuredWidth();
        // if view hasn't been measured yet
        //if(viewWidth==0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            viewWidth = view.getMeasuredWidth();
        //}
        // account for rounding errors that might clip text
        viewWidth += 1;

        float ratio = ((float)viewWidth/(float)pagerWidth);
        return ratio;
    }
}
