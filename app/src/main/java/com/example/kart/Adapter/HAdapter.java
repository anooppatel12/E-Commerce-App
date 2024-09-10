package com.example.kart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.kart.Model.Product;
import com.example.kart.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class HAdapter extends PagerAdapter {
    private Context context;
    private List<Product> productList;

    public HAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_layout, container, false);

        Product product = productList.get(position);

        ShapeableImageView imageView = view.findViewById(R.id.post_image);
        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);

        Picasso.get().load(product.getImage()).into(imageView);
        title.setText(product.getTitle());
        description.setText(product.getDescription());


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
