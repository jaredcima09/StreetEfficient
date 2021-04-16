package com.capstone.streetefficient.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DocumentsAdapter extends PagerAdapter {


    ArrayList<String> documents;
    Context context;

    public DocumentsAdapter(ArrayList<String> documents, Context context) {
        this.documents = documents;
        this.context = context;
    }

    @Override
    public int getCount() {
        return documents.size();
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        Picasso.get()
                .load(documents.get(position))
                .fit()
                .centerInside()
                .into(imageView);

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
