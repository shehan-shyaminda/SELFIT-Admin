package com.codelabs.selfit_admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.codelabs.selfit_admin.R;

public class OnboardSliderAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;

    public OnboardSliderAdapter(Context context){
        this.context = context;
    }

    public String[] slide_desc = {
            "MEET YOUR COACH,\nSTART YOUR JOURNEY",
            "CREATE A WORKOUT PLAN\nTO STAY FIT",
            "ACTION IS THE\nKEY TO ALL SUCCESS"
    };

    public int[] slide_images = {
            R.drawable.slide_001,
            R.drawable.slide_002,
            R.drawable.slide_003
    };

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboard_row_layout,container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.img_slideImg);
        TextView slideDesc = (TextView) view.findViewById(R.id.txt_slideDesc);

        slideImageView.setImageResource(slide_images[position]);
        slideDesc.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}