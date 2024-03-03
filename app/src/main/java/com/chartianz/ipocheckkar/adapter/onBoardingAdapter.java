package com.chartianz.ipocheckkar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.chartianz.ipocheckkar.R;

import java.util.Objects;

public class onBoardingAdapter extends PagerAdapter {

    Context context;
    int[] images;
    String[] subtitle;
    LayoutInflater mLayoutInflater;

    public onBoardingAdapter(Context context, int[] images, String[] subtitle) {
        this.context = context;
        this.images = images;
        this.subtitle = subtitle;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.onboarding_image_layout, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageViewMain);
        TextView onboarding_subtitle= itemView.findViewById(R.id.onboarding_subtitle);
        imageView.setImageResource(images[position]);
        onboarding_subtitle.setText(subtitle[position]);
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }

}
