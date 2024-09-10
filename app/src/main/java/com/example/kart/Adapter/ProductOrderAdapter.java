package com.example.kart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Model.CartModel;
import com.example.kart.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ProductViewHolder> {

    private Context context;
    private List<CartModel> productList;


    public ProductOrderAdapter(Context context, List<CartModel> productList) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_order_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CartModel product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.salePrice.setText("₹ " +product.getPrice());

        Picasso.get().load(product.getImage()).into(holder.image);

        holder.qty.setText("QTY: "+product.getQty());

    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, salePrice, qty;
        ShapeableImageView image;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            salePrice = itemView.findViewById(R.id.sale_price);
            image = itemView.findViewById(R.id.post_image);
            qty = itemView.findViewById(R.id.qty);

        }
    }
}
